package org.dromara.xhs.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.sse.dto.SseMessageDto;
import org.dromara.common.sse.utils.SseMessageUtils;
import org.dromara.xhs.domain.XhsShareNote;
import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.xhs.domain.vo.XhsShareNoteVo;
import org.dromara.xhs.mapper.XhsShareNoteMapper;
import org.dromara.xhs.service.IXhsShareNoteService;
import org.dromara.xhs.util.extract.core.LinkExtractor;
import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;
import org.dromara.xhs.util.extract.model.PlatformEnum;
import org.dromara.xhs.util.note.ApiResponse;
import org.dromara.xhs.util.note.ApiService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;
import static org.dromara.common.core.utils.ServiceExceptionUtil.exception;
import static org.dromara.constant.ErrorCodeConstant.INVALIDATE_SHARE_URL;
import static org.dromara.constant.ErrorCodeConstant.XHS_SHARE_EXIST;

/**
 * 小红书笔记链接分享Service业务层处理
 *
 * @author ZRL
 * @date 2025-08-15
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class XhsShareNoteServiceImpl implements IXhsShareNoteService {

    private final XhsShareNoteMapper baseMapper;

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 查询小红书笔记链接分享
     *
     * @param id 主键
     * @return 小红书笔记链接分享
     */
    @Override
    public XhsShareNoteVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询小红书笔记链接分享列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 小红书笔记链接分享分页列表
     */
    @Override
    public TableDataInfo<XhsShareNoteVo> queryPageList(XhsShareNoteBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<XhsShareNote> lqw = buildQueryWrapper(bo);
        Page<XhsShareNoteVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的小红书笔记链接分享列表
     *
     * @param bo 查询条件
     * @return 小红书笔记链接分享列表
     */
    @Override
    public List<XhsShareNoteVo> queryList(XhsShareNoteBo bo) {
        LambdaQueryWrapper<XhsShareNote> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<XhsShareNote> buildQueryWrapper(XhsShareNoteBo bo) {
        LambdaQueryWrapper<XhsShareNote> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(XhsShareNote::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getShareUrl()), XhsShareNote::getShareUrl, bo.getShareUrl());
        return lqw;
    }

    /**
     * 新增小红书笔记链接分享
     *
     * @param bo 小红书笔记链接分享
     * @return 是否新增成功
     */
    @Override
    public Boolean insertXhsShareNote(XhsShareNoteBo bo) {
        XhsShareNote add = MapstructUtils.convert(bo, XhsShareNote.class);
        // 新增时设置创建时间、创建人等
        String username = "分享填入";
        LoginUser loginUser = LoginHelper.getLoginUser();
        Long userId;
        if (loginUser != null) {
            username = loginUser.getUsername();
            userId = loginUser.getUserId();
        } else {
            userId = null;
            log.info("无法获取登录用户信息，此处操作为分享后填入");
        }
        validEntityBeforeSave(add);
        add.setCreateBy(username);

        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 使用线程池 处理异步获取笔记信息
            CompletableFuture.runAsync(() -> {
                log.info("开始处理笔记信息");
                XhsShareNote shareNote = baseMapper.selectById(bo.getId());
                ApiResponse apiResponse = ApiService.parseUrl(shareNote.getShareUrl());

                if (apiResponse.isSuccess()) {
                    shareNote.setTitle(apiResponse.getTitle());
                    shareNote.setUsername(apiResponse.getNickName());
                    shareNote.setCover(apiResponse.getCoverImg());
                    shareNote.setAvatar(apiResponse.getNickPic());
                    Long pushTime = apiResponse.getPushTime();
                    LocalDateTime localDateTime = DateUtil.date(pushTime).toLocalDateTime();
                    shareNote.setPostTime(localDateTime);
                    Integer likeNum = Integer.valueOf(apiResponse.getLikeNum());
                    Integer commentNum = Integer.valueOf(apiResponse.getCommentNum());
                    Integer collectNum = Integer.valueOf(apiResponse.getCollectNum());
                    shareNote.setInteraction(likeNum + commentNum + collectNum);
                    Db.updateById(shareNote);
                    log.info("笔记信息处理成功");


                    if (userId != null) {
                        SseMessageDto dto = new SseMessageDto();
                        dto.setMessage("您加入的笔记：" + shareNote.getNoteId() + "，获取笔记互动数据成功；请刷新页面查看");
                        dto.setUserIds(List.of(userId));
                        SseMessageUtils.publishMessage(dto);
                    }
                } else {
                    log.info("笔记信息获取失败，接口获取异常");
                }
            });
        }
        return flag;
    }

    /**
     * 修改小红书笔记链接分享
     *
     * @param bo 小红书笔记链接分享
     * @return 是否修改成功
     */
    @SneakyThrows
    @Override
    public Boolean updateXhsShareNote(XhsShareNoteBo bo) {
        XhsShareNote update = MapstructUtils.convert(bo, XhsShareNote.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(XhsShareNote entity) {
        //TODO 做一些数据校验,如唯一约束
        if (entity == null) {
            throw new ServiceException("数据不能为空");
        }

        // 指定解析器
        try {
            LinkParseResult linkParseResult = LinkExtractor
                .with(PlatformEnum.XIAO_HONG_SHU)
                .parse(entity.getShareUrl());

            // 设置解析结果
            entity.setNoteId(linkParseResult.getContentId());
            entity.setUrlType(linkParseResult.getLinkType().name());
            entity.setShareUrl(linkParseResult.getUrl());
            entity.setInteraction(0);
            entity.setPreInteraction(0);
        } catch (LinkParseException e) {
            log.error("解析链接失败：{}", e.getMessage());
            throw exception(INVALIDATE_SHARE_URL, e.getMessage());
        }

        // 查询笔记唯一性
        LambdaQueryWrapper<XhsShareNote> wrapper =
            Wrappers.lambdaQuery(XhsShareNote.class).eq(XhsShareNote::getNoteId, entity.getNoteId());
        XhsShareNote one = Db.getOne(wrapper);
        if (one != null) {
            throw exception(XHS_SHARE_EXIST);
        }
    }

    /**
     * 校验并批量删除小红书笔记链接分享信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量导入产品信息数据
     * @param xhsShareNoteVoList 产品信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importXhsShareNote(List<XhsShareNoteBo> xhsShareNoteVoList, boolean updateSupport, String operatorName){
        return ImportEntities(
            xhsShareNoteVoList,
            updateSupport,
            operatorName,
            this::importXhsShareNote,
            // TODO 自定义名称
            item -> Opt.ofNullable(item.getShareUrl()).orElseGet(()->"")
        );
    }

    /**
     * 导入产品信息信息
     * @param shareNoteBo 产品信息信息
     * @param operatorName 操作人
     */
    private void importXhsShareNote(XhsShareNoteBo shareNoteBo, String operatorName) {
        // TODO 自定义导入逻辑
        if (shareNoteBo.getId() == null){
            ValidatorUtils.validate(shareNoteBo, AddGroup.class);
            this.insertXhsShareNote(shareNoteBo);
        }else {
            ValidatorUtils.validate(shareNoteBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            XhsShareNote xhsShareNote = Db.getById(shareNoteBo.getId(), XhsShareNote.class);
            if (xhsShareNote == null){
                shareNoteBo.setId(null);
                this.insertXhsShareNote(shareNoteBo);
                return;
            }
            this.updateXhsShareNote(shareNoteBo);
        }
    }


    @Override
    @Async
    public void batchUpdate() {
        log.info("开始更新");

        LambdaQueryWrapper<XhsShareNote> wrapper = Wrappers.lambdaQuery(XhsShareNote.class)
            .eq(XhsShareNote::getMonitor, "ON")
            .or()
            .isNull(XhsShareNote::getPostTime);

        List<XhsShareNote> list = Db.list(wrapper);
        log.info("笔记数量：{}", list.size());
        for (XhsShareNote xhsShareNote : list) {
            String url = xhsShareNote.getShareUrl();
            try {
                ApiResponse apiResponse = ApiService.parseUrl(url);
                xhsShareNote.setTitle(apiResponse.getTitle());
                xhsShareNote.setUsername(apiResponse.getNickName());
                xhsShareNote.setCover(apiResponse.getCoverImg());
                xhsShareNote.setAvatar(apiResponse.getNickPic());
                Long pushTime = apiResponse.getPushTime();
                LocalDateTime localDateTime = DateUtil.date(pushTime).toLocalDateTime();
                xhsShareNote.setPostTime(localDateTime);
                Integer likeNum = Integer.valueOf(apiResponse.getLikeNum());
                Integer commentNum = Integer.valueOf(apiResponse.getCommentNum());
                Integer collectNum = Integer.valueOf(apiResponse.getCollectNum());
                Integer preInteraction = xhsShareNote.getInteraction();
                xhsShareNote.setPreInteraction(preInteraction);
                int interaction = likeNum + commentNum + collectNum;
                // 互动增量
                xhsShareNote.setInteraction(interaction);
                boolean saveStatus = Db.updateById(xhsShareNote);
                log.info("笔记更新" + (saveStatus ? "成功" : "失败"));
                // 添加延迟以避免请求过于频繁
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.warn("批量处理被中断");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("处理URL时发生异常: {}", url, e);
            }
        }
    }

    @Override
    public String UpdateInteractionByNoteId(String noteId) {
        Long userId = LoginHelper.getUserId();
        CompletableFuture.runAsync(() -> {
            LambdaQueryWrapper<XhsShareNote> wrapper = Wrappers.lambdaQuery(XhsShareNote.class)
                .eq(XhsShareNote::getNoteId, noteId);
            try {
                XhsShareNote xhsShareNote = Db.getOne(wrapper);
                String url = xhsShareNote.getShareUrl();
                ApiResponse apiResponse = ApiService.parseUrl(url);
                log.info("笔记回调数据：{}",apiResponse);
                xhsShareNote.setTitle(apiResponse.getTitle());
                xhsShareNote.setUsername(apiResponse.getNickName());
                xhsShareNote.setCover(apiResponse.getCoverImg());
                xhsShareNote.setAvatar(apiResponse.getNickPic());
                Long pushTime = apiResponse.getPushTime();
                LocalDateTime localDateTime = DateUtil.date(pushTime).toLocalDateTime();
                xhsShareNote.setPostTime(localDateTime);
                Integer likeNum = Integer.valueOf(apiResponse.getLikeNum());
                Integer commentNum = Integer.valueOf(apiResponse.getCommentNum());
                Integer collectNum = Integer.valueOf(apiResponse.getCollectNum());
                Integer preInteraction = xhsShareNote.getInteraction();
                xhsShareNote.setPreInteraction(preInteraction);
                xhsShareNote.setUpdateTime(new Date());
                int interaction = likeNum + commentNum + collectNum;
                // 互动增量
                xhsShareNote.setInteraction(interaction);
                boolean saveStatus = Db.updateById(xhsShareNote);
                log.info("笔记更新" + (saveStatus ? "成功" : "失败"));
                if (userId != null) {
                    SseMessageDto dto = new SseMessageDto();
                    dto.setMessage("笔记：" + noteId + "，获取笔记互动数据成功；请刷新页面查看");
                    dto.setUserIds(List.of(userId));
                    SseMessageUtils.publishMessage(dto);
                }
            }catch (Exception e){
                log.error("处理URL时发生异常，笔记ID {}", noteId, e);
                throw new RuntimeException("处理URL时发生异常，笔记ID：" + noteId, e);
            }
        });

        return "笔记数据更新中，更新完成后将发送通知";
    }
}

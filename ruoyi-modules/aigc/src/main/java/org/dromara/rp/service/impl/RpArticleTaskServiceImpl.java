package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.utils.YDUtils;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.rp.domain.RpAccount;
import org.dromara.rp.domain.RpAccountGroup;
import org.dromara.rp.domain.RpArticleTask;
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.rp.domain.bo.RpArticleTaskBo;
import org.dromara.rp.domain.bo.RpContentGroupInfo;
import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.mapper.RpAccountGroupMapper;
import org.dromara.rp.mapper.RpAccountMapper;
import org.dromara.rp.mapper.RpArticleTaskMapper;
import org.dromara.rp.mapper.RpaAccountConfigMapper;
import org.dromara.rp.service.IRpArticleDetailService;
import org.dromara.rp.service.IRpArticleTaskService;
import org.dromara.xhs.domain.XhsDyRun;
import org.dromara.xhs.domain.XhsDyRunItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;
import static org.dromara.constant.ConstantStr.MEDIA_TYPE_IMAGE;
import static org.dromara.constant.ConstantStr.MEDIA_TYPE_VIDEO;

/**
 * 文章任务主Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RpArticleTaskServiceImpl implements IRpArticleTaskService {

    private final RpArticleTaskMapper baseMapper;

    private final IRpArticleDetailService  rpArticleDetailService;

    private final RpAccountMapper rpAccountMapper;

    private final RpAccountGroupMapper rpAccountGroupMapper;

    private final YDUtils ydUtils;

    private final RpaAccountConfigMapper rpaAccountConfigMapper;

    private final Function<RpArticleTaskVo, String> getEntityName = (RpArticleTaskVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询文章任务主
     *
     * @param id 主键
     * @return 文章任务主
     */
    @Override
    public RpArticleTaskVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询文章任务主列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文章任务主分页列表
     */
    @Override
    public TableDataInfo<RpArticleTaskVo> queryPageList(RpArticleTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpArticleTask> lqw = buildQueryWrapper(bo);
        Page<RpArticleTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的文章任务主列表
     *
     * @param bo 查询条件
     * @return 文章任务主列表
     */
    @Override
    public List<RpArticleTaskVo> queryList(RpArticleTaskBo bo) {
        LambdaQueryWrapper<RpArticleTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpArticleTask> buildQueryWrapper(RpArticleTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpArticleTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpArticleTask::getId);
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), RpArticleTask::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), RpArticleTask::getDescription, bo.getDescription());
        lqw.eq(bo.getTotalArticles() != null, RpArticleTask::getTotalArticles, bo.getTotalArticles());
        lqw.eq(bo.getStatus() != null, RpArticleTask::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 获取当前请求对象（从Spring上下文）
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 判断当前设备是否为移动端（手机/平板）
     * @return true=移动端（手机/平板），false=PC端
     */
    private boolean isMobileDevice() {
        HttpServletRequest request = getCurrentRequest();
        // 非Web请求场景（如定时任务），默认判定为PC
        if (request == null) {
            log.warn("无法获取当前HTTP请求，默认判定为PC设备");
            return false;
        }

        String userAgent = request.getHeader("User-Agent");
        // User-Agent为空，默认判定为PC
        if (StringUtils.isBlank(userAgent)) {
            log.warn("请求头User-Agent为空，默认判定为PC设备");
            return false;
        }

        // 移动端核心关键词（覆盖主流手机/平板系统）
        String[] mobileKeywords = {"Android", "iPhone", "iPad", "iPod", "Windows Phone", "Mobile", "iOS", "SymbianOS", "BlackBerry"};
        // PC端核心关键词
        String[] pcKeywords = {"Windows NT", "Macintosh", "X11", "Linux x86_64"};

        // 优先匹配移动端关键词
        boolean isMobile = Arrays.stream(mobileKeywords).anyMatch(userAgent::contains);
        if (isMobile) {
            return true;
        }

        // 匹配PC端关键词
        boolean isPc = Arrays.stream(pcKeywords).anyMatch(userAgent::contains);
        return !isPc;
    }

    /**
     * 插入文章任务并分发至RPA执行
     * 核心流程：
     * 1. 转换BO为实体并保存文章任务主记录
     * 2. 校验并处理每个内容分组，保存文章详情
     * 3. 关联账号/分组信息获取RPA编号，按RPA编号分组构建执行参数
     * 4. 调用影刀RPA接口分发任务
     *
     * @param bo 文章任务业务对象
     * @return 插入结果：true-成功，false-失败
     * @throws ServiceException 业务异常（如内容为空、账号配置缺失等）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertRpArticleTask(RpArticleTaskBo bo) {
        // 1. 基础校验：BO不能为空
        Assert.notNull(bo, "文章任务BO不能为空");
        Assert.notNull(bo.getContent(), "文章任务内容不能为空");

        // ========== 新增：设备类型判断 ==========
        boolean isMobile = isMobileDevice();
        String deviceType = isMobile ? "手机/平板" : "PC";
        log.info("当前操作设备类型：{}，任务名称：{}", deviceType, bo.getTaskName());

        // 2. 转换并保存任务主记录
        RpArticleTask taskEntity = MapstructUtils.convert(bo, RpArticleTask.class);
        taskEntity.setTotalArticles((long) bo.getContent().getContentGroups().size());
        validEntityBeforeSave(taskEntity);
        boolean isSaveSuccess = baseMapper.insert(taskEntity) > 0;

        if (isSaveSuccess) {
            bo.setId(taskEntity.getId());
            List<RpContentGroupInfo> contentGroups = bo.getContent().getContentGroups();

            // 校验内容分组非空
            validateContentGroups(contentGroups);

            // 获取当前登录用户信息（提前获取，避免循环内重复调用）
            String loginPhone = LoginHelper.getPhoneNumber();
            Long loginUserId = LoginHelper.getUserId();

            if (isMobile) {
                // 移动端：生成指定格式的报文并调用接口
                List<Map<String, Object>> mobileRunItemList = buildMobileRunItemList(taskEntity.getId(), contentGroups);
                callYdApiForMobile(mobileRunItemList);
            } else {
                // PC端：保留原有逻辑
                Map<Integer, List<XhsDyRunItem>> rpaRunItemMap = buildRpaRunItemMap(taskEntity.getId(), contentGroups);
                callYdApiByRpaGroup(rpaRunItemMap, loginPhone, loginUserId);
            }
        }

        return isSaveSuccess;
    }

    /**
     * 校验内容分组列表有效性
     *
     * @param contentGroups 内容分组列表
     * @throws ServiceException 内容分组为空时抛出异常
     */
    private void validateContentGroups(List<RpContentGroupInfo> contentGroups) {
        if (Objects.isNull(contentGroups) || contentGroups.isEmpty()) {
            throw new ServiceException("分发内容为空，无法执行任务");
        }
    }

    /**
     * 构建RPA编号与执行项的映射关系
     *
     * @param taskId        文章任务ID
     * @param contentGroups 内容分组列表
     * @return key: RPA编号，value: 该RPA对应的执行项列表
     * @throws ServiceException 账号/分组配置异常时抛出
     */
    private Map<Integer, List<XhsDyRunItem>> buildRpaRunItemMap(Long taskId, List<RpContentGroupInfo> contentGroups) {
        Map<Integer, List<XhsDyRunItem>> rpaRunItemMap = new HashMap<>(16);

        for (RpContentGroupInfo group : contentGroups) {
            // 设置任务ID关联
            group.setTaskId(taskId);

            // 保存文章详情并返回详情ID
            Long detailId = saveRpArticleDetail(group);

            // 获取当前分组对应的RPA编号
            Integer rpaNo = getRpaNoByContentGroup(group);

            // 构建RPA执行项
            XhsDyRunItem runItem = buildXhsRunItem(detailId, group);

            // 将执行项添加到对应RPA编号的列表中
            rpaRunItemMap.computeIfAbsent(rpaNo, k -> new ArrayList<>(8)).add(runItem);
        }

        return rpaRunItemMap;
    }

    /**
     * 保存文章详情记录
     *
     * @param group 内容分组信息
     * @return 文章详情ID
     */
    private Long saveRpArticleDetail(RpContentGroupInfo group) {
        // 构建额外信息JSON
        Map<String, Object> extraMap = new HashMap<>(8);
        extraMap.put("video", group.getVideo());
        extraMap.put("picList", group.getPicList());
        extraMap.put("publishTime", group.getPublishTime());
        extraMap.put("tagList", group.getTagList());
        extraMap.put("url", group.getUrl());
        group.setExtraInfo(JSONUtil.toJsonStr(extraMap));

        // 转换并保存详情
        RpArticleDetailBo detailBo = BeanUtil.copyProperties(group, RpArticleDetailBo.class);
        rpArticleDetailService.insertRpArticleDetail(detailBo);

        return detailBo.getId();
    }

    /**
     * 通过内容分组获取对应的RPA编号
     * 关联查询：内容分组→账号→账号分组→RPA编号
     *
     * @param group 内容分组信息
     * @return RPA编号
     * @throws ServiceException 任意关联环节配置缺失时抛出
     */
    private Integer getRpaNoByContentGroup(RpContentGroupInfo group) {
        // 1. 校验并获取账号ID
        Long accountId = group.getAccountId();
        if (Objects.isNull(accountId)) {
            throw new ServiceException(String.format("内容分组【%s】未指定发布账号，无法获取RPA配置", group.getTitle()));
        }

        // 2. 查询账号信息
        RpAccount account = rpAccountMapper.selectById(accountId);
        if (Objects.isNull(account)) {
            throw new ServiceException(String.format("账号ID【%d】不存在，无法获取RPA配置", accountId));
        }

        // 3. 查询账号分组信息
        Long groupId = account.getGroupId();
        if (Objects.isNull(groupId)) {
            throw new ServiceException(String.format("账号【%s】未归属分组，无法获取RPA配置", account.getAccountName()));
        }
        RpAccountGroup accountGroup = rpAccountGroupMapper.selectById(groupId);
        if (Objects.isNull(accountGroup)) {
            throw new ServiceException(String.format("分组ID【%d】不存在，无法获取RPA配置", groupId));
        }

        // 4. 获取并校验RPA编号
        Integer rpaNo = accountGroup.getRpaNo();
        if (Objects.isNull(rpaNo)) {
            throw new ServiceException(String.format("分组【%s】未配置RPA编号，无法分发任务", accountGroup.getGroupName()));
        }

        return rpaNo;
    }

    /**
     * 构建小红书RPA执行项
     *
     * @param detailId 文章详情ID（作为执行项ID）
     * @param group    内容分组信息
     * @return 构建完成的RPA执行项
     */
    private XhsDyRunItem buildXhsRunItem(Long detailId, RpContentGroupInfo group) {
        XhsDyRunItem runItem = new XhsDyRunItem();
        runItem.setId(detailId);

        // 转换媒体类型（String→Integer）
        Integer mediaType = Integer.valueOf(group.getType());
        runItem.setType(mediaType);

        // 构建媒体URL列表（图片/视频区分）
        List<String> mediaUrlList = buildMediaUrlList(group);
        runItem.setMedium(mediaUrlList);

        // 设置基础内容信息
        runItem.setTitle(group.getTitle());
        runItem.setContent(group.getContent());

        // 标签处理：添加#前缀
        List<String> tagList = Optional.ofNullable(group.getTagList())
            .orElse(Collections.emptyList())
            .stream()
            .map(tag -> "#" + tag)
            .collect(Collectors.toList());
        runItem.setTagList(tagList);

        // @用户处理：添加@前缀（空值兼容）
        String mention = Optional.ofNullable(group.getMention())
            .filter(m -> !m.isEmpty())
            .map(m -> "@" + m)
            .orElse("");
        runItem.setMention(mention);

        // 定时发布时间
        runItem.setSchedule(group.getPublishTime());

        return runItem;
    }

    /**
     * 构建媒体URL列表（区分图片/视频）
     *
     * @param group 内容分组信息
     * @return 媒体URL列表
     */
    private List<String> buildMediaUrlList(RpContentGroupInfo group) {
        if (MEDIA_TYPE_IMAGE.equals(group.getType())) {
            return Optional.ofNullable(group.getPicList()).orElse(Collections.emptyList());
        } else if (MEDIA_TYPE_VIDEO.equals(group.getType())) {
            return Collections.singletonList(Optional.ofNullable(group.getVideo()).orElse(""));
        } else {
            throw new ServiceException(String.format("不支持的媒体类型：%s", group.getType()));
        }
    }

    /**
     * 按RPA分组调用影刀接口
     *
     * @param rpaRunItemMap RPA编号与执行项的映射
     * @param loginPhone    登录用户手机号
     * @param loginUserId   登录用户ID
     * @throws ServiceException RPA配置缺失或接口调用异常时抛出
     */
    private void callYdApiByRpaGroup(Map<Integer, List<XhsDyRunItem>> rpaRunItemMap,
                                     String loginPhone,
                                     Long loginUserId) {
        for (Map.Entry<Integer, List<XhsDyRunItem>> entry : rpaRunItemMap.entrySet()) {
            Integer rpaNo = entry.getKey();
            List<XhsDyRunItem> runItems = entry.getValue();

            // 构建RPA执行参数
            XhsDyRun xhsDyRun = buildXhsRun(loginUserId, loginPhone, runItems);
            String jsonParam = JsonUtils.toJsonString(xhsDyRun);

            // 日志打印
            log.info("调用影刀接口，RPA编号：{}，执行参数：{}", rpaNo, jsonParam);

            // 查询RPA配置
            RpaAccountConfigVo rpaConfig = rpaAccountConfigMapper.selectVoById(rpaNo);
            if (Objects.isNull(rpaConfig)) {
                throw new ServiceException(String.format("RPA编号【%d】对应的配置不存在，无法调用接口", rpaNo));
            }

            // 调用影刀接口
            ydUtils.RunYD(rpaConfig.getRobotClientUuid(), rpaConfig.getRobotClientName(), jsonParam);
        }
    }

    /**
     * 构建小红书RPA执行参数对象
     *
     * @param userId     登录用户ID
     * @param account    登录用户手机号
     * @param runItems   执行项列表
     * @return 构建完成的RPA执行参数
     */
    private XhsDyRun buildXhsRun(Long userId, String account, List<XhsDyRunItem> runItems) {
        XhsDyRun xhsDyRun = new XhsDyRun();
        xhsDyRun.setCookieId(userId);
        xhsDyRun.setAccount(account);
        xhsDyRun.setDataList(runItems);
        return xhsDyRun;
    }

    /**
     * 构建移动端RPA执行项报文列表（匹配指定JSON格式）
     * @param taskId 任务ID
     * @param contentGroups 内容分组列表
     * @return 移动端报文列表
     */
    private List<Map<String, Object>> buildMobileRunItemList(Long taskId, List<RpContentGroupInfo> contentGroups) {
        List<Map<String, Object>> mobileItemList = new ArrayList<>();
        for (RpContentGroupInfo group : contentGroups) {
            // 保存文章详情并返回详情ID
            Long detailId = saveRpArticleDetail(group);
            RpAccount account = rpAccountMapper.selectById(group.getAccountId());
            String deviceCode = account.getDeviceCode();

            // 构建单条移动端执行项
            Map<String, Object> mobileItem = new HashMap<>();
            // ID：转为字符串格式
            mobileItem.put("id", detailId.toString());
            // 设备编码：RPA+4位数字
            mobileItem.put("device", deviceCode);
            // 文件路径列表：区分图片/视频
            mobileItem.put("file_path", buildMediaUrlList(group));
            // 标题
            mobileItem.put("title", group.getTitle());
            // 描述（对应原内容字段）
            mobileItem.put("description", group.getContent());
            // 标签列表：直接使用原标签（无需加#）
            mobileItem.put("tags", Optional.ofNullable(group.getTagList()).orElse(Collections.emptyList()));
            // @用户列表：转为单元素列表，空值兼容
            String mention = Optional.ofNullable(group.getMention()).orElse("");
            mobileItem.put("mentions", Collections.singletonList(mention));
            // 媒体类型：picture/video
            String mediaType = MEDIA_TYPE_IMAGE.equals(group.getType()) ? "picture"
                : MEDIA_TYPE_VIDEO.equals(group.getType()) ? "video" : null;
            if (mediaType == null) {
                throw new ServiceException(String.format("不支持的媒体类型：%s", group.getType()));
            }
            mobileItem.put("type", mediaType);
            //获取RPA编号
            Integer rpaNo = getRpaNoByContentGroup(group);
            mobileItem.put("rpaNo", rpaNo);

            mobileItemList.add(mobileItem);
        }
        return mobileItemList;
    }

    /**
     * 调用移动端RPA接口
     * @param mobileRunItemList 移动端执行项列表
     * @throws ServiceException RPA配置缺失或接口调用异常时抛出
     */
    private void callYdApiForMobile(List<Map<String, Object>> mobileRunItemList) {
        // 按RPA编号分组（从device字段提取）
        Map<Integer, List<Map<String, Object>>> rpaMobileItemMap = mobileRunItemList.stream()
            .collect(Collectors.groupingBy(item -> (Integer) item.get("rpaNo")));

        for (Map.Entry<Integer, List<Map<String, Object>>> entry : rpaMobileItemMap.entrySet()) {
            Integer rpaNo = entry.getKey();
            List<Map<String, Object>> mobileItems = entry.getValue();

            // 构建移动端RPA执行参数（直接传列表JSON）
            String jsonParam = JsonUtils.toJsonString(mobileItems);
            log.info("【移动端】调用影刀接口，RPA编号：{}，执行参数：{}", rpaNo, jsonParam);

            // 查询RPA配置
            RpaAccountConfigVo rpaConfig = rpaAccountConfigMapper.selectVoById(rpaNo);
            if (Objects.isNull(rpaConfig)) {
                throw new ServiceException(String.format("RPA编号【%d】对应的配置不存在，无法调用移动端接口", rpaNo));
            }

            // 调用影刀接口
            ydUtils.RunYD(rpaConfig.getRobotClientUuid(), rpaConfig.getRobotClientName(), jsonParam);
        }
    }

    /**
     * 修改文章任务主
     *
     * @param bo 文章任务主
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpArticleTask(RpArticleTaskBo bo) {
        RpArticleTask update = MapstructUtils.convert(bo, RpArticleTask.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpArticleTask entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除文章任务主信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量导入文章任务主数据
     * @param rpArticleTaskList 文章任务主列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpArticleTask(List<RpArticleTaskVo> rpArticleTaskList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpArticleTaskList,
            updateSupport,
            operatorName,
            this::importRpArticleTaskInfo,
            getEntityName
        );
    }

    /**
     * 导入文章任务主信息
     * @param rpArticleTaskVo 文章任务主信息
     * @param operatorName 操作人
     */
    private void importRpArticleTaskInfo(RpArticleTaskVo rpArticleTaskVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpArticleTaskBo rpArticleTaskBo = BeanUtil.toBean(rpArticleTaskVo, RpArticleTaskBo.class);
        if (rpArticleTaskBo.getId() == null){
            ValidatorUtils.validate(rpArticleTaskBo, AddGroup.class);
            this.insertRpArticleTask(rpArticleTaskBo);
        }else {
            ValidatorUtils.validate(rpArticleTaskBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpArticleTask rpArticleTask = Db.getById(rpArticleTaskBo.getId(), RpArticleTask.class);
            if (rpArticleTask == null){
                rpArticleTaskBo.setId(null);
                this.insertRpArticleTask(rpArticleTaskBo);
                return;
            }
            this.updateRpArticleTask(rpArticleTaskBo);
        }
    }


}

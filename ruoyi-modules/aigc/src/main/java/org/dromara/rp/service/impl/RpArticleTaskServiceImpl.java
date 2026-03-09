package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
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
import org.dromara.rp.domain.RpBitAccount;
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.rp.domain.bo.RpArticleTaskBo;
import org.dromara.rp.domain.bo.RpContentGroupInfo;
import org.dromara.rp.domain.vo.RpArticleDetailVo;
import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.mapper.RpAccountGroupMapper;
import org.dromara.rp.mapper.RpAccountMapper;
import org.dromara.rp.mapper.RpArticleTaskMapper;
import org.dromara.rp.mapper.RpBitAccountMapper;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /** 移动端设备关键词（覆盖主流手机/平板系统） */
    private static final List<String> MOBILE_DEVICE_KEYWORDS = Arrays.asList("Android", "iPhone", "iPad", "iPod", "Windows Phone", "Mobile", "iOS", "SymbianOS", "BlackBerry");
    /** PC端设备关键词 */
    private static final List<String> PC_DEVICE_KEYWORDS = Arrays.asList("Windows NT", "Macintosh", "X11", "Linux x86_64");
    /** 媒体类型映射：原类型 → 移动端接口类型 */
    private static final Map<String, String> MEDIA_TYPE_MAPPING = new HashMap<>(2);

    static {
        MEDIA_TYPE_MAPPING.put(MEDIA_TYPE_IMAGE, "picture");
        MEDIA_TYPE_MAPPING.put(MEDIA_TYPE_VIDEO, "video");
    }

    // ========== 依赖注入 ==========
    private final RpArticleTaskMapper baseMapper;
    private final IRpArticleDetailService rpArticleDetailService;
    private final RpAccountMapper rpAccountMapper;
    private final RpAccountGroupMapper rpAccountGroupMapper;
    private final YDUtils ydUtils;
    private final RpaAccountConfigMapper rpaAccountConfigMapper;
    private final RpBitAccountMapper bitAccountMapper;

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
    public RpArticleTaskVo queryById(Long id) {
        RpArticleTaskVo rpArticleTaskVo = baseMapper.selectVoById(id);
        List<RpArticleDetailVo> rpArticleDetailVos = rpArticleDetailService.queryMainDetailList(id);
        rpArticleTaskVo.setDetailVos(rpArticleDetailVos);
        return rpArticleTaskVo;
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

    /**
     * 构建查询条件封装器
     */
    private LambdaQueryWrapper<RpArticleTask> buildQueryWrapper(RpArticleTaskBo bo) {
        LambdaQueryWrapper<RpArticleTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpArticleTask::getId);
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), RpArticleTask::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), RpArticleTask::getDescription, bo.getDescription());
        lqw.eq(bo.getTotalArticles() != null, RpArticleTask::getTotalArticles, bo.getTotalArticles());
        lqw.eq(bo.getStatus() != null, RpArticleTask::getStatus, bo.getStatus());
        lqw.eq(bo.getDeviceType() != null, RpArticleTask::getDeviceType, bo.getDeviceType());
        return lqw;
    }

    /**
     * 获取当前HTTP请求对象（从Spring上下文）
     * @return 当前请求对象，非Web场景返回null
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    /**
     * 判断当前操作设备是否为移动端（手机/平板）
     * @return true=移动端，false=PC端（非Web场景默认返回false）
     */
    private boolean isMobileDevice() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            log.warn("无法获取HTTP请求，默认判定为PC设备（非Web场景）");
            return false;
        }

        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            log.warn("请求头User-Agent为空，默认判定为PC设备");
            return false;
        }

        // 优先匹配移动端关键词
        boolean isMobile = MOBILE_DEVICE_KEYWORDS.stream().anyMatch(userAgent::contains);
        if (isMobile) {
            return true;
        }

        // 匹配PC端关键词，未匹配则判定为移动端（兜底）
        boolean isPc = PC_DEVICE_KEYWORDS.stream().anyMatch(userAgent::contains);
        return !isPc;
    }

    /**
     * 插入文章任务并分发至RPA执行
     * 核心流程：
     * 1. 基础校验 & 设备类型判断
     * 2. 保存任务主记录
     * 3. 按设备类型（移动端/PC端）分发RPA任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertRpArticleTask(RpArticleTaskBo bo) {
        // 1. 基础校验
        validateInsertBo(bo);

        // 2. 设备类型判断 & 日志记录
//        boolean isMobile = isMobileDevice();
        log.info("开始插入文章任务，任务名称：{}，操作设备类型：{}", bo.getTaskName(), bo.getDeviceType() == 1 ? "手机/平板" : "PC");

        // 3. 保存任务主记录
        RpArticleTask taskEntity = saveMainTaskRecord(bo);
        if (taskEntity == null) {
            log.error("文章任务主记录保存失败，任务名称：{}", bo.getTaskName());
            return false;
        }
        bo.setId(taskEntity.getId());

        // 4. 校验内容分组
        List<RpContentGroupInfo> contentGroups = bo.getContent().getContentGroups();
        validateContentGroups(contentGroups);

        //获取比特账号分组
        List<RpBitAccount> bitAccounts = bitAccountMapper.selectList();
        //将bitAccounts改为id为key，accountCode为value的map
        Map<Long, String> bitAccountMap = bitAccounts.stream().collect(Collectors.toMap(RpBitAccount::getId, RpBitAccount::getAccountCode));

        // 5. 按设备类型分发RPA任务
        if (bo.getDeviceType() == 1) {
            dispatchMobileRpaTask(taskEntity.getId(), contentGroups,bo.getPlatform(),bitAccountMap);
        } else {
            dispatchPcRpaTask(taskEntity.getId(), contentGroups,bo.getPlatform(),bitAccountMap);
        }

        log.info("文章任务插入并分发完成，任务ID：{}", taskEntity.getId());
        return true;
    }

    /**
     * 校验插入用BO的合法性
     */
    private void validateInsertBo(RpArticleTaskBo bo) {
        Assert.notNull(bo, "文章任务BO不能为空");
        Assert.notNull(bo.getContent(), "文章任务内容不能为空");
        Assert.notNull(bo.getContent().getContentGroups(), "文章任务内容分组不能为空");
    }

    /**
     * 保存文章任务主记录
     * @return 保存后的任务实体，保存失败返回null
     */
    private RpArticleTask saveMainTaskRecord(RpArticleTaskBo bo) {
        RpArticleTask taskEntity = MapstructUtils.convert(bo, RpArticleTask.class);
        taskEntity.setTotalArticles((long) bo.getContent().getContentGroups().size());
        validEntityBeforeSave(taskEntity);

        return baseMapper.insert(taskEntity) > 0 ? taskEntity : null;
    }

    /**
     * 校验内容分组列表有效性
     */
    private void validateContentGroups(List<RpContentGroupInfo> contentGroups) {
        if (contentGroups.isEmpty()) {
            throw new ServiceException("分发内容为空，无法执行任务");
        }
    }

    /**
     * 分发PC端RPA任务
     */
    private void dispatchPcRpaTask(Long taskId, List<RpContentGroupInfo> contentGroups, String platform,Map<Long, String> bitAccountMap) {
        // 构建RPA执行项映射
        Map<Long, List<XhsDyRunItem>> rpaRunItemMap = buildPcRpaRunItemMap(taskId, contentGroups,bitAccountMap);

        // 获取登录用户信息（提前获取，避免循环内重复调用）
        String loginPhone = LoginHelper.getPhoneNumber();
        Long loginUserId = LoginHelper.getUserId();

        // 调用影刀接口
        callYdApiByRpaGroup(rpaRunItemMap, loginPhone, loginUserId, platform,taskId);
    }

    /**
     * 分发移动端RPA任务
     */
    private void dispatchMobileRpaTask(Long taskId, List<RpContentGroupInfo> contentGroups, String platform,Map<Long, String> bitAccountMap) {
        // 构建移动端执行项列表
        List<Map<String, Object>> mobileRunItemList = buildMobileRunItemList(taskId, contentGroups,bitAccountMap);

        // 调用移动端影刀接口
        callYdApiForMobile(mobileRunItemList,platform,taskId);
    }

    /**
     * 构建PC端RPA编号与执行项的映射关系
     */
    private Map<Long, List<XhsDyRunItem>> buildPcRpaRunItemMap(Long taskId, List<RpContentGroupInfo> contentGroups,Map<Long, String> bitAccountMap) {
        Map<Long, List<XhsDyRunItem>> rpaRunItemMap = new HashMap<>(contentGroups.size());

        for (RpContentGroupInfo group : contentGroups) {
            group.setTaskId(taskId);
            Long detailId = saveRpArticleDetail(group);
            Long rpaNo = getRpaNoByContentGroup(group);
            XhsDyRunItem runItem = buildXhsRunItem(detailId, group,bitAccountMap);

            rpaRunItemMap.computeIfAbsent(rpaNo, k -> new ArrayList<>(8)).add(runItem);
        }

        return rpaRunItemMap;
    }

    /**
     * 保存文章详情记录
     * @return 保存后的详情ID
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
     * 关联逻辑：内容分组→账号→账号分组→RPA编号
     */
    private Long getRpaNoByContentGroup(RpContentGroupInfo group) {
        // 1. 校验账号ID
        Long accountId = Optional.ofNullable(group.getAccountId())
            .orElseThrow(() -> new ServiceException(String.format("内容分组【%s】未指定发布账号，无法获取RPA配置", group.getTitle())));

        // 2. 查询账号信息
        RpAccount account = Optional.ofNullable(rpAccountMapper.selectById(accountId))
            .orElseThrow(() -> new ServiceException(String.format("账号ID【%d】不存在，无法获取RPA配置", accountId)));

        // 3. 查询账号分组信息
        Long groupId = Optional.ofNullable(account.getGroupId())
            .orElseThrow(() -> new ServiceException(String.format("账号【%s】未归属分组，无法获取RPA配置", account.getAccountName())));

        RpAccountGroup accountGroup = Optional.ofNullable(rpAccountGroupMapper.selectById(groupId))
            .orElseThrow(() -> new ServiceException(String.format("分组ID【%d】不存在，无法获取RPA配置", groupId)));

        // 4. 校验并返回RPA编号
        return Optional.ofNullable(accountGroup.getRpaNo())
            .orElseThrow(() -> new ServiceException(String.format("分组【%s】未配置RPA编号，无法分发任务", accountGroup.getGroupName())));
    }

    /**
     * 构建PC端小红书RPA执行项
     */
    private XhsDyRunItem buildXhsRunItem(Long detailId, RpContentGroupInfo group,Map<Long, String> bitAccountMap) {
        XhsDyRunItem runItem = new XhsDyRunItem();
        runItem.setId(detailId);
        runItem.setType(Integer.valueOf(group.getType()));
        runItem.setMedium(buildMediaUrlList(group));
        runItem.setTitle(group.getTitle());
        runItem.setContent(group.getContent());
        runItem.setTagList(buildTagList(group.getTagList()));
        runItem.setMention(buildMentionStr(group.getMention()));
        runItem.setSchedule(group.getPublishTime());
        runItem.setIfControlEvaluation(group.getIfControlEvaluation());
        runItem.setControlEvaluationContent(group.getControlEvaluationContent());
        runItem.setIsBite(Long.valueOf(group.getIfBite()));
        runItem.setBiteId(ObjUtil.isNull(group.getIfBite()) ||"1".equals(group.getIfBite())?bitAccountMap.get(Long.valueOf(group.getBiteNo())):null);

        return runItem;
    }

    /**
     * 构建标签列表（添加#前缀）
     */
    private List<String> buildTagList(List<String> originalTags) {
        return Optional.ofNullable(originalTags)
            .orElse(Collections.emptyList())
            .stream()
            .map(tag -> "#" + tag)
            .collect(Collectors.toList());
    }

    /**
     * 构建@用户字符串（添加@前缀）
     */
    private String buildMentionStr(String originalMention) {
        return Optional.ofNullable(originalMention)
            .filter(StringUtils::isNotBlank)
            .map(mention -> "@" + mention)
            .orElse("");
    }

    /**
     * 构建媒体URL列表（区分图片/视频）
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
     * 按RPA分组调用PC端影刀接口
     */
    private void callYdApiByRpaGroup(Map<Long, List<XhsDyRunItem>> rpaRunItemMap,
                                     String loginPhone,
                                     Long loginUserId,
                                     String platform,
                                     Long taskId) {
        for (Map.Entry<Long, List<XhsDyRunItem>> entry : rpaRunItemMap.entrySet()) {
            Long rpaNo = entry.getKey();
            List<XhsDyRunItem> runItems = entry.getValue();

            // 构建执行参数
            XhsDyRun xhsDyRun = buildXhsRun(loginUserId, loginPhone, runItems);
            String jsonParam = JsonUtils.toJsonString(xhsDyRun);
            log.info("【PC端】调用影刀接口，RPA编号：{}，执行参数：{}", rpaNo, jsonParam);

            // 调用接口
            callYdApi(rpaNo, jsonParam,platform,taskId);
        }
    }

    /**
     * 构建PC端小红书RPA执行参数对象
     */
    private XhsDyRun buildXhsRun(Long userId, String account, List<XhsDyRunItem> runItems) {
        XhsDyRun xhsDyRun = new XhsDyRun();
        xhsDyRun.setCookieId(userId);
        xhsDyRun.setAccount(account);
        xhsDyRun.setDataList(runItems);
        return xhsDyRun;
    }

    /**
     * 构建移动端RPA执行项报文列表
     */
    private List<Map<String, Object>> buildMobileRunItemList(Long taskId, List<RpContentGroupInfo> contentGroups,Map<Long, String> bitAccountMap) {
        List<Map<String, Object>> mobileItemList = new ArrayList<>(contentGroups.size());

        for (RpContentGroupInfo group : contentGroups) {
            Long detailId = saveRpArticleDetail(group);
            RpAccount account = rpAccountMapper.selectById(group.getAccountId());
            Long rpaNo = getRpaNoByContentGroup(group);

            // 构建单条移动端执行项
            Map<String, Object> mobileItem = new HashMap<>(10);
            mobileItem.put("id", detailId.toString());
            mobileItem.put("device", account.getDeviceCode());
            mobileItem.put("file_path", buildMediaUrlList(group));
            mobileItem.put("title", group.getTitle());
            mobileItem.put("description", group.getContent());
            mobileItem.put("tags", Optional.ofNullable(group.getTagList()).orElse(Collections.emptyList()));
            mobileItem.put("mentions", Collections.singletonList(Optional.ofNullable(group.getMention()).orElse("")));
            mobileItem.put("type", getMobileMediaType(group.getType()));
            mobileItem.put("rpaNo", rpaNo);
            mobileItem.put("ifBite", group.getIfBite());
            mobileItem.put("biteId", ObjUtil.isNull(group.getIfBite()) ||"1".equals(group.getIfBite())?bitAccountMap.get(Long.valueOf(group.getBiteNo())):"");

            mobileItemList.add(mobileItem);
        }

        return mobileItemList;
    }

    /**
     * 获取移动端媒体类型标识
     */
    private String getMobileMediaType(String originalType) {
        return Optional.ofNullable(MEDIA_TYPE_MAPPING.get(originalType))
            .orElseThrow(() -> new ServiceException(String.format("不支持的媒体类型：%s", originalType)));
    }

    /**
     * 调用移动端RPA接口
     */
    private void callYdApiForMobile(List<Map<String, Object>> mobileRunItemList,String platform,Long taskId) {
        // 按RPA编号分组
        Map<Long, List<Map<String, Object>>> rpaMobileItemMap = mobileRunItemList.stream()
            .collect(Collectors.groupingBy(item -> (Long) item.get("rpaNo")));

        for (Map.Entry<Long, List<Map<String, Object>>> entry : rpaMobileItemMap.entrySet()) {
            Long rpaNo = entry.getKey();
            List<Map<String, Object>> mobileItems = entry.getValue();

            // 构建执行参数
            String jsonParam = JsonUtils.toJsonString(mobileItems);
            log.info("【移动端】调用影刀接口，RPA编号：{}，执行参数：{}", rpaNo, jsonParam);

            // 调用接口
            callYdApi(rpaNo, jsonParam,platform,taskId);
        }
    }

    /**
     * 通用影刀接口调用方法
     */
    private void callYdApi(Long rpaNo, String jsonParam, String platform,Long taskId) {
        // 查询RPA配置
        RpaAccountConfigVo rpaConfig = Optional.ofNullable(rpaAccountConfigMapper.selectVoById(rpaNo))
            .orElseThrow(() -> new ServiceException(String.format("RPA编号【%d】对应的配置不存在，无法调用接口", rpaNo)));
        String ydAppId = switch ( platform) {
            case "小红书" -> "03ffda93-f595-4ef7-8d4a-82fedb08e346";
            case "抖音" -> "e9662962-655e-43fa-b208-ab1b54bd691a";
            case "逛逛" -> "86fa53ab-a388-488c-8b87-f5bc7ed8e195";
            default -> throw new ServiceException("不支持的平台");
        };
        // 调用影刀接口
        try {
            log.info("调用影刀接口开始，RPA编号：{}，参数：{},uuid:{},name:{}", rpaNo, jsonParam, rpaConfig.getRobotClientUuid(), rpaConfig.getRobotClientName());
            boolean ifSuccess = ydUtils.RunYD(ydAppId, rpaConfig.getRobotClientName(), jsonParam);
            RpArticleTask task = new RpArticleTask();
            task.setId(taskId);
            task.setStatus(ifSuccess?1L:3L);
//            task.setStatus(1L);
            task.setUpdateTime(new Date());
            baseMapper.updateById(task);
        } catch (Exception e) {
            log.error("调用影刀接口失败，RPA编号：{}，参数：{}", rpaNo, jsonParam, e);
            throw new ServiceException(String.format("调用RPA接口失败，RPA编号：%d", rpaNo));
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
        RpArticleTaskBo rpArticleTaskBo = BeanUtil.toBean(rpArticleTaskVo, RpArticleTaskBo.class);

        if (rpArticleTaskBo.getId() == null) {
            ValidatorUtils.validate(rpArticleTaskBo, AddGroup.class);
            this.insertRpArticleTask(rpArticleTaskBo);
            return;
        }

        // 存在ID时，校验是否为有效记录
        ValidatorUtils.validate(rpArticleTaskBo, EditGroup.class);
        RpArticleTask existEntity = Db.getById(rpArticleTaskBo.getId(), RpArticleTask.class);

        if (existEntity == null) {
            rpArticleTaskBo.setId(null);
            this.insertRpArticleTask(rpArticleTaskBo);
        } else {
            this.updateRpArticleTask(rpArticleTaskBo);
        }
    }
}

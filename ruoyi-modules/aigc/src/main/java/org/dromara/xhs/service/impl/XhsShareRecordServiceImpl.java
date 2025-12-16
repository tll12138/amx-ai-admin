package org.dromara.xhs.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.xhs.config.XhsShareProperties;
import org.dromara.xhs.domain.XhsShareRecord;
import org.dromara.xhs.domain.bo.XhsShareRecordBo;
import org.dromara.xhs.domain.vo.share.XhsShareRecordVo;
import org.dromara.xhs.domain.vo.share.ShareCreateReqVO;
import org.dromara.xhs.domain.vo.share.ShareCreateRespVO;
import org.dromara.xhs.enums.ShareStatusEnum;
import org.dromara.xhs.enums.ShareTypeEnum;
import org.dromara.xhs.mapper.XhsShareRecordMapper;
import org.dromara.xhs.service.IXhsShareRecordService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.dromara.common.core.utils.ServiceExceptionUtil.exception;
import static org.dromara.constant.ErrorCodeConstant.*;
import static org.dromara.xhs.util.StringGeneratorUtil.generateShareId;

/**
 * 分享记录Service业务层处理
 *
 * @author ZRL
 * @date 2025-07-24
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XhsShareRecordServiceImpl implements IXhsShareRecordService {

    private final XhsShareRecordMapper baseMapper;

    @Resource
    private RedisTemplate<String, XhsShareRecord> redisTemplate;

    private final XhsShareProperties shareProperties;

//    /**
//     * 分页查询分享记录列表
//     *
//     * @param bo        查询条件
//     * @param pageQuery 分页参数
//     * @return 分享记录分页列表
//     */
//    @Override
//    public TableDataInfo<XhsShareRecordVo> queryPageList(XhsShareRecordBo bo, PageQuery pageQuery) {
//        LambdaQueryWrapper<XhsShareRecord> lqw = buildQueryWrapper(bo);
//        Page<XhsShareRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
//        return TableDataInfo.build(result);
//    }
//
//    /**
//     * 查询符合条件的分享记录列表
//     *
//     * @param bo 查询条件
//     * @return 分享记录列表
//     */
//    @Override
//    public List<XhsShareRecordVo> queryList(XhsShareRecordBo bo) {
//        LambdaQueryWrapper<XhsShareRecord> lqw = buildQueryWrapper(bo);
//        return baseMapper.selectVoList(lqw);
//    }

    private LambdaQueryWrapper<XhsShareRecord> buildQueryWrapper(XhsShareRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<XhsShareRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(XhsShareRecord::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getShareId()), XhsShareRecord::getShareId, bo.getShareId());
        lqw.eq(bo.getUserId() != null, XhsShareRecord::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), XhsShareRecord::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), XhsShareRecord::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getImages()), XhsShareRecord::getImages, bo.getImages());
        lqw.eq(StringUtils.isNotBlank(bo.getVideo()), XhsShareRecord::getVideo, bo.getVideo());
        lqw.eq(StringUtils.isNotBlank(bo.getCover()), XhsShareRecord::getCover, bo.getCover());
        lqw.eq(StringUtils.isNotBlank(bo.getShareType()), XhsShareRecord::getType, bo.getShareType());
        lqw.eq(bo.getStatus() != null, XhsShareRecord::getStatus, bo.getStatus());
        lqw.eq(bo.getExpiresAt() != null, XhsShareRecord::getExpiresAt, bo.getExpiresAt());
        lqw.eq(bo.getAccessCount() != null, XhsShareRecord::getAccessCount, bo.getAccessCount());
        lqw.eq(bo.getAccessTime() != null, XhsShareRecord::getAccessTime, bo.getAccessTime());
        return lqw;
    }

    @Override
    public ShareCreateRespVO createShare(ShareCreateReqVO createReqVO) {
        // 转换数据结构
        XhsShareRecord xhsShareRecord = MapstructUtils.convert(createReqVO, XhsShareRecord.class);

        // 转换失败则抛出异常
        if (xhsShareRecord == null) {
            throw exception(ERROR_SHARE_INFO);
        }

        // 校验类型是否匹配
        if (!checkParamsByShareType(xhsShareRecord)){
            // 分享类型 对应的 参数为空
            throw exception(ERROR_SHARE_TYPE);
        }

        // 生成唯一分享ID
        String shareId = generateShareId();
        xhsShareRecord.setShareId(shareId);

        // 设置默认过期时间
        LocalDateTime localDateTime = LocalDateTimeUtil.now()
            .plusDays(shareProperties.getDefaultExpireDays());
        xhsShareRecord.setExpiresAt(localDateTime);

        // 设置默认状态为正常
        xhsShareRecord.setStatus(ShareStatusEnum.NORMAL_CODE);
        xhsShareRecord.setAccessCount(0);

        // 保存分享记录
        int result = baseMapper.insert(xhsShareRecord);
        if (result <= 0) {
            throw exception(ERROR_SHARE_INFO, "分享信息失败");
        }

        // 生成分享链接
        String shareUrl = generateShareUrl(shareId);

        // 缓存分享信息
        cacheShareInfo(shareId, xhsShareRecord);

        // 返回结果
        return ShareCreateRespVO.builder()
            .shareId(shareId)
            .shareUrl(shareUrl)
            .createTime(xhsShareRecord.getCreateTime())
            .expiresAt(xhsShareRecord.getExpiresAt())
            .build();
    }

    /**
     * 根据类型校验对应的参数是否存在
     *
     * @param createReqVO 创建参数
     */
    private boolean checkParamsByShareType(XhsShareRecord createReqVO) {
        if (ShareTypeEnum.NORMAL_TYPE.equals(createReqVO.getType())) {
            return StringUtils.isNotEmpty(createReqVO.getImages());
        } else if (ShareTypeEnum.VIDEO_TYPE.equals(createReqVO.getType())) {
            return StringUtils.isNotEmpty(createReqVO.getVideo());
        } else {
            throw exception(ERROR_SHARE_TYPE);
        }
    }

    @Override
    public XhsShareRecordVo getShareDetail(String shareId) {
        // 先从缓存获取
        XhsShareRecord cachedShare = getCachedShareInfo(shareId);
        if (cachedShare != null) {
            // 更新访问次数
            updateAccessCount(shareId, cachedShare.getAccessCount());
            XhsShareRecordVo recordVo = MapstructUtils.convert(cachedShare, XhsShareRecordVo.class);
            if (recordVo != null) {
                recordVo.setShareUrl(generateShareUrl(shareId));
            }
            return recordVo;
        }

        // 从数据库查询
        XhsShareRecord shareRecord = queryByShareId(shareId);
        if (shareRecord == null) {
            throw exception(INVALIDATE_SHARE_URL);
        }

        // 检查分享是否有效
        if (!isShareValid(shareRecord)) {
            throw exception(INVALIDATE_SHARE_URL);
        }

        // 更新访问次数
        updateAccessCount(shareId, shareRecord.getAccessCount());

        // 重新缓存
        cacheShareInfo(shareId, shareRecord);
        XhsShareRecordVo recordVo = MapstructUtils.convert(shareRecord, XhsShareRecordVo.class);
        if (recordVo != null) {
            recordVo.setShareUrl(generateShareUrl(shareId));
        }
        return recordVo;
    }

    @Override
    public void refreshToken() {

    }

    @Override
    public XhsShareRecord queryByShareId(String shareId) {
        LambdaQueryWrapper<XhsShareRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(XhsShareRecord::getShareId, shareId);
        return baseMapper.selectOne(wrapper);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成分享链接
     */
    private String generateShareUrl(String shareId) {
        return shareProperties.getUrlPrefix() + shareId;
    }

    /**
     * 缓存分享信息
     */
    private void cacheShareInfo(String shareId, XhsShareRecord dto) {
        try {
            String cacheKey = shareProperties.getCachePrefix() + shareId;
            redisTemplate.opsForValue().set(cacheKey, dto,
                shareProperties.getDefaultExpireDays(), TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("缓存分享信息失败, shareId: {}", shareId, e);
        }
    }

    /**
     * 从缓存获取分享信息
     */
    private XhsShareRecord getCachedShareInfo(String shareId) {
        try {
            String cacheKey = shareProperties.getCachePrefix() + shareId;
            return redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("从缓存获取分享信息失败, shareId: {}", shareId, e);
        }
        return null;
    }

    /**
     * 清除分享缓存
     */
    private void clearShareCache(String shareId) {
        try {
            String cacheKey = shareProperties.getCachePrefix() + shareId;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("清除分享缓存失败, shareId: {}", shareId, e);
        }
    }

    /**
     * 检查分享是否有效
     */
    private boolean isShareValid(XhsShareRecord shareRecord) {
        if (shareRecord == null) {
            return false;
        }

        // 检查状态
        if (!ShareStatusEnum.NORMAL_CODE.equals(shareRecord.getStatus())) {
            return false;
        }

        // 检查过期时间
        return shareRecord.getExpiresAt() == null ||
                !shareRecord.getExpiresAt().isBefore(LocalDateTime.now());
    }

    /**
     * 更新访问次数
     */
    private void updateAccessCount(String shareId, Integer accessCount) {
        try {
            LambdaQueryWrapper<XhsShareRecord> wrapper = Wrappers.lambdaQuery(XhsShareRecord.class);
            wrapper.eq(XhsShareRecord::getShareId, shareId);
            wrapper.eq(XhsShareRecord::getAccessCount, accessCount);

            XhsShareRecord updateRecord = new XhsShareRecord();
            updateRecord.setAccessCount(accessCount + 1);
            updateRecord.setAccessTime(LocalDateTime.now());
            updateRecord.setUpdateTime(DateUtils.getNowDate());

            baseMapper.update(updateRecord, wrapper);
        } catch (Exception e) {
            log.warn("更新访问次数失败, shareId: {}", shareId, e);
        }
    }
}

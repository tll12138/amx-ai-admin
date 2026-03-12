package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.rp.domain.bo.YdAppConfigBo;
import org.dromara.rp.domain.vo.YdAppConfigVo;
import org.dromara.rp.domain.YdAppConfig;
import org.dromara.rp.mapper.YdAppConfigMapper;
import org.dromara.rp.service.IYdAppConfigService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.function.Function;

/**
 * 影刀应用配置Service业务层处理
 *
 * @author LL
 * @date 2026-03-12
 */
@RequiredArgsConstructor
@Service
public class YdAppConfigServiceImpl implements IYdAppConfigService {

    private final YdAppConfigMapper baseMapper;

    private final Function<YdAppConfigVo, String> getEntityName = (YdAppConfigVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询影刀应用配置
     *
     * @param id 主键
     * @return 影刀应用配置
     */
    @Override
    public YdAppConfigVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询影刀应用配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 影刀应用配置分页列表
     */
    @Override
    public TableDataInfo<YdAppConfigVo> queryPageList(YdAppConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<YdAppConfig> lqw = buildQueryWrapper(bo);
        Page<YdAppConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的影刀应用配置列表
     *
     * @param bo 查询条件
     * @return 影刀应用配置列表
     */
    @Override
    public List<YdAppConfigVo> queryList(YdAppConfigBo bo) {
        LambdaQueryWrapper<YdAppConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<YdAppConfig> buildQueryWrapper(YdAppConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<YdAppConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(YdAppConfig::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), YdAppConfig::getPlatform, bo.getPlatform());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), YdAppConfig::getDeviceType, bo.getDeviceType());
        lqw.eq(StringUtils.isNotBlank(bo.getApplicationId()), YdAppConfig::getApplicationId, bo.getApplicationId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), YdAppConfig::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增影刀应用配置
     *
     * @param bo 影刀应用配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertYdAppConfig(YdAppConfigBo bo) {
        YdAppConfig add = MapstructUtils.convert(bo, YdAppConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改影刀应用配置
     *
     * @param bo 影刀应用配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateYdAppConfig(YdAppConfigBo bo) {
        YdAppConfig update = MapstructUtils.convert(bo, YdAppConfig.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(YdAppConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除影刀应用配置信息
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
     * 批量导入影刀应用配置数据
     * @param ydAppConfigList 影刀应用配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importYdAppConfig(List<YdAppConfigVo> ydAppConfigList, boolean updateSupport, String operatorName){
        return ImportEntities(
            ydAppConfigList,
            updateSupport,
            operatorName,
            this::importYdAppConfigInfo,
            getEntityName
        );
    }

    /**
     * 导入影刀应用配置信息
     * @param ydAppConfigVo 影刀应用配置信息
     * @param operatorName 操作人
     */
    private void importYdAppConfigInfo(YdAppConfigVo ydAppConfigVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        YdAppConfigBo ydAppConfigBo = BeanUtil.toBean(ydAppConfigVo, YdAppConfigBo.class);
        if (ydAppConfigBo.getId() == null){
            ValidatorUtils.validate(ydAppConfigBo, AddGroup.class);
            this.insertYdAppConfig(ydAppConfigBo);
        }else {
            ValidatorUtils.validate(ydAppConfigBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            YdAppConfig ydAppConfig = Db.getById(ydAppConfigBo.getId(), YdAppConfig.class);
            if (ydAppConfig == null){
                ydAppConfigBo.setId(null);
                this.insertYdAppConfig(ydAppConfigBo);
                return;
            }
            this.updateYdAppConfig(ydAppConfigBo);
        }
    }
}

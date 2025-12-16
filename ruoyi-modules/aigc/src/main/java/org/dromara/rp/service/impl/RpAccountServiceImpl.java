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
import org.dromara.rp.domain.bo.RpAccountBo;
import org.dromara.rp.domain.vo.RpAccountVo;
import org.dromara.rp.domain.RpAccount;
import org.dromara.rp.mapper.RpAccountMapper;
import org.dromara.rp.service.IRpAccountService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.function.Function;

/**
 * 账号信息Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@RequiredArgsConstructor
@Service
public class RpAccountServiceImpl implements IRpAccountService {

    private final RpAccountMapper baseMapper;

    private final Function<RpAccountVo, String> getEntityName = (RpAccountVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询账号信息
     *
     * @param id 主键
     * @return 账号信息
     */
    @Override
    public RpAccountVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询账号信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 账号信息分页列表
     */
    @Override
    public TableDataInfo<RpAccountVo> queryPageList(RpAccountBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpAccount> lqw = buildQueryWrapper(bo);
        Page<RpAccountVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(item -> {
            item.setExtraInfo("");
        });
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的账号信息列表
     *
     * @param bo 查询条件
     * @return 账号信息列表
     */
    @Override
    public List<RpAccountVo> queryList(RpAccountBo bo) {
        LambdaQueryWrapper<RpAccount> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpAccount> buildQueryWrapper(RpAccountBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpAccount> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpAccount::getId);
        lqw.like(StringUtils.isNotBlank(bo.getAccountName()), RpAccount::getAccountName, bo.getAccountName());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceCode()), RpAccount::getDeviceCode, bo.getDeviceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), RpAccount::getPlatform, bo.getPlatform());
        lqw.eq(bo.getGroupId() != null, RpAccount::getGroupId, bo.getGroupId());
        lqw.eq(bo.getStatus() != null, RpAccount::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getExtraInfo()), RpAccount::getExtraInfo, bo.getExtraInfo());
        return lqw;
    }

    /**
     * 新增账号信息
     *
     * @param bo 账号信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpAccount(RpAccountBo bo) {
        RpAccount add = MapstructUtils.convert(bo, RpAccount.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改账号信息
     *
     * @param bo 账号信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpAccount(RpAccountBo bo) {
        RpAccount update = MapstructUtils.convert(bo, RpAccount.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpAccount entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除账号信息信息
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
     * 批量导入账号信息数据
     * @param rpAccountList 账号信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpAccount(List<RpAccountVo> rpAccountList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpAccountList,
            updateSupport,
            operatorName,
            this::importRpAccountInfo,
            getEntityName
        );
    }

    /**
     * 导入账号信息信息
     * @param rpAccountVo 账号信息信息
     * @param operatorName 操作人
     */
    private void importRpAccountInfo(RpAccountVo rpAccountVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpAccountBo rpAccountBo = BeanUtil.toBean(rpAccountVo, RpAccountBo.class);
        if (rpAccountBo.getId() == null){
            ValidatorUtils.validate(rpAccountBo, AddGroup.class);
            this.insertRpAccount(rpAccountBo);
        }else {
            ValidatorUtils.validate(rpAccountBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpAccount rpAccount = Db.getById(rpAccountBo.getId(), RpAccount.class);
            if (rpAccount == null){
                rpAccountBo.setId(null);
                this.insertRpAccount(rpAccountBo);
                return;
            }
            this.updateRpAccount(rpAccountBo);
        }
    }
}

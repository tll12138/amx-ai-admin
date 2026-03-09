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
import org.dromara.rp.domain.bo.RpBitAccountBo;
import org.dromara.rp.domain.vo.RpBitAccountVo;
import org.dromara.rp.domain.RpBitAccount;
import org.dromara.rp.mapper.RpBitAccountMapper;
import org.dromara.rp.service.IRpBitAccountService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.function.Function;

/**
 * 比特账号信息Service业务层处理
 *
 * @author LL
 * @date 2026-03-02
 */
@RequiredArgsConstructor
@Service
public class RpBitAccountServiceImpl implements IRpBitAccountService {

    private final RpBitAccountMapper baseMapper;

    private final Function<RpBitAccountVo, String> getEntityName = (RpBitAccountVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询比特账号信息
     *
     * @param id 主键
     * @return 比特账号信息
     */
    @Override
    public RpBitAccountVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询比特账号信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比特账号信息分页列表
     */
    @Override
    public TableDataInfo<RpBitAccountVo> queryPageList(RpBitAccountBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpBitAccount> lqw = buildQueryWrapper(bo);
        Page<RpBitAccountVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的比特账号信息列表
     *
     * @param bo 查询条件
     * @return 比特账号信息列表
     */
    @Override
    public List<RpBitAccountVo> queryList(RpBitAccountBo bo) {
        LambdaQueryWrapper<RpBitAccount> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpBitAccount> buildQueryWrapper(RpBitAccountBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpBitAccount> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpBitAccount::getId);
        lqw.like(StringUtils.isNotBlank(bo.getAccountName()), RpBitAccount::getAccountName, bo.getAccountName());
        lqw.eq(StringUtils.isNotBlank(bo.getAccountCode()), RpBitAccount::getAccountCode, bo.getAccountCode());
        lqw.eq(bo.getStatus() != null, RpBitAccount::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增比特账号信息
     *
     * @param bo 比特账号信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpBitAccount(RpBitAccountBo bo) {
        RpBitAccount add = MapstructUtils.convert(bo, RpBitAccount.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改比特账号信息
     *
     * @param bo 比特账号信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpBitAccount(RpBitAccountBo bo) {
        RpBitAccount update = MapstructUtils.convert(bo, RpBitAccount.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpBitAccount entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除比特账号信息信息
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
     * 批量导入比特账号信息数据
     * @param rpBitAccountList 比特账号信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpBitAccount(List<RpBitAccountVo> rpBitAccountList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpBitAccountList,
            updateSupport,
            operatorName,
            this::importRpBitAccountInfo,
            getEntityName
        );
    }

    /**
     * 导入比特账号信息信息
     * @param rpBitAccountVo 比特账号信息信息
     * @param operatorName 操作人
     */
    private void importRpBitAccountInfo(RpBitAccountVo rpBitAccountVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpBitAccountBo rpBitAccountBo = BeanUtil.toBean(rpBitAccountVo, RpBitAccountBo.class);
        if (rpBitAccountBo.getId() == null){
            ValidatorUtils.validate(rpBitAccountBo, AddGroup.class);
            this.insertRpBitAccount(rpBitAccountBo);
        }else {
            ValidatorUtils.validate(rpBitAccountBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpBitAccount rpBitAccount = Db.getById(rpBitAccountBo.getId(), RpBitAccount.class);
            if (rpBitAccount == null){
                rpBitAccountBo.setId(null);
                this.insertRpBitAccount(rpBitAccountBo);
                return;
            }
            this.updateRpBitAccount(rpBitAccountBo);
        }
    }
}

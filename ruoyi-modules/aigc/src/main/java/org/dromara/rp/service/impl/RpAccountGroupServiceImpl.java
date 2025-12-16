package org.dromara.rp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.rp.domain.RpAccountGroup;
import org.dromara.rp.domain.bo.RpAccountGroupBo;
import org.dromara.common.core.utils.dropdown.common.DropDownOptionsGroup;
import org.dromara.rp.domain.vo.RpAccountGroupVo;
import org.dromara.rp.mapper.RpAccountGroupMapper;
import org.dromara.rp.mapper.RpAccountMapper;
import org.dromara.rp.domain.RpAccount;
import org.dromara.rp.domain.vo.RpAccountBriefVo;
import org.dromara.rp.domain.vo.RpAccountGroupWithAccountsVo;
import org.dromara.rp.service.IRpAccountGroupService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.dromara.common.core.utils.dropdown.DropdownConverter.convertToDropdown;

/**
 * 账号分组Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@RequiredArgsConstructor
@Service
public class RpAccountGroupServiceImpl implements IRpAccountGroupService {

    private final RpAccountGroupMapper baseMapper;
    private final RpAccountMapper accountMapper;

    private final Function<RpAccountGroupVo, Long> getEntityID = (RpAccountGroupVo entity) -> {
        if (entity == null) {
            return -1L;
        }
        return entity.getId() == null ? -1 : entity.getId();
    };

    /**
     * 查询账号分组
     *
     * @param id 主键
     * @return 账号分组
     */
    @Override
    public RpAccountGroupVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询账号分组列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 账号分组分页列表
     */
    @Override
    public TableDataInfo<RpAccountGroupVo> queryPageList(RpAccountGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpAccountGroup> lqw = buildQueryWrapper(bo);
        Page<RpAccountGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的账号分组列表
     *
     * @param bo 查询条件
     * @return 账号分组列表
     */
    @Override
    public List<RpAccountGroupVo> queryList(RpAccountGroupBo bo) {
        LambdaQueryWrapper<RpAccountGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<DropDownOptionsGroup<Long>> getSelectOptions() {
        LambdaQueryWrapper<RpAccountGroup> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpAccountGroup::getUpdateTime);
        List<RpAccountGroupVo> list = baseMapper.selectVoList(lqw);
        return convertToDropdown(list, RpAccountGroupVo::getPlatform, RpAccountGroupVo::getGroupName, getEntityID);
    }

    private LambdaQueryWrapper<RpAccountGroup> buildQueryWrapper(RpAccountGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpAccountGroup> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpAccountGroup::getId);
        lqw.like(StringUtils.isNotBlank(bo.getGroupName()), RpAccountGroup::getGroupName, bo.getGroupName());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), RpAccountGroup::getPlatform, bo.getPlatform());
        lqw.eq(StringUtils.isNotBlank(bo.getExtraInfo()), RpAccountGroup::getExtraInfo, bo.getExtraInfo());
        return lqw;
    }

    /**
     * 新增账号分组
     *
     * @param bo 账号分组
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpAccountGroup(RpAccountGroupBo bo) {
        RpAccountGroup add = MapstructUtils.convert(bo, RpAccountGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改账号分组
     *
     * @param bo 账号分组
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpAccountGroup(RpAccountGroupBo bo) {
        RpAccountGroup update = MapstructUtils.convert(bo, RpAccountGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpAccountGroup entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除账号分组信息
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

    @Override
    public List<RpAccountGroupWithAccountsVo> listGroupsWithAccountsByPlatform(String platform) {
        LambdaQueryWrapper<RpAccountGroup> groupQuery = Wrappers.lambdaQuery();
        groupQuery.eq(RpAccountGroup::getPlatform, platform);
        groupQuery.orderByAsc(RpAccountGroup::getCreateTime);
        List<RpAccountGroupVo> groups = baseMapper.selectVoList(groupQuery);

        if (groups.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groups.stream()
            .map(RpAccountGroupVo::getId)
            .filter(id -> id != null)
            .toList();

        LambdaQueryWrapper<RpAccount> accountQuery = Wrappers.lambdaQuery();
        accountQuery.in(RpAccount::getGroupId, groupIds);
        accountQuery.eq(RpAccount::getPlatform, platform);
        List<RpAccount> accounts = accountMapper.selectList(accountQuery);

        Map<Long, List<RpAccount>> groupedAccounts = accounts.stream()
            .filter(a -> a.getGroupId() != null)
            .collect(java.util.stream.Collectors.groupingBy(RpAccount::getGroupId));

        List<RpAccountGroupWithAccountsVo> result = new java.util.ArrayList<>();
        for (RpAccountGroupVo g : groups) {
            List<RpAccount> accs = groupedAccounts.getOrDefault(g.getId(), java.util.Collections.emptyList());
            accs.sort((o1, o2) -> {
                String c1 = o1.getDeviceCode();
                String c2 = o2.getDeviceCode();
                if (org.dromara.common.core.utils.StringUtils.isNotEmpty(c1) && org.dromara.common.core.utils.StringUtils.isNotEmpty(c2)) {
                    return c1.compareToIgnoreCase(c2);
                }
                if (org.dromara.common.core.utils.StringUtils.isNotEmpty(c1)) {
                    return -1;
                }
                if (org.dromara.common.core.utils.StringUtils.isNotEmpty(c2)) {
                    return 1;
                }
                java.util.Date t1 = o1.getCreateTime();
                java.util.Date t2 = o2.getCreateTime();
                if (t1 == null && t2 == null) {
                    return 0;
                }
                if (t1 == null) {
                    return 1;
                }
                if (t2 == null) {
                    return -1;
                }
                return t1.compareTo(t2);
            });

            List<RpAccountBriefVo> briefList = new java.util.ArrayList<>(accs.size());
            for (RpAccount a : accs) {
                RpAccountBriefVo brief = new RpAccountBriefVo();
                brief.setId(a.getId());
                brief.setAccountName(a.getAccountName());
                brief.setDeviceCode(a.getDeviceCode());
                briefList.add(brief);
            }

            RpAccountGroupWithAccountsVo item = new RpAccountGroupWithAccountsVo();
            item.setId(g.getId());
            item.setGroupName(g.getGroupName());
            item.setAccounts(briefList);
            result.add(item);
        }

        return result;
    }
}

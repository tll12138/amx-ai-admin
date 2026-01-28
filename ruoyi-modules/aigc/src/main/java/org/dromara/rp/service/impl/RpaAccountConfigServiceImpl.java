package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.utils.YDUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.rp.domain.RpaAccountConfig;
import org.dromara.rp.domain.bo.RpaAccountConfigBo;
import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.mapper.RpaAccountConfigMapper;
import org.dromara.rp.service.IRpaAccountConfigService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

/**
 * RPA账号配置Service业务层处理
 *
 * @author LL
 * @date 2026-01-28
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RpaAccountConfigServiceImpl implements IRpaAccountConfigService {

    private final RpaAccountConfigMapper baseMapper;

    private final YDUtils ydUtils;

    private final Function<RpaAccountConfigVo, String> getEntityName = (RpaAccountConfigVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询RPA账号配置
     *
     * @param id 主键
     * @return RPA账号配置
     */
    @Override
    public RpaAccountConfigVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询RPA账号配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return RPA账号配置分页列表
     */
    @Override
    public TableDataInfo<RpaAccountConfigVo> queryPageList(RpaAccountConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpaAccountConfig> lqw = buildQueryWrapper(bo);
        Page<RpaAccountConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的RPA账号配置列表
     *
     * @param bo 查询条件
     * @return RPA账号配置列表
     */
    @Override
    public List<RpaAccountConfigVo> queryList(RpaAccountConfigBo bo) {
        LambdaQueryWrapper<RpaAccountConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpaAccountConfig> buildQueryWrapper(RpaAccountConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpaAccountConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpaAccountConfig::getId);
        lqw.like(StringUtils.isNotBlank(bo.getRobotClientName()), RpaAccountConfig::getRobotClientName, bo.getRobotClientName());
        lqw.eq(StringUtils.isNotBlank(bo.getRobotClientUuid()), RpaAccountConfig::getRobotClientUuid, bo.getRobotClientUuid());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), RpaAccountConfig::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增RPA账号配置
     *
     * @param bo RPA账号配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpaAccountConfig(RpaAccountConfigBo bo) {
        RpaAccountConfig add = MapstructUtils.convert(bo, RpaAccountConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改RPA账号配置
     *
     * @param bo RPA账号配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpaAccountConfig(RpaAccountConfigBo bo) {
        RpaAccountConfig update = MapstructUtils.convert(bo, RpaAccountConfig.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpaAccountConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除RPA账号配置信息
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
     * 批量导入RPA账号配置数据
     * @param rpaAccountConfigList RPA账号配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpaAccountConfig(List<RpaAccountConfigVo> rpaAccountConfigList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpaAccountConfigList,
            updateSupport,
            operatorName,
            this::importRpaAccountConfigInfo,
            getEntityName
        );
    }

    /**
     * 定时获取RPA机器人账号存库
     */
    @Override
    public void getRpaRobotAccount() {
        // 1. 获取机器人列表
        JSONObject ydRobotListJson = ydUtils.getYDRobotList();
        if (ydRobotListJson == null) {
            log.warn("获取远端机器人列表返回null，终止账号同步");
            return;
        }

        // 2. 获取data数组
        JSONArray dataArray = ydRobotListJson.getJSONArray("data");
        if (dataArray == null || dataArray.isEmpty()) {
            log.info("远端机器人列表为空，无需同步");
            return;
        }

        // 3. 遍历每条机器人数据，执行新增/更新逻辑
        dataArray.forEach(item -> {
            // 转换为JSONObject
            if (!(item instanceof JSONObject robotItem)) {
                log.error("机器人列表项格式异常，跳过：{}", item);
                return;
            }

            // 4. 提取核心字段（空值处理）
            String robotClientUuid = robotItem.getStr("robotClientUuid");
            String robotClientName = robotItem.getStr("robotClientName");
            String status = robotItem.getStr("status");
            String description = robotItem.getStr("description");
            status = "offline".equals(status)?"1":"0";

            // 关键字段为空则跳过
            if (robotClientUuid == null || robotClientUuid.trim().isEmpty()) {
                log.warn("机器人唯一标识为空，跳过该条数据");
                return;
            }

            LambdaQueryWrapper<RpaAccountConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RpaAccountConfig::getRobotClientUuid, robotClientUuid);
            RpaAccountConfig dbAccount = baseMapper.selectOne(queryWrapper);

            // 6. 存在则更新，不存在则新增
            RpaAccountConfig account = new RpaAccountConfig();
            if (dbAccount != null) {
                account = dbAccount;
                account.setRobotClientName(robotClientName);
                account.setStatus(status);
                account.setRemark(description);
                baseMapper.updateById(account);
                log.info("更新机器人账号：{}", robotClientUuid);
            } else {
                // 不存在：填充所有字段，新增
                account.setRobotClientUuid(robotClientUuid);
                account.setRobotClientName(robotClientName);
                account.setStatus(status);
                account.setRemark(description);
                baseMapper.insert(account);
                log.info("新增机器人账号：{}", robotClientUuid);
            }
        });
    }

    /**
     * 导入RPA账号配置信息
     * @param rpaAccountConfigVo RPA账号配置信息
     * @param operatorName 操作人
     */
    private void importRpaAccountConfigInfo(RpaAccountConfigVo rpaAccountConfigVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpaAccountConfigBo rpaAccountConfigBo = BeanUtil.toBean(rpaAccountConfigVo, RpaAccountConfigBo.class);
        if (rpaAccountConfigBo.getId() == null){
            ValidatorUtils.validate(rpaAccountConfigBo, AddGroup.class);
            this.insertRpaAccountConfig(rpaAccountConfigBo);
        }else {
            ValidatorUtils.validate(rpaAccountConfigBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpaAccountConfig rpaAccountConfig = Db.getById(rpaAccountConfigBo.getId(), RpaAccountConfig.class);
            if (rpaAccountConfig == null){
                rpaAccountConfigBo.setId(null);
                this.insertRpaAccountConfig(rpaAccountConfigBo);
                return;
            }
            this.updateRpaAccountConfig(rpaAccountConfigBo);
        }
    }
}

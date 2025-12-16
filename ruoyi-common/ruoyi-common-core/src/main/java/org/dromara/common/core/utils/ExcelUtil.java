package org.dromara.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import jakarta.validation.Validator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author zrl
 * @date 2025/1/8
 */
@Slf4j
public class ExcelUtil {

    /**
     * 通用批量导入实体列表的静态方法.
     *
     * @param <T> 实体类型.
     * @param entityList 实体列表.
     * @param validator 用于验证实体的 Validator 实例.
     * @param updateSupport 是否更新数据.
     * @param operatorName 操作者名称.
     * @param insertAction 插入实体的方法.
     * @param getEntityName 获取实体唯一标识的方法，用于展示插入结果.
     * @return 导入结果详情内容.
     */
    public static <T> String ImportEntities(
            List<T> entityList,
//            Validator validator,
            boolean updateSupport,
            String operatorName,
            BiConsumer<T, String> insertAction,
            Function<T, String> getEntityName
    ) {
        if (entityList == null || entityList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (T entity : entityList) {
            try {
//                BeanValidators.validateNoArg(validator, entity);
                insertAction.accept(entity, operatorName);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、").append(getEntityName.apply(entity)).append(" 导入成功");
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、" + getEntityName.apply(entity) + " 导入失败： ";
                failureMsg.append(msg).append(e.getMessage());
                log.error(msg, e);
            }
        }

        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        log.info("导入结束...");
        return successMsg.toString();
    }
}

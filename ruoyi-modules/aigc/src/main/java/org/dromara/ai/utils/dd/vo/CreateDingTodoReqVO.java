package org.dromara.ai.utils.dd.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zrl
 * @date 2024/10/31
 */
@Data
@Builder
public class CreateDingTodoReqVO {

    /**
     * 是否仅执行者 可见
     */
    public Boolean isOnlyShowExecutor;
    /**
     * 发起人 unionId
     */
    private String creator;
    /**
     * 审批人 unionId  执行人列表
     */
    private List<String> executor;
    /**
     * 参与人 unionId  参与人列表
     */
    private List<String> participantIds;
    /**
     * 移动端路径
     */
    private String appUrl;
    /**
     * PC路径
     */
    private String pcUrl;
    /**
     * 待办ID
     */
    private String sourceId;
    /**
     * 代办标题
     */
    private String title;
    /**
     * 代办描述
     */
    private String description;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 截止时间
     */
    private Long dueTime;


    /**
     * 创建人名称
     */
    private String createUserName;





}

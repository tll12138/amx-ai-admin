package org.dromara.rp.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.model.ExecuteResult;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.rp.service.IRpaAccountConfigService;
import org.springframework.stereotype.Component;

/**
 * 定时获取影刀机器人列表
 * @author tll
 * @date 2026-01-28 11:11:09
 */
@Slf4j
@Component
@JobExecutor(name = "RpRobotApiJob")
@RequiredArgsConstructor
public class RpRobotApiJob {
    @Resource
    private IRpaAccountConfigService service;
    public ExecuteResult jobExecute() {
        log.info("开始执行定时获取影刀机器人列表任务");
        try {
            // 执行任务
            service.getRpaRobotAccount();
            log.info("定时获取影刀机器人列表任务执行完成");
            return ExecuteResult.success("定时获取影刀机器人列表任务执行成功");
        }catch (Exception e){
            log.error("定时获取影刀机器人列表任务执行失败: {}", e.getMessage(), e);
            return ExecuteResult.failure("定时获取影刀机器人列表任务执行失败: " + e.getMessage());
        }
    }
}

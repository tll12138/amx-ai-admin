package org.dromara.ai.utils;

import lombok.Getter;

import java.util.concurrent.*;


public class AIThreadPoolExecutor {

    // 创建自定义线程池
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 30;
    private static final long KEEP_ALIVE_TIME = 30L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int WORK_QUEUE_CAPACITY = 150;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY);
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.AbortPolicy();

    // 提供获取线程池实例的方法
    @Getter
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            WORK_QUEUE,
            REJECTED_EXECUTION_HANDLER
    );

}

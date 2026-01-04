package io.zerows.epoch.assembly;

import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 极致性能优化版 - Event 扫描器
 * 1. 移除 EndPointThread，减少对象开销。
 * 2. 静态复用 Extractor，消除反射损耗。
 * 3. 虚拟线程 + 并发容器，实现最大吞吐量与数据强一致性。
 */
@Slf4j
public class InquirerForEvent implements Inquirer<Set<WebEvent>> {

    // 🚀 全局复用提取器
    private static final Extractor<Set<WebEvent>> EXTRACTOR = Ut.instance(ExtractorEvent.class);

    // 日志模板
    private static final String LOG_MSG = "[ ZERO ] ( {} Event ) ---> @EndPoint 端对象 {} 包含 {} Events 定义！";

    @Override
    public Set<WebEvent> scan(final Set<Class<?>> endpoints) {
        if (endpoints == null || endpoints.isEmpty()) {
            return Collections.emptySet();
        }

        // 🚀 使用并发 Set 直接聚合结果，避免后续串行 merge
        // 预估大小 * 4 是为了减少扩容操作，WebEvent 通常数量较多
        final Set<WebEvent> totalEvents = ConcurrentHashMap.newKeySet(endpoints.size() * 4);

        // 🚀 虚拟线程池：瞬间分发所有扫描任务
        // try-with-resources 自动执行 join()，保证方法返回时所有线程已结束，数据绝对一致
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (final Class<?> endpoint : endpoints) {
                executor.submit(() -> this.executeScan(endpoint, totalEvents));
            }
        }

        return totalEvents;
    }

    private void executeScan(final Class<?> endpoint, final Set<WebEvent> target) {
        try {
            final Set<WebEvent> result = EXTRACTOR.extract(endpoint);
            if (result != null && !result.isEmpty()) {
                // 并发写入，无锁高性能
                target.addAll(result);
                log.info(LOG_MSG, result.size(), endpoint.getName(), result.size());
            }
        } catch (final Throwable e) {
            log.error("[ ZERO ] EndPoint 扫描失败: {}", endpoint.getName(), e);
        }
    }
}
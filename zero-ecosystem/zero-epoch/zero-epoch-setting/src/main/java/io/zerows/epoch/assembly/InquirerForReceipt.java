package io.zerows.epoch.assembly;

import io.zerows.epoch.jigsaw.Inquirer;
import io.zerows.epoch.web.WebReceipt;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 极致性能优化版 - Receipt 扫描器
 * 1. 移除 QueueThread，减少对象分配。
 * 2. 静态复用 Extractor，避免重复反射创建。
 * 3. 虚拟线程并发 + ConcurrentHashMap 聚合，速度最快且一致性强。
 */
@Slf4j
public class InquirerForReceipt implements Inquirer<Set<WebReceipt>> {

    // 🚀 性能关键点1：全局复用，避免每次扫描都 create instance
    private static final Extractor<Set<WebReceipt>> EXTRACTOR = Ut.instance(ExtractorReceipt.class);

    // 日志模板预编译
    private static final String LOG_MSG = "[ ZERO ] ( {} Receipt ) <--- @Queue 队列对象 {} 包含了 {} Receipt 定义！ ";

    @Override
    public Set<WebReceipt> scan(final Set<Class<?>> queues) {
        if (queues == null || queues.isEmpty()) {
            return Collections.emptySet();
        }

        // 🚀 性能关键点2：直接使用并发 Set，写入时自动同步，不需要后续再 merge
        final Set<WebReceipt> totalReceipts = ConcurrentHashMap.newKeySet(queues.size() * 2);

        // 🚀 性能关键点3：虚拟线程池 (Java 21+)
        // try-with-resources 语法糖保证了代码块结束前，所有线程自动 join，保证数据完整性 (一致性)
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (final Class<?> queue : queues) {
                executor.submit(() -> this.executeScan(queue, totalReceipts));
            }
        }
        // 此时所有任务已完成，totalReceipts 包含了完整数据

        return totalReceipts;
    }

    private void executeScan(final Class<?> queue, final Set<WebReceipt> target) {
        try {
            final Set<WebReceipt> result = EXTRACTOR.extract(queue);
            if (result != null && !result.isEmpty()) {
                // 并发写入，线程安全
                target.addAll(result);
                log.info(LOG_MSG, result.size(), queue.getName(), result.size());
            }
        } catch (final Throwable e) {
            // 捕获异常防止中断整个扫描流程，保证整体数据一致性
            log.error("[ ZERO ] 队列扫描失败: {}", queue.getName(), e);
        }
    }
}
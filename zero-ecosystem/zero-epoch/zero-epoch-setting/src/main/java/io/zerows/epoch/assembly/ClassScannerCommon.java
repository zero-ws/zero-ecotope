package io.zerows.epoch.assembly;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 扫描加速版 V7 🚀🔥 (Broad-Scan + Batching)
 * <p>
 * 场景适配：
 * 1. 【广域扫描】保留 rejectPackages (黑名单)，适应未知包结构的复杂环境。
 * 2. 【批量处理】应对广域扫描可能带来的海量类数量，使用分片 (Batching) 降低调度开销。
 * 3. 【虚拟线程】利用 Java 21 虚拟线程的高吞吐特性处理 ClassLoader IO。
 * 4. 【延迟加载】loadClass(false) 避免初始化静态块，提升速度并防止副作用。
 */
@Slf4j
@SuppressWarnings("all")
class ClassScannerCommon implements ClassScanner {

    // 批处理大小：广域扫描下类数量可能很大，适当调大 Batch 减少任务总数
    // 64-128 是个不错的平衡点，既能利用并发，又不会让任务队列爆炸
    private static final int BATCH_SIZE = 128;

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final long t0 = System.nanoTime();

        // 1. 获取黑名单配置 (必须保留，用于剔除明确不需要的第三方库)
        final String[] skipPackages = ClassFilterPackage.SKIP_PACKAGE;

        // 2. ClassGraph 扫描配置
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()               // 必须开启
            .rejectPackages(skipPackages)    // 🚫 核心：黑名单过滤
            .ignoreClassVisibility()         // 扫描所有修饰符
            .enableExternalClasses()         // 确保能扫描到非 System Loader 的类 (视环境而定，通常建议开启)
            .scan()) {

            final ClassInfoList allClassInfo = scanResult.getAllClasses();
            final int totalClasses = allClassInfo.size();

            // 结果容器：预估大小以减少扩容开销
            final Set<Class<?>> result = ConcurrentHashMap.newKeySet(totalClasses);

            if (totalClasses == 0) {
                return result;
            }

            // 3. 🚀 启动虚拟线程池
            // 广域扫描可能会产生数万个类，使用 Batching 模式至关重要
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

                // 手动切片 (Chunking)
                for (int i = 0; i < totalClasses; i += BATCH_SIZE) {
                    final int start = i;
                    final int end = Math.min(i + BATCH_SIZE, totalClasses);

                    // 提交批处理任务
                    executor.submit(() -> processBatch(allClassInfo, start, end, result));
                }

            } // 自动阻塞等待所有分片完成 (Auto Join)

            final long t1 = System.nanoTime();
            log.info("[ ZERO ] 扫描完成：{}/{}，总耗时={} ms (模式: BroadScan-VThreads🚀)",
                result.size(), totalClasses, (t1 - t0) / 1_000_000L);

            return result;

        } catch (Exception e) {
            log.warn("[ ZERO ] ClassGraph 扫描异常", e);
            return Collections.emptySet();
        }
    }

    /**
     * 批处理逻辑：在单个虚拟线程内串行加载一批类
     * 优势：
     * 1. 减少 CPU 上下文切换（同线程处理一组数据）。
     * 2. 减少 ConcurrentHashMap 的 CAS 写入竞争次数（从 N 次降为 N/BATCH_SIZE 次）。
     */
    private void processBatch(ClassInfoList allInfo, int start, int end, Set<Class<?>> globalResult) {
        // 线程私有 Buffer (无锁，极快)
        final List<Class<?>> localBuffer = new ArrayList<>(end - start);

        for (int i = start; i < end; i++) {
            try {
                final ClassInfo ci = allInfo.get(i);
                // 延迟加载：不初始化 static {} 代码块，这对广域扫描的安全性和速度至关重要
                final Class<?> cls = ci.loadClass(false);

                // 业务过滤
                if (cls != null && ClassFilter.isValid(cls)) {
                    localBuffer.add(cls);
                }
            } catch (Throwable ignored) {
                // 广域扫描时，遇到 NoClassDefFoundError 或依赖缺失非常常见，直接静默跳过
            }
        }

        // 批量写入全局容器
        if (!localBuffer.isEmpty()) {
            globalResult.addAll(localBuffer);
        }
    }
}
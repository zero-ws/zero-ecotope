package io.zerows.platform.metadata;

import io.zerows.specification.atomic.HThread;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Multi thread helper tool based on Java 21 Virtual Threads.
 * <p>
 * 优化点：
 * 1. 全面使用虚拟线程 (Virtual Threads)，轻量且高效。
 * 2. 引入 ExecutorService + try-with-resources 实现结构化并发，自动处理 join 等待。
 * 3. 移除了 Keeper 线程的额外开销（除非是为了模拟非守护线程）。
 * 4. 消除手动 new Thread() 和 join()，减少出错概率。
 */
@Slf4j
public final class KRunner {

    /**
     * 启动一个独立的虚拟线程任务（模拟非守护线程行为）。
     * <p>
     * 原理：虚拟线程默认是 Daemon 线程，JVM 退出时会直接终止。
     * 如果需要任务像平台线程一样阻止 JVM 退出，仍需一个 Keeper，但我们可以简化写法。
     *
     * @param hooker 任务逻辑
     * @param name   线程名称
     * @return Keeper 线程引用（平台线程）
     */
    public static Thread run(final Runnable hooker, final String name) {
        Objects.requireNonNull(hooker, "hooker cannot be null");

        // 创建一个平台线程作为 Keeper，阻塞等待虚拟线程完成
        // 这是为了保持与你原代码逻辑一致（防止 main 结束导致虚拟线程被杀）
        final Thread keeper = new Thread(() -> {
            final String vName = name + "-v";
            // 启动虚拟线程
            final Thread vThread = Thread.ofVirtual().name(vName).start(hooker);
            try {
                vThread.join();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[KRunner] Task interrupted: {}", name);
            } catch (final Throwable t) {
                log.error("[KRunner] Task error: {}", name, t);
            }
        }, name);

        keeper.setDaemon(false); // 关键：确保 Keeper 不是 Daemon
        keeper.start();
        return keeper;
    }

    /**
     * 并发执行多个 Runnable 任务，并阻塞直到所有任务完成。
     *
     * @param name    线程名前缀
     * @param hookers 任务列表
     */
    public static void run(final String name, final Runnable... hookers) {
        if (hookers == null || hookers.length == 0) {
            return;
        }

        // 自定义线程工厂，用于设置名称
        final ThreadFactory factory = Thread.ofVirtual().name(name + "-", 0).factory();

        // 使用 try-with-resources，代码块结束时会自动调用 close()，
        // 而 close() 会隐式等待所有提交的任务完成 (Structured Concurrency)
        try (final ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {
            for (final Runnable hooker : hookers) {
                executor.submit(() -> executeSafe(hooker));
            }
        } // 在这里阻塞，直到所有虚拟线程执行完毕
    }

    /**
     * 并发处理集合中的数据，并阻塞直到所有处理完成。
     * <p>
     * 优化：原代码使用 new Thread() 会在集合很大时导致资源耗尽。
     * 现改用虚拟线程池，支持百万级并发。
     *
     * @param inputSet 输入集合
     * @param consumer 消费逻辑
     * @param <T>      泛型
     */
    public static <T> void run(final Collection<T> inputSet, final Consumer<T> consumer) {
        if (inputSet == null || inputSet.isEmpty()) {
            return;
        }

        try (final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (final T item : inputSet) {
                executor.submit(() -> {
                    try {
                        consumer.accept(item);
                    } catch (final Throwable t) {
                        log.error("[KRunner] Batch consumer error", t);
                    }
                });
            }
        } // 自动 join
    }

    /**
     * 并发执行 HThread 任务并收集结果。
     *
     * @param meanThreads 任务对象列表
     * @param result      结果容器
     * @param <T>         结果泛型
     */
    public static <T> void run(final List<HThread<T>> meanThreads,
                               final ConcurrentMap<String, T> result) {
        if (meanThreads == null || meanThreads.isEmpty()) {
            return;
        }

        try (final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (final HThread<T> meanThread : meanThreads) {
                executor.submit(() -> {
                    try {
                        // 1. 执行任务逻辑 (HThread 也是 Runnable)
                        meanThread.run();

                        // 2. 收集结果 (任务完成后立即放入 Map，不需要等所有线程结束再遍历)
                        // 这样可以减少一次循环遍历
                        final String key = meanThread.name();
                        final T value = meanThread.get();
                        if (key != null && value != null) {
                            result.put(key, value);
                        }
                    } catch (final Throwable t) {
                        log.error("[KRunner] HThread execution error: {}", meanThread.name(), t);
                    }
                });
            }
        } // 自动等待所有任务完成
    }

    // --- 内部辅助 ---

    private static void executeSafe(final Runnable task) {
        try {
            task.run();
        } catch (final Throwable t) {
            log.error("[KRunner] Async task error", t);
        }
    }
}
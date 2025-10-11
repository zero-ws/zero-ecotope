package io.zerows.platform.metadata;

import io.r2mo.function.Fn;
import io.zerows.specification.atomic.HThread;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Multi thread helper tool to do some multi-thread works.
 */
@Slf4j
public final class KRunner {

    // Execute single thread
    public static Thread run(final Runnable hooker, final String name) {
        Objects.requireNonNull(hooker, "hooker cannot be null");

        // 1) 先创建未启动的虚拟线程
        final Thread vThread = Thread.ofVirtual().name(name).unstarted(hooker);

        // 2) 再创建一个“keeper”平台线程去启动并等待虚拟线程结束
        //    平台线程是非守护线程，能像你之前的实现一样撑住 JVM 生命周期
        final Thread keeper = new Thread(() -> {
            try {
                vThread.start();
                vThread.join();
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("[KRunner] keeper 被中断: {}", name);
            } catch (final Throwable t) {
                log.error("[KRunner] 任务异常: {}", name, t);
            }
        }, name + "-keeper");

        // 显式确保是非守护线程（默认如此，这里再次强调）
        keeper.setDaemon(false);
        keeper.start();

        // 返回 keeper，保持与旧版返回“可 join 的线程”一致的用法
        return keeper;
    }

    // Execute multi thread
    public static void run(final String name, final Runnable... hookers) {
        final List<Thread> threads = new ArrayList<>();
        for (int idx = 0; idx < hookers.length; idx++) {
            final String threadName = name + "-" + idx;
            final Runnable hooker = hookers[idx];
            threads.add(run(hooker, threadName));
        }
        Fn.jvmAt(() -> {
            for (final Thread thread : threads) {
                thread.join();
            }
        });
    }

    public static <T> void run(final Set<T> inputSet, final Consumer<T> consumer) {
        final Set<Thread> threads = new HashSet<>();
        inputSet.forEach(item -> {
            final Thread thread = new Thread(() -> consumer.accept(item));
            thread.start();
            threads.add(thread);
        });
        Fn.jvmAt(() -> {
            for (final Thread thread : threads) {
                thread.join();
            }
        });
    }

    public static <T> void run(final List<HThread<T>> meanThreads,
                               final ConcurrentMap<String, T> result) {
        final List<Thread> references = new ArrayList<>();
        for (final HThread<T> meanThread : meanThreads) {
            final Thread thread = new Thread(meanThread);
            references.add(thread);
            thread.start();
        }
        references.forEach(item -> {
            try {
                item.join();
            } catch (final InterruptedException ex) {
                log.error("[ ZERO ] 多线程等待异常", ex);
            }
        });
        for (final HThread<T> meanThread : meanThreads) {
            final String key = meanThread.name();
            final T value = meanThread.get();
            result.put(key, value);
        }
    }
}

package io.zerows.epoch.corpus.model.running;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 全局唯一的一个，用于存储启动的线程名称
 *
 * @author lang : 2024-05-04
 */
public class RunThread {
    private static final Set<String> AGENTS = new HashSet<>();
    private static final Set<String> WORKERS = new HashSet<>();
    private static RunThread INSTANCE;

    private RunThread() {
    }

    public static RunThread one() {
        synchronized (RunThread.class) {
            if (Objects.isNull(INSTANCE)) {
                INSTANCE = new RunThread();
            }
            return INSTANCE;
        }
    }

    public void increase(final boolean isAgent) {
        if (isAgent) {
            AGENTS.add(Thread.currentThread().getName());
        } else {
            WORKERS.add(Thread.currentThread().getName());
        }
    }

    public Set<String> agents() {
        return AGENTS;
    }

    public Set<String> workers() {
        return WORKERS;
    }
}

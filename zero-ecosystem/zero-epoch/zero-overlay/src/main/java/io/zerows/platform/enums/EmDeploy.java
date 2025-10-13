package io.zerows.platform.enums;

/**
 * @author lang : 2024-04-20
 */
public final class EmDeploy {
    private EmDeploy() {
    }

    public enum Mode {
        CONFIG, // Configuration
        CODE,   // Programming
    }

    public enum Component {
        AGENT,          // Agent
        WORKER,         // Worker
        SCHEDULER,      // Scheduler
    }

    public enum JoinPoint {
        IPC,
        QAS
    }
}

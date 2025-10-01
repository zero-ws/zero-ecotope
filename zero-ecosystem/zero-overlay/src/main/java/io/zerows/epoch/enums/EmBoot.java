package io.zerows.epoch.enums;

/**
 * @author lang : 2023-05-31
 */
public final class EmBoot {
    private EmBoot() {
    }

    /**
     * @author lang : 2023-05-30
     */
    public enum LifeCycle {
        PRE,    // pre 组件，比较特殊的组件
        ON,     // install, resolved, start
        OFF,    // stop, uninstall
        RUN;    // run, update, refresh

        public static LifeCycle from(final String name) {
            return LifeCycle.valueOf(name.toUpperCase());
        }
    }
}

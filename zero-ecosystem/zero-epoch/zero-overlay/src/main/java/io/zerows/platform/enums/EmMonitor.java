package io.zerows.platform.enums;

/**
 * @author lang : 2024-04-17
 */
public final class EmMonitor {

    private EmMonitor() {
    }

    public enum Type {
        CONSTANT,       // 常量接口
        AMBIGUITY,      // 双环境接口
    }
}

package io.zerows.core.exception;

import io.zerows.core.spi.HorizonIo;

/**
 * 顶层异常
 */
public abstract class ProgramException extends Exception {

    protected ProgramException(final String message) {
        super(message);
    }

    public abstract int getCode();

    /**
     * 外置绑定 {@link HorizonIo}，从外层绑定可实现两种三种环境的不同切换
     * <pre><code>
     *     1. JVM 本地环境切换
     *     2. OSGI Bundle环境切换
     *     3. Java 9 的模块环境切换
     * </code></pre>
     * 默认情况下异常不绑定此接口，用内置接口，直接处理，但 Bundle 环境比较特殊。
     *
     * @param io 提取异常信息专用
     *
     * @return 当前异常信息
     */
    public ProgramException io(final HorizonIo io) {
        return this;
    }
}

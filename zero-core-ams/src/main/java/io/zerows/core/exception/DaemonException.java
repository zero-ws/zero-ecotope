package io.zerows.core.exception;

import io.zerows.common.program.KFailure;
import io.zerows.ams.constant.VString;
import io.zerows.ams.constant.error.ErrorMessage;
import io.zerows.core.spi.HorizonIo;

/**
 * 和资源文件绑定的检查异常类，通常在编程过程中抛出该异常
 * 1. 配置强化校验
 * 2. 容器后台程序校验
 */
public abstract class DaemonException extends ProgramException {

    private final KFailure failure;

    public DaemonException(final Class<?> clazz, final Object... args) {
        super(VString.EMPTY);
        // KFailure 构造
        this.failure = KFailure.of(clazz, args)             // caller, params
            .bind(this.getCode())                           // error code
            .bind(ErrorMessage.EXCEPTION_DAEMON);           // [ ERR{} ] ( {} ) Daemon Error: {}
    }

    @Override
    public String getMessage() {
        return this.failure.message();
    }

    @Override
    public DaemonException io(final HorizonIo io) {
        this.failure.bind(io);
        return this;
    }
}

package io.zerows.common.program;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.ams.util.HUt;
import io.zerows.core.exception.AbstractException;
import io.zerows.core.exception.WebException;
import io.zerows.core.exception.internal.ErrorMissingException;
import io.zerows.core.exception.internal.SPINullException;
import io.zerows.core.spi.HorizonIo;
import io.zerows.core.uca.log.Log;

import java.util.Objects;

/**
 * 新容器，带内置 SPI 绑定的专用异常信息处理程序，用于桥接 {@link AbstractException} 实现消息的
 * 可定制化，包括不同上下文环境的消息定制化部分。
 * <pre><code>
 *     1. 在原始基础上追加一层，每个异常构造构造时构造对应的 {@link KFailure}
 *     2. 标准环境
 *        OSGI环境
 *     3. 最终目的是封装 {@link HorizonIo} 的基础容器追加
 *        输出部分：
 *        - code
 *        - info
 *        - message
 *        输入部分：
 *        - caller
 *        - params
 * </code></pre>
 * 此类中不包含 {@link HttpStatusCode} 部分的内容，该内容由 {@link WebException}
 * 负责构造。
 *
 * @author lang : 2023-06-25
 */
public class KFailure {
    /**
     * 异常调用者，通常是构造异常的第一参数，用于在异常消息构造时提供调用者信息
     */
    private final Class<?> caller;
    /**
     * 异常调用者当前动态参数
     */
    private final Object[] params;
    /**
     * 异常代码，负值
     */
    private Integer errorCode;

    private String pattern;
    private String message;
    private String readable;

    private HorizonIo horizonIo;

    private KFailure(final Class<?> caller, final Object... params) {
        this.caller = caller;
        this.params = params;
    }

    public static KFailure of(final Class<?> caller, final Object... params) {
        return new KFailure(caller, params);
    }

    public KFailure bind(final int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public KFailure bind(final HorizonIo horizonIo) {
        if (Objects.nonNull(horizonIo)) {
            this.horizonIo = horizonIo;
        }
        return this;
    }

    public KFailure bind(final String pattern) {
        this.pattern = pattern;
        return this;
    }

    public Class<?> caller() {
        return this.caller;
    }

    public String message() {
        Objects.requireNonNull(this.errorCode);
        if (Objects.isNull(this.message)) {
            final HorizonIo io = this.io();
            // EXXXXX
            final String key = ("E" + Math.abs(this.errorCode)).intern();
            final JsonObject errorJ = io.ofError();
            if (null != errorJ && errorJ.containsKey(key)) {
                final String pattern = errorJ.getString(key);
                final String error = HUt.fromMessage(pattern, this.params);
                if (HUt.isNil(this.pattern)) {
                    // 不带前缀构造模式
                    this.message = error;
                } else {
                    // 带前缀构造模式
                    this.message = HUt.fromMessage(this.pattern, String.valueOf(this.errorCode), this.caller.getSimpleName(), error);
                }
            } else {
                throw new ErrorMissingException(this.caller, this.errorCode);
            }
        }
        return this.message;
    }

    public KFailure message(final String message) {
        this.message = message;
        return this;
    }

    public void readable(final String readable) {
        if (Objects.isNull(this.params)) {
            this.readable = readable;
        } else {
            this.readable = HUt.fromMessage(readable, this.params);
        }
    }

    public String readable() {
        Objects.requireNonNull(this.errorCode);
        if (HUt.isNotNil(this.readable)) {
            return this.readable;
        }
        final HorizonIo io = this.io();
        // XXXXX
        final String key = String.valueOf(Math.abs(this.errorCode));
        final JsonObject messageJ = io.ofFailure();
        final String pattern = messageJ.getString(key, null);
        if (HUt.isNil(pattern)) {
            return null;
        } else {
            return HUt.fromMessage(pattern, this.params);
        }
    }

    private HorizonIo io() {
        final HorizonIo io = Objects.isNull(this.horizonIo) ?
            // 如果没有绑定外置，则直接使用内置
            HUt.service(HorizonIo.class, true) :
            // 外部传入的 HorizonIo
            this.horizonIo;
        if (Objects.isNull(io)) {
            throw new SPINullException(this.getClass());
        }
        Log.info(this.getClass(), "HorizonIo = {0}", io.getClass().getName());
        return io;
    }
}

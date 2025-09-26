package io.zerows.core.exception;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VName;
import io.zerows.ams.constant.VString;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.ams.constant.error.ErrorMessage;
import io.zerows.ams.util.HUt;
import io.zerows.common.program.KFailure;
import io.zerows.core.spi.HorizonIo;

import java.util.UUID;

/**
 *
 */
@Deprecated
public abstract class WebException extends BaseZeroException {
    private final UUID id;
    private final KFailure failure;
    protected HttpStatusCode status;

    public WebException(final String message) {
        super(message);
        this.status = HttpStatusCode.BAD_REQUEST;
        this.id = UUID.randomUUID();

        this.failure = KFailure.of(null)   // caller = null, params = null
            .bind(this.getCode())                // error code
            .message(message);                   // message bind
    }

    public WebException(final Class<?> clazz, final Object... args) {
        super(VString.EMPTY);
        this.status = HttpStatusCode.BAD_REQUEST;
        this.id = UUID.randomUUID();
        this.failure = KFailure.of(clazz, args)     // caller, params
            .bind(this.getCode())                   // error code
            .bind(ErrorMessage.EXCEPTION_WEB);      // [ ERR{} ] ( {} ) Web Error: {}
        // readable 构造时设置
    }

    @Override
    public abstract int getCode();

    @Override
    public String getMessage() {
        return this.failure.message();
    }

    public HttpStatusCode getStatus() {
        // Default exception for 400
        return this.status;
    }

    @Override
    public Class<?> caller() {
        return this.failure.caller();
    }

    /**
     * 设置状态和可续信息的主要目的是在于触发动态验证，如果系统有动态验证功能，那么
     * 需要重新设置状态以及可读信息，最终生成的数据格式才能通用
     */
    public void status(final HttpStatusCode status) {
        this.status = status;
    }

    public String readable() {
        return this.failure.readable();
    }

    public void readable(final String readable) {
        this.failure.readable(readable);
    }

    /**
     * 最终格式化异常信息
     * <pre><code>
     *     {
     *         "code": "内部异常定义",
     *         "message": "系统错误信息",
     *         "info": "人工可读异常"
     *     }
     * </code></pre>
     *
     * @return {@link JsonObject}
     */
    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        // 追加UUID做追踪链
        data.put(VName.ID, this.getId().toString());
        data.put(VName.CODE, this.getCode());
        data.put(VName.MESSAGE, this.getMessage());
        final String readable = this.readable();
        if (HUt.isNotNil(readable)) {
            data.put(VName.INFO, readable);
        }
        return data;
    }

    /**
     * 针对第三方的API处理的部分 UUID 格式表
     *
     * @return 当前消息标识
     */
    public UUID getId() {
        return this.id;
    }


    @Override
    public WebException io(final HorizonIo io) {
        this.failure.bind(io);
        return this;
    }
}

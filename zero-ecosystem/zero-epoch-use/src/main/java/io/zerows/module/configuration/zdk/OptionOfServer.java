package io.zerows.module.configuration.zdk;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.core.exception.web._60050Exception501NotSupport;

import java.io.Serializable;

/**
 * 服务器配置对象，不同服务器配置对象对应的配置信息不同
 *
 * @author lang : 2024-04-20
 */
public interface OptionOfServer<T> extends Serializable {

    String key();

    String name();

    ServerType type();

    T options();

    OptionOfServer<T> options(T option);


    // ---------------------- 桥接必须实现的方法 ----------------------

    /**
     * 部分服务器类型支持到 HTTP 服务器的桥接，这种情况下实现此方法获取原始 HttpServerOptions 的引用，典型如：
     * <pre><code>
     *     {@link ServerType#API}
     *     {@link ServerType#IPC}
     *     {@link ServerType#SOCK}
     * </code></pre>
     *
     * @return HttpServerOptions
     */
    default HttpServerOptions serverBridge() {
        throw new _60050Exception501NotSupport(this.getClass());
    }

    default OptionOfServer<T> serverBridge(final HttpServerOptions serverOptions) {
        throw new _60050Exception501NotSupport(this.getClass());
    }
}

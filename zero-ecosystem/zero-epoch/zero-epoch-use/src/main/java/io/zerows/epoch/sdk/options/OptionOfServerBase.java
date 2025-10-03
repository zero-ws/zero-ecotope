package io.zerows.epoch.sdk.options;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.constant.VString;
import io.zerows.enums.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
public abstract class OptionOfServerBase<T> implements OptionOfServer<T> {

    protected HttpServerOptions serverBridge;
    protected T serverOptions;
    protected String serverName;

    protected OptionOfServerBase(final String serverName) {
        this.serverName = serverName;
    }

    // Overwrite
    @Override
    public OptionOfServer<T> serverBridge(final HttpServerOptions options) {
        this.serverBridge = options;
        return this;
    }

    // Overwrite
    @Override
    public String name() {
        return this.serverName;
    }

    // Overwrite
    @Override
    public String key() {
        final String key = this.serverBridge.getHost() + VString.COLON + this.serverBridge.getPort();
        final String type = this.type().key();
        return type + VString.SLASH + key;
    }

    // Overwrite
    @Override
    public HttpServerOptions serverBridge() {
        return this.serverBridge;
    }

    @Override
    public T options() {
        return this.serverOptions;
    }

    @Override
    public OptionOfServer<T> options(final T option) {
        this.serverOptions = option;
        return this;
    }

    @Override
    public abstract ServerType type();
}

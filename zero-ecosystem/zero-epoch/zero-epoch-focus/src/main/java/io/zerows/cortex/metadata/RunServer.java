package io.zerows.cortex.metadata;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
public class RunServer implements RunInstance<HttpServer> {
    private static final AtomicBoolean LOCKED = new AtomicBoolean(Boolean.TRUE);
    private final String name;
    private RunVertx refRunVertx;
    private SockOptions sockOptions;
    private HttpServerOptions serverOptions;
    private HttpServer server;
    private RunSetting setting;

    public RunServer(final String name) {
        this.name = name;
    }

    public Vertx refVertx() {
        return this.refRunVertx().instance();
    }

    public Router refRouter() {
        return (Router) this.server.requestHandler();
    }

    public RunVertx refRunVertx() {
        return Objects.requireNonNull(this.refRunVertx);
    }

    public RunServer refRunVertx(final RunVertx vertx) {
        this.refRunVertx = vertx;
        // 只有此处被设置的时候才会开启 this.enabled 的操作
        final NodeVertx nodeVertx = vertx.config();
        this.setting = new RunSetting(nodeVertx);
        return this;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isOk() {
        return Ut.isNotNil(this.name) && Objects.nonNull(this.server);
    }

    @Override
    public HttpServer instance() {
        return this.server;
    }

    public RunServer config(final HttpServerOptions options) {
        this.serverOptions = options;
        return this;
    }

    public RunServer config(final SockOptions sockOptions) {
        this.sockOptions = sockOptions;
        return this;
    }

    public RunServer build() {
        if (Objects.isNull(this.server.requestHandler())) {
            // 构造路由
            final Vertx vertx = this.refVertx();
            final Router router = Router.router(vertx);

            final HttpServerOptions option = this.serverOptions;
            if (LOCKED.getAndSet(Boolean.FALSE)) {
                log.info("[ ZERO ] 系统为服务器：host = {}, port = {}, websocket = {} 构造路由",
                    option.getHost(), option.getPort(), Objects.nonNull(this.sockOptions)
                );
            }
            this.server.requestHandler(router);
        }
        return this;
    }


    @Override
    @SuppressWarnings("unchecked")
    public RunServer instance(final HttpServer httpServer) {
        this.server = httpServer;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpServerOptions config() {
        return this.serverOptions;
    }

    public SockOptions configSock() {
        return this.sockOptions;
    }

    public CorsOptions configCors() {
        return this.setting.optionsCors();
    }

    public JsonObject configSession() {
        return this.setting.optionsSession();
    }

    @Override
    public boolean isOk(final int hashCode) {
        if (Objects.isNull(this.server)) {
            return false;
        }
        return this.server.hashCode() == hashCode;
    }

    public boolean enabledSession() {
        return Objects.nonNull(this.setting) && this.setting.session();
    }
}

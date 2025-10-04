package io.zerows.cortex.metadata;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.configuration.OptionOfServer;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-05-03
 */
public class RunServer implements RunInstance<HttpServer> {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);
    private final String name;
    private HttpServer server;
    private OptionOfServer<SockOptions> optionOfSock;
    private OptionOfServer<HttpServerOptions> optionOfServer;
    private RunVertx refVertx;

    public RunServer(final String name) {
        this.name = name;
    }

    /**
     * 返回路由对象，可动态添加新的 {@link Route} 到路由表中生成动态接口
     *
     * @return {@link Router}
     */
    public Router refRouter() {
        return (Router) this.server.requestHandler();
    }

    public Vertx refVertx() {
        return Objects.requireNonNull(this.refVertx).instance();
    }

    public RunVertx refRunVertx() {
        return this.refVertx;
    }

    public RunServer refRunVertx(final RunVertx vertx) {
        this.refVertx = vertx;
        return this;
    }

    public RunServer config(final OptionOfServer<HttpServerOptions> optionOfServer) {
        this.optionOfServer = optionOfServer;
        return this;
    }

    public RunServer configSock(final OptionOfServer<SockOptions> optionOfSock) {
        this.optionOfSock = optionOfSock;
        return this;
    }

    public OptionOfServer<SockOptions> configSock() {
        return this.optionOfSock;
    }

    public HSetting setting() {
        return this.refVertx.setting();
    }

    public RunServer build() {
        if (Objects.isNull(this.server.requestHandler())) {
            final RunVertx runVertx = this.refVertx;
            final Router router = Router.router(runVertx.instance());

            final HttpServerOptions option = this.optionOfServer.options();
            if (IS_OUT.getAndSet(Boolean.FALSE)) {
                Ut.Log.vertx(this.getClass()).info(
                    "The system is building Router for Server: host = {}, port = {}, websocket = {}",
                    option.getHost(), option.getPort(), this.isSock()
                );
            }
            this.server.requestHandler(router);
        }
        return this;
    }

    public boolean isSock() {
        return Objects.nonNull(this.optionOfSock);
    }

    // ---------------- 接口专用方法
    @Override
    public String name() {
        return this.name;
    }

    @Override
    public HttpServer instance() {
        return this.server;
    }

    @Override
    public boolean isOk(final int hashCode) {
        if (Objects.isNull(this.server)) {
            return false;
        }
        return this.server.hashCode() == hashCode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RunServer instance(final HttpServer server) {
        this.server = server;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OptionOfServer<HttpServerOptions> config() {
        return this.optionOfServer;
    }

    @Override
    public boolean isOk() {
        return Ut.isNotNil(this.name) && Objects.nonNull(this.server);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RunServer runServer = (RunServer) o;
        return Objects.equals(this.server, runServer.server);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.server);
    }
}

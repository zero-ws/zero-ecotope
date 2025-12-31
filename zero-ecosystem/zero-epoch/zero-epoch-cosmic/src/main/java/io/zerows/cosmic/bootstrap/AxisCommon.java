package io.zerows.cosmic.bootstrap;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.epoch.constant.KWeb;
import io.zerows.plugins.session.SessionActor;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-04
 */
public class AxisCommon implements Axis {

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        /*
         * CSRF Handler 设置（默认关闭）
         * 根据配置加载 Session 部分，包括不同的 Session 实现
         * 此代码依赖 session 安装包
         */
        this.mountSession(server, bundle);


        /*
         * Body / Content 专用的处理器
         */
        this.mountBody(server, bundle);


        /*
         * 跨域处理
         */
        this.mountCors(server, bundle);
    }

    private void mountCors(final RunServer server, final HBundle bundle) {
        final Router router = server.refRouter();
        final CorsOptions config = server.configCors();
        if (Objects.isNull(config)) {
            return;
        }
        final CorsHandler handler = CorsHandler.create()
            .allowCredentials(config.getCredentials())
            .allowedMethods(config.withMethods())
            .allowedHeaders(config.withHeaders());

        config.withOrigins().forEach(handler::addOrigin);
        router.route().order(KWeb.ORDER.CORS)
            .handler(handler);
    }

    private void mountBody(final RunServer server, final HBundle bundle) {
        final Router router = server.refRouter();
        router.route().order(KWeb.ORDER.BODY)
            // 32MB
            .handler(BodyHandler.create().setBodyLimit(32 * 1024 * 1024));
        router.route().order(KWeb.ORDER.CONTENT)
            .handler(ResponseContentTypeHandler.create());
    }

    private void mountSession(final RunServer server, final HBundle bundle) {
        final Router router = server.refRouter();
        final Vertx vertx = server.refVertx();
        // 新版 HActor 的实现类中直接构造，内部可如此使用
        final SessionHandler handler = SessionActor.ofHandler(vertx);
        router.route().order(KWeb.ORDER.SESSION)
            .handler(handler);
    }
}

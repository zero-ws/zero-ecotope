package io.zerows.cosmic.bootstrap;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.cortex.metadata.RunServerLegacy;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.plugins.session.SessionClient;
import io.zerows.cosmic.plugins.session.SessionInfix;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.epoch.constant.KWeb;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-04
 */
public class AxisCommon implements Axis {

    @Override
    public void mount(final RunServerLegacy server, final HBundle bundle) {
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

    private void mountCors(final RunServerLegacy server, final HBundle bundle) {
        final HSetting setting = null; // server.setting();
        final Router router = server.refRouter();
        final CorsOptions config = CorsOptions.get(setting);
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

    private void mountBody(final RunServerLegacy server, final HBundle bundle) {
        final Router router = server.refRouter();
        router.route().order(KWeb.ORDER.BODY)
            // 32MB
            .handler(BodyHandler.create().setBodyLimit(32 * 1024 * 1024));
        router.route().order(KWeb.ORDER.CONTENT)
            .handler(ResponseContentTypeHandler.create());
    }

    private void mountSession(final RunServerLegacy server, final HBundle bundle) {
        final HSetting setting = null; // server.setting();
        final Router router = server.refRouter();
        final Vertx vertx = server.refVertx();
        if (setting.hasInfix(YmlCore.inject.SESSION)) {
            /*
             * 由于配置了 Session，为了全局安全性，替换掉旧模式下的
             * SessionClient 初始化流程，可支持 Redis 类型的 Session
             */
            final HConfig config = setting.infix(YmlCore.inject.SESSION);
            final SessionClient client = SessionInfix.getOrCreate(vertx, config.options());
            router.route().order(KWeb.ORDER.SESSION)
                .handler(client.getHandler());
        } else {
            /*
             * 默认场景，使用 Vertx 自带的 SessionStore
             */
            final SessionStore store;
            if (vertx.isClustered()) {
                store = ClusteredSessionStore.create(vertx);
            } else {
                store = LocalSessionStore.create(vertx);
            }
            router.route().order(KWeb.ORDER.SESSION)
                .handler(SessionHandler.create(store));
        }
    }
}

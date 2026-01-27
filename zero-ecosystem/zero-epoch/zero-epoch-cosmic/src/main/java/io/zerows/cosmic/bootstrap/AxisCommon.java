package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.spec.options.CorsOptions;
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
         * 静态资源处理器
         */
        this.mountStatic(server, bundle);
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

    /**
     * 挂载静态资源处理器
     * 映射逻辑：
     * 请求 <a href="http://localhost:8080/WW_verify_xxx.txt">WW_Verify???</a>
     * -> 寻找 classpath:static/WW_verify_xxx.txt
     */
    private void mountStatic(final RunServer server, final HBundle bundle) {
        final Router router = server.refRouter();

        // 1. 创建静态资源处理器，指向 "static" 目录 (src/main/resources/static)
        final StaticHandler staticHandler = StaticHandler.create("static")
            .setIndexPage("index.html")    // 默认首页
            .setCachingEnabled(true)       // 开启缓存
            .setIncludeHidden(false)       // 不包含隐藏文件
            .setDirectoryListing(false);   // 禁止列出目录

        // 2. 【关键】挂载到根路径
        // 这样 /WW_verify_SSUl57ztEGWh1t3Q.txt 就会自动去 static 目录下找
        router.route("/*").order(KWeb.ORDER.STATIC).handler(staticHandler);

        // 💡 提示：如果你的应用有 SPA (Vue/React) 的 404 回退逻辑 (index.html)，
        // 务必确保上面的 staticHandler 在 SPA 处理器【之前】注册。
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
        final Future<SessionHandler> handlerFuture = SessionActor.waitHandler(vertx);
        handlerFuture.onSuccess(router
            .route()
            .order(KWeb.ORDER.SESSION)::handler);
    }
}

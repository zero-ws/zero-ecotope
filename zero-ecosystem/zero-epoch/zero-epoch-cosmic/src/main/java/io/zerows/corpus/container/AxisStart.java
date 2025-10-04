package io.zerows.corpus.container;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.zerows.component.log.OLog;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2024-05-04
 */
public class AxisStart implements OAxis {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);

    @Override
    public void mount(final RunServer runServer, final HBundle bundle) {
        final HttpServer server = runServer.instance();
        server.listen().onComplete(res -> {
            if (IS_OUT.getAndSet(Boolean.FALSE)) {
                // 封闭代码，只会运行一次，单线程执行
                if (res.succeeded()) {
                    final HttpServer running = res.result();
                    this.outRunning(running, runServer);
                } else {
                    /*
                     * 此处处理启动过程中启动失败时的异常信息
                     */
                    final Throwable errorException = res.cause();
                    if (Objects.nonNull(errorException)) {
                        Ut.Log.boot(this.getClass()).fatal(errorException);
                    }
                }
            }
        });
    }

    private void outRunning(final HttpServer running, final RunServer runServer) {
        final OLog logger = Ut.Log.boot(this.getClass());

        // Route 处理
        final Router router = runServer.refRouter();
        final List<Route> routes = router.getRoutes();

        // 标准日志格式化
        final Set<String> pathSet = new TreeSet<>();
        final ConcurrentMap<String, Route> pathMap = new ConcurrentHashMap<>();
        routes.forEach(route -> {
            final String path = route.getPath();
            final Set<HttpMethod> methods = route.methods();
            if (Objects.isNull(methods) || methods.isEmpty()) {
                if (Objects.nonNull(path)) {
                    logger.info("  -->  Uri Registry:        * {}", path);
                }
            } else {
                pathSet.add(path);
                pathMap.put(path, route);
            }
        });
        pathSet.forEach(path -> {
            final Route route = pathMap.get(path);
            final Set<HttpMethod> methods = route.methods();
            methods.forEach(method -> logger.info("  -->  Uri Registry: {} {}", Ut.fromAdjust(method.name(), 8), path));
        });
        final HttpServerOptions optionOfServer = runServer.config().options();
        final String prefix = optionOfServer.isSsl() ? "https" : "http";
        logger.info("( Http Server ) {0} Http Server has been started successfully. Endpoint: {3}://{1}:{2}.",
            this.getClass().getSimpleName(), Ut.netIPv4(), String.valueOf(running.actualPort()), prefix);
    }
}

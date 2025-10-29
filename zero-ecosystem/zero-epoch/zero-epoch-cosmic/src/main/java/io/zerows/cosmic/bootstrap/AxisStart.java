package io.zerows.cosmic.bootstrap;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class AxisStart implements Axis {
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
                        log.error("[ ZERO ] ❌ 服务器启动失败，异常信息如下：", errorException);
                    }
                }
            }
        });
    }

    private void outRunning(final HttpServer running, final RunServer runServer) {

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
                    log.info("[ ZERO ]  -->  Uri 注册:        * {}", path);
                }
            } else {
                pathSet.add(path);
                pathMap.put(path, route);
            }
        });
        pathSet.forEach(path -> {
            final Route route = pathMap.get(path);
            final Set<HttpMethod> methods = route.methods();
            methods.forEach(method -> log.info("  -->  Uri 注册: {} {}", Ut.fromAdjust(method.name(), 8), path));
        });
        final HttpServerOptions optionOfServer = runServer.config();
        final String prefix = optionOfServer.isSsl() ? "https" : "http";
        log.info("[ ZERO ] ( Http Server ) {} ✅️ 服务器成功启动 SUCCESS !. Endpoint: {}://{}:{}.",
            this.getClass().getSimpleName(), prefix, Ut.netIPv4(), running.actualPort());
    }
}

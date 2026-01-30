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
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
        final Map<String, Set<String>> routeMap = this.getStringSetMap(router);

        // 打印日志
        routeMap.forEach((path, methods) -> {
            methods.forEach(method -> {
                // 格式化 Method 宽度为 8，Path 在后，这样排序完全基于 Path
                final String methodStr = Ut.fromAdjust(method, 8);
                if ("*".equals(method)) {
                    log.info("[ ZERO ]  -->  Uri 注册: {} {}（Wide）", methodStr, path);
                } else {
                    log.info("[ ZERO ]  -->  Uri 注册: {} {}", methodStr, path);
                }
            });
        });

        final HttpServerOptions optionOfServer = runServer.config();
        final String prefix = optionOfServer.isSsl() ? "https" : "http";
        log.info("[ ZERO ] ( Http Server ) {} ✅️ 服务器成功启动 SUCCESS !. Endpoint: {}://{}:{}.",
            this.getClass().getSimpleName(), prefix, Ut.netIPv4(), running.actualPort());
    }

    private @NonNull Map<String, Set<String>> getStringSetMap(final Router router) {
        final List<Route> routes = router.getRoutes();

        /*
         * 数据结构：TreeMap<Path, TreeSet<Method>>
         * TreeMap 保证 Path 排序
         * TreeSet 保证同一路径下的 Method 排序 (GET, POST...)
         */
        final Map<String, Set<String>> routeMap = new TreeMap<>();

        routes.forEach(route -> {
            final String path = route.getPath();
            if (Ut.isNil(path)) {
                return; // 忽略无路径路由
            }

            final Set<HttpMethod> methods = route.methods();
            if (Objects.isNull(methods) || methods.isEmpty()) {
                // 处理全匹配方法 (Wide)
                routeMap.computeIfAbsent(path, k -> new TreeSet<>()).add("*");
            } else {
                methods.forEach(method -> {
                    routeMap.computeIfAbsent(path, k -> new TreeSet<>()).add(method.name());
                });
            }
        });
        return routeMap;
    }
}

package io.zerows.cosmic.bootstrap;

import io.r2mo.SourceReflect;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.zerows.component.environment.DevMonitor;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
class LinearCenter {

    private static final ConcurrentMap<VertxComponent, Function<HBundle, Linear>> CONSTRUCTOR = new ConcurrentHashMap<>() {
        {
            this.put(VertxComponent.AGENT, LinearAgent::new);
            this.put(VertxComponent.WORKER, LinearWorker::new);
        }
    };

    static Linear of(final VertxComponent type, final HBundle bundle) {
        return Linear.CC_SKELETON.pick(() -> {
            log.info("[ ZERO ] Linear将会被初始化，类型：{}", type);
            final Function<HBundle, Linear> constructorFn = CONSTRUCTOR.get(type);
            Objects.requireNonNull(constructorFn);
            return constructorFn.apply(bundle);
        }, type.name());
    }

    static void startAsync(final Class<?> classVerticle,
                           final DeploymentOptions options,
                           final RunVertx runVertx) {
        final Vertx vertx = runVertx.instance();
        Objects.requireNonNull(vertx, "[ ZERO ] 发布模型中的 vertx 实例不可为空！");

        vertx.deployVerticle(() -> SourceReflect.instance(classVerticle), options).onComplete(res -> {
            final String deploymentId = res.result();
            if (res.succeeded()) {
                log.info("[ ZERO ] ✅ Verticle 组件 {} 发布成功！( instances = {}, deploymentId = {}, thread = {} ",
                    classVerticle.getName(), options.getInstances(), deploymentId,
                    options.getThreadingModel());


                // 连接底层监控平台（开发监控专用）
                DevMonitor.on(vertx).add(classVerticle, options, deploymentId);


                runVertx.addDeployment(deploymentId, classVerticle);
            } else {
                final Throwable ex = res.cause();
                Optional.ofNullable(ex).ifPresent(Throwable::printStackTrace);


                log.warn("[ ZERO ] ⚠️ Verticle 组件 {} 发布失败！( instances = {}, deploymentId = {}, thread = {}, \n\terror = {} )",
                    classVerticle.getName(), options.getInstances(), deploymentId,
                    options.getThreadingModel(), Objects.isNull(ex) ? "未知错误" : ex.getMessage());
            }
        });
    }

    static void stopAsync(final Class<?> verticleCls,
                          final DeploymentOptions options,
                          final RunVertx runVertx) {
        final Vertx vertx = runVertx.instance();
        Objects.requireNonNull(vertx);
        // 发布撤销
        final Set<String> ids = runVertx.findDeployment(verticleCls);
        ids.forEach(id -> vertx.undeploy(id).onComplete(res -> {
            if (res.succeeded()) {
                log.info("[ ZERO ] ✅ Verticle 组件 {} 撤销成功！( deploymentId = {})", verticleCls.getName(), id);


                // 连接底层监控平台（开发监控专用）
                DevMonitor.on(vertx).remove(verticleCls, options);


                runVertx.removeDeployment(id);
            } else {
                final Throwable ex = res.cause();
                Optional.ofNullable(ex).ifPresent(Throwable::printStackTrace);

                log.warn("[ ZERO ] ⚠️ Verticle 组件 {} 撤销失败！( deploymentId = {}, \n\terror = {} )",
                    verticleCls.getName(), id, Objects.isNull(ex) ? "未知错误" : ex.getMessage());
            }
        }));
    }
}

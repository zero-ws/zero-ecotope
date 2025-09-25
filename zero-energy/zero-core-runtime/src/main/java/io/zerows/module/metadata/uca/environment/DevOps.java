package io.zerows.module.metadata.uca.environment;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2023/4/25
 */
@Slf4j
public class DevOps {
    private final transient Vertx vertx;

    private DevOps(final Vertx vertx) {
        this.vertx = vertx;
    }

    public static DevOps on(final Vertx vertx) {
        return new DevOps(vertx);
    }


    private static void add(final Vertx vertx, final String name, final DeploymentOptions options, final String id) {
        vertx.sharedData().<String, Object>getAsyncMap(KWeb.SHARED.DEPLOYMENT).onSuccess(data -> {
            final JsonObject instance = new JsonObject();
            instance.put(KName.DEPLOY_ID, id);
            instance.put(KName.TYPE, name);
            instance.put("instances", options.getInstances());
            instance.put("poolName", options.getWorkerPoolName());
            instance.put("poolSize", options.getWorkerPoolSize());
            data.put(name, instance).onSuccess(nil ->
                log.info("[ Meansure ] The {} has been added. ( instances = {} ), TM = {}",
                    name, options.getInstances(), options.getThreadingModel()));
        });
    }

    private static void remove(final Vertx vertx, final String name, final DeploymentOptions options) {
        vertx.sharedData().<String, Object>getAsyncMap(KWeb.SHARED.DEPLOYMENT).onSuccess(data -> {
            data.remove(name).onSuccess(nil ->
                log.info("[ Meansure ] The {} has been removed. ( instances = {} )",
                    name, options.getInstances()));
        });
    }

    public void add(final Class<?> clazz, final DeploymentOptions options, final String id) {
        add(this.vertx, clazz.getName(), options, id);
    }

    public void add(final String name, final DeploymentOptions options, final String id) {
        add(this.vertx, name, options, id);
    }

    public void remove(final Class<?> clazz, final DeploymentOptions options) {
        remove(this.vertx, clazz.getName(), options);
    }
}

package io.zerows.plugins.redis;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.epoch.constant.KName;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-31
 */
@Actor(value = "REDIS", sequence = -188)
@Slf4j
public class RedisActor extends AbstractHActor {

    public static Redis ofClient() {
        return RedisAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<Redis> addOn = RedisAddOn.of(vertxRef, config);
        final JsonObject options = config.options();
        this.vLog("[ Redis ] RedisActor 初始化完成，配置：{}", options.encode());

        final Provider<Redis> provider = new RedisProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Redis ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());

        this.tryConnect(options);
        return Future.succeededFuture(Boolean.TRUE);
    }

    private void tryConnect(final JsonObject options) {
        final Redis redis = ofClient();
        final String type = Ut.valueString(options, KName.TYPE);
        final String content;
        if ("CLUSTER".equals(type)) {
            final JsonArray endpoints = Ut.valueJArray(options, "endpoints");
            content = endpoints.encode();
        } else {
            content = Ut.valueString(options, "connectionString");
        }
        redis.connect().onComplete(result -> {
            if (result.succeeded()) {
                this.vLog("[ Redis ] \uD83C\uDF52 Redis 连接成功！！--> {}", content);
            } else {
                this.vLog().error(result.cause().getMessage(), result.cause());
            }
        });
    }
}

package io.zerows.plugins.redis;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.epoch.constant.KName;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-31
 */
@Actor(value = "REDIS", sequence = -207)
@Slf4j
public class RedisActor extends AbstractHActor {

    public static Redis ofClient() {
        // Fix: Cannot invoke "io.zerows.plugins.redis.RedisAddOn.createSingleton()" because the
        //      return value of "io.zerows.plugins.redis.RedisAddOn.of()" is null
        final RedisAddOn addOn = RedisAddOn.of();
        if (Objects.isNull(addOn)) {
            return null;
        }
        return addOn.createSingleton();
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
        Objects.requireNonNull(redis).connect()
            .compose(conn -> {
                // ✅ 连接建立后，立即发送 PING 来验证身份
                // 如果没密码或密码错误，这里会炸出 NOAUTH
                return redis.send(Request.cmd(Command.PING));
            })
            .onComplete(result -> {
                if (result.succeeded()) {
                    // 只有 PING 通了，才是真的成功
                    this.vLog("[ Redis ] \uD83C\uDF52 Redis 连接 + 认证成功！！--> {}", content);
                } else {
                    // 🛑 这里一定会捕获到 NOAUTH Authentication required
                    this.vLog().error("[ XMOD ] [ Redis ] 连接建立了，但认证失败 (NOAUTH): {} / 访问：{}",
                        result.cause().getMessage(), content);
                }
            });
    }
}

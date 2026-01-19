package io.zerows.plugins.weco;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Actor(value = "weco", sequence = -160)
@Slf4j
public class WeCoActor extends AbstractHActor {

    public static WeChatClient ofClientWeChat() {
        return WeChatAddOn.of().createSingleton();
    }

    public static WeComClient ofClientWeCom() {
        return WeComAddOn.of().createSingleton();
    }

    public static WeCoConfig.WeChatMp configOfWeChatMp() {
        return WeCoAsyncManager.of().configOf().getWechatMp();
    }

    public static WeCoConfig.WeComCp configOfWeComCp() {
        return WeCoAsyncManager.of().configOf().getWecomCp();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            log.warn("[ WeCo ] ----> 未检测到微信生态配置，跳过微信生态服务的启动！");
            return Future.succeededFuture(Boolean.TRUE);
        }
        final WeCoConfig configuration = WeCoAsyncManager.of().configOf(vertxRef, config);

        // WeChatMp
        final List<Future<Boolean>> startTasks = new ArrayList<>();
        if (configuration.isWeChatMp() || configuration.isWeChatOpen()) {
            startTasks.add(this.startWeChat(config, vertxRef));
        }

        // WeComCp
        if (configuration.isWeComCp()) {
            startTasks.add(this.startWeCom(config, vertxRef));
        }

        return Fx.combineB(startTasks).compose(started -> {
            this.vLog("[ WeCo ] ----> 已启用微信生态服务！");
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    protected Future<Boolean> startWeChat(final HConfig config, final Vertx vertxRef) {
        final AddOn<WeChatClient> addOn = WeChatAddOn.of(vertxRef, config);
        this.vLog("[ WeCo ] WeChatActor 初始化完成，配置：{}", config);

        final Provider<WeChatClient> provider = new WeChatProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ WeCo ] WeMP DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected Future<Boolean> startWeCom(final HConfig config, final Vertx vertxRef) {
        final AddOn<WeComClient> addOn = WeComAddOn.of(vertxRef, config);
        this.vLog("[ WeCo ] WeComActor 初始化完成，配置：{}", config);

        final Provider<WeComClient> provider = new WeComProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ WeCo ] WeCom DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}

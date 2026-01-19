package io.zerows.plugins.weco;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoBuilder;
import io.r2mo.xync.weco.wechat.WeChatAccount;
import io.r2mo.xync.weco.wechat.WeChatContext;
import io.r2mo.xync.weco.wechat.WeChatCredential;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 微信公众号转换器
 * <p>
 * 负责将 Spring 环境下的配置转换为标准交换对象
 * </p>
 *
 * @author lang : 2025-12-09
 */
@Slf4j
class WeChatMPWaitVertx implements UniProvider.Wait<WeCoConfig.WeChatMp> {

    // 缓存账号和上下文，避免重复构建
    private static final Cc<String, UniAccount> CC_ACCOUNT = Cc.open();
    private static final Cc<String, UniContext> CC_CONTEXT = Cc.open();

    @Override
    public UniAccount account(final JObject params, final WeCoConfig.WeChatMp config) {
        Objects.requireNonNull(config, "WeChat Config 不能为空");
        final WeChatCredential credential = config.credential();

        // 基于 AppID 缓存账号对象
        return CC_ACCOUNT.pick(() -> {
            final WeChatAccount account = new WeChatAccount(credential)
                .signature("WeChat-MP"); // 默认签名

            log.info("[ PLUG ] ( WeMP ) 构造 WeChat 账号, AppID: {}", credential.appId());
            return account;
        }, String.valueOf(credential.hashCode()));
    }

    @Override
    public UniContext context(final JObject params, final WeCoConfig.WeChatMp config) {
        Objects.requireNonNull(config, "WeChat Config 不能为空");

        // 基于配置哈希缓存上下文
        return CC_CONTEXT.pick(() -> {
            final WeChatContext ctx = new WeChatContext();

            // 1. 注入代理 (如果配置存在)
            if (config.getProxy() != null) {
                ctx.setProxy(config.getProxy());
                log.info("[ PLUG ] ( WeMP ) 构造 WeChat 上下文, Proxy: {}", (config.getProxy() != null));
            }

            // 2. 可以在此处扩展超时等其他配置
            ctx.setToken(config.getToken());
            return ctx;
        }, String.valueOf(config.hashCode()));
    }

    @Override
    public UniContext contextClient(final JObject params, final WeCoConfig.WeChatMp config) {
        // 微信发送与接收环境通常一致
        return this.context(params, config);
    }

    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> headers, final WeCoConfig.WeChatMp config) {
        return WeCoBuilder.message(params, headers);
    }
}

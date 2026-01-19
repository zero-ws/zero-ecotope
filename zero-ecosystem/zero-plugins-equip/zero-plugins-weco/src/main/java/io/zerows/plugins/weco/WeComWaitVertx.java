package io.zerows.plugins.weco;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoBuilder;
import io.r2mo.xync.weco.wecom.WeComAccount;
import io.r2mo.xync.weco.wecom.WeComContext;
import io.r2mo.xync.weco.wecom.WeComCredential;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
class WeComWaitVertx implements UniProvider.Wait<WeCoConfig.WeComCp> {

    // 缓存账号和上下文
    private static final Cc<String, UniAccount> CC_ACCOUNT = Cc.open();
    private static final Cc<String, UniContext> CC_CONTEXT = Cc.open();

    @Override
    public UniAccount account(final JObject params, final WeCoConfig.WeComCp config) {
        Objects.requireNonNull(config, "WeCom Config 不能为空");
        final WeComCredential credential = config.credential();

        return CC_ACCOUNT.pick(() -> {
            // 构造账号，签名带上 AgentID 便于区分
            final WeComAccount account = new WeComAccount(credential)
                .signature("WeCom-Agent-" + credential.agentId());

            log.info("[ PLUG ] ( WeCo ) 构造 WeCom 账号, CorpID: {} / AgentID: {}", credential.corpId(), credential.agentId());
            return account;
        }, String.valueOf(credential.hashCode()));
    }

    @Override
    public UniContext context(final JObject params, final WeCoConfig.WeComCp config) {
        Objects.requireNonNull(config, "WeCom Config 不能为空");

        return CC_CONTEXT.pick(() -> {
            final WeComContext ctx = new WeComContext();

            // 1. 注入代理
            if (config.getProxy() != null) {
                ctx.setProxy(config.getProxy());
            }

            // 2. 注入 AgentID 到上下文 (部分 API 依赖 URL 中的 AgentID)
            if (config.getAgentId() != null) {
                ctx.set("agentId", config.getAgentId());
            }

            log.info("[ PLUG ] ( WeCo ) 构造 WeCom 上下文, Proxy: {}", (config.getProxy() != null));
            return ctx;
        }, String.valueOf(config.hashCode()));
    }

    @Override
    public UniContext contextClient(final JObject params, final WeCoConfig.WeComCp config) {
        return this.context(params, config);
    }

    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> headers, final WeCoConfig.WeComCp config) {
        return WeCoBuilder.message(params, headers);
    }
}

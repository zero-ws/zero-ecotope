package io.zerows.plugins.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.weco.exception._81501Exception500WeChatConfig;
import io.zerows.plugins.weco.exception._81551Exception500WeComConfig;
import io.zerows.plugins.weco.metadata.WeCoConfig;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class WeCoAsyncManager {
    private static final Cc<String, WeCoConfig> CC_CONFIG = Cc.open();
    private static final WeCoAsyncManager INSTANCE = new WeCoAsyncManager();

    private WeCoAsyncManager() {
    }

    static WeCoAsyncManager of() {
        return INSTANCE;
    }

    WeCoConfig configOf(final Vertx vertx, final HConfig config) {
        return CC_CONFIG.pick(() -> {
            final JsonObject options = config.options();
            final WeCoConfig configuration = Ut.deserialize(options, WeCoConfig.class);
            // 1-1. 尝试配置微信公众号
            final boolean isWeChatMp = this.isWeChatMp(configuration.getWechatMp());

            // 1-2. 尝试配置微信开放平台
            final boolean isWeChatOpen = this.isWeChatOpen(configuration.getWechatOpen());

            // 2-1. 尝试配置企业微信
            final boolean isWeCom = this.isWeComCp(configuration.getWecomCp());
            if (!isWeChatOpen && !isWeChatMp && !isWeCom) {
                log.warn("[ WeCo ] 模块已加载，但部分有效配置缺失 (wechat/wecom)。");
            }
            return null;
        }, String.valueOf(vertx.hashCode()));
    }

    private boolean isWeChatOpen(final WeCoConfig.WeChatOpen wechatOpen) {
        if (wechatOpen == null) {
            return false;
        }

        // AppID 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getAppId()), _81501Exception500WeChatConfig.class, "app-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getSecret()), _81501Exception500WeChatConfig.class, "secret");

        // Redirect URI 检查
        Fn.jvmKo(StrUtil.isEmpty(wechatOpen.getRedirectUri()), _81501Exception500WeChatConfig.class, "redirect-uri");

        log.info("[ WeCo ] ----> 已启用 WeChat (开放平台) 服务模块！[AppID: {}]", wechatOpen.getAppId());
        return true;
    }

    private boolean isWeComCp(final WeCoConfig.WeComCp wecom) {
        if (wecom == null) {
            return false;
        }

        // CorpID 检查
        Fn.jvmKo(StrUtil.isEmpty(wecom.getCorpId()), _81551Exception500WeComConfig.class, "corp-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wecom.getSecret()), _81551Exception500WeComConfig.class, "secret");

        // AgentID 检查
        Fn.jvmKo(wecom.getAgentId() == null, _81551Exception500WeComConfig.class, "agent-id");

        // Callback 检查（必须配置）
        Fn.jvmKo(StrUtil.isEmpty(wecom.getUrlCallback()), _81551Exception500WeComConfig.class, "callback");

        log.info("[ WeCo ] ----> 已启用 WeCom (企业微信) 服务模块！[CorpID: {}, AgentID: {}]", wecom.getCorpId(), wecom.getAgentId());
        return true;
    }

    private boolean isWeChatMp(final WeCoConfig.WeChatMp wechat) {
        if (wechat == null) {
            return false;
        }

        // AppID 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getAppId()), _81501Exception500WeChatConfig.class, "app-id");

        // Secret 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getSecret()), _81501Exception500WeChatConfig.class, "secret");

        // Token 检查
        Fn.jvmKo(StrUtil.isEmpty(wechat.getToken()), _81501Exception500WeChatConfig.class, "token");

        log.info("[ WeCo ] ----> 已启用 WeChat (公众号) 服务模块！[AppID: {}]", wechat.getAppId());
        return true;
    }

    WeCoConfig configOf(final Vertx vertx) {
        return CC_CONFIG.get(String.valueOf(vertx.hashCode()));
    }
}

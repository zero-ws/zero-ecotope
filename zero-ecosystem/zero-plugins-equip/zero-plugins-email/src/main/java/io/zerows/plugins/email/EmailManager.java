package io.zerows.plugins.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.xync.email.EmailDomain;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.email.exception._80364Exception404EmailAccount;
import io.zerows.plugins.email.exception._80365Exception404EmailServer;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

/**
 * @author lang : 2025-10-14
 */
class EmailManager extends AddOnManager<EmailClient> {
    private static final Cc<String, EmailClient> CC_STORED = Cc.open();
    private static final Cc<String, EmailConfig> CC_CONFIG = Cc.open();

    private static final EmailManager INSTANCE = new EmailManager();

    private EmailManager() {
    }

    static EmailManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, EmailClient> stored() {
        return CC_STORED;
    }

    EmailConfig configOf(final Vertx vertx, final HConfig config) {
        return CC_CONFIG.pick(() -> {
            final JsonObject emailJ = config.options();
            final EmailConfig emailServer = Ut.deserialize(emailJ, EmailConfig.class);
            // 账号检查
            Fn.jvmKo(StrUtil.isEmpty(emailServer.getUsername()) || StrUtil.isEmpty(emailServer.getPassword()),
                _80364Exception404EmailAccount.class);

            final EmailDomain stmpServer = emailServer.getSmtp();
            // SMTP 服务检查
            Fn.jvmKo(StrUtil.isEmpty(stmpServer.getHost()) || 0 >= stmpServer.getPort(),
                _80365Exception404EmailServer.class);
            return emailServer;
        }, String.valueOf(vertx.hashCode()));
    }

    EmailConfig configOf(final Vertx vertx) {
        return CC_CONFIG.get(String.valueOf(vertx.hashCode()));
    }
}

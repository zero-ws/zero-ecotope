package io.zerows.plugins.email;

import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.web.ForTpl;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Defer
class EmailClientImpl implements EmailClient {
    private static final Cc<String, UniProvider> CC_PROVIDER = Cc.openThread();
    private static final Cc<String, ForTpl> CC_TPL = Cc.openThread();

    private final EmailConfig serverConfig;
    private final Vertx vertx;

    EmailClientImpl(final Vertx vertx, final EmailConfig serverConfig) {
        this.vertx = vertx;
        this.serverConfig = serverConfig;
        // 账号检查
    }

    public Vertx vertx() {
        return this.vertx;
    }

    @Override
    public Future<JsonObject> sendAsync(final String template, final JsonObject paramsJ,
                                        final Set<String> toSet) {
        // 1. 根据模板提取内容
        final JObject params = Ut.valueJ(paramsJ);
        final ForTpl thymeleafTpl = CC_TPL.pick(EmailTpl::new);
        final String contentHtml = thymeleafTpl.process(template, params);
        params.put("content", contentHtml);

        // 2. 根据配置发送邮件
        final UniProvider.Wait<EmailConfig> wait = UniProvider.waitFor(EmailWaitVertx::new);
        final UniAccount account = wait.account(params, this.serverConfig);
        final UniContext context = wait.context(params, this.serverConfig);


        // 3. 消息构造（每次都构造新消息）
        final UniMessage<String> message = wait.message(params, this.serverConfig);
        toSet.forEach(message::addTo);

        final UniProvider provider = CC_PROVIDER.pick(() -> SPI.findOne(UniProvider.class, "UNI_EMAIL"));
        final String result = provider.send(account, message, context);
        final JObject sentJ = UniProvider.replySuccess(result);
        log.info("[ PLUG ] ( Email ) 发送邮件成功，结果：{}", sentJ);
        return Future.succeededFuture(sentJ.data());
    }
}

package io.zerows.extension.commerce.rbac.agent.service.login.pre;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.commerce.rbac.agent.service.login.TokenStub;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.exception._80227Exception404MobileNotFound;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;
import io.zerows.platform.constant.VValue;
import io.zerows.plugins.integration.sms.SmsInfix;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;

/**
 * @author lang : 2024-07-11
 */
public class SmsService implements SmsStub {

    private final ScClock<String> cache;
    @Inject
    private TokenStub tokenStub;
    @Inject
    private transient ImageStub imageStub;

    public SmsService() {
        this.cache = ClockFactory.ofSms(this.getClass());
    }

    @Override
    public Future<Boolean> send(final String sessionId, final JsonObject params) {
        final String mobile = Ut.valueString(params, KName.MOBILE);
        return this.fetchUser(mobile).compose(query -> {
            if (Objects.isNull(query)) {
                return FnVertx.failOut(_80227Exception404MobileNotFound.class, mobile);
            }
            final String imageCode = Ut.valueString(params, AuthKey.CAPTCHA_IMAGE);
            return this.imageStub.verify(sessionId, imageCode);
        }).compose(verified -> {
            final JsonObject normalized = params.copy();
            normalized.put("tpl", "MSG_LOGIN");
            return this.sendInternal(sessionId, normalized);
        });
    }

    private Future<SUser> fetchUser(final String mobile) {
        return Ux.Jooq.on(SUserDao.class).<SUser>fetchAsync(KName.MOBILE, mobile).compose(queryList -> {
            if (Objects.isNull(queryList) || queryList.isEmpty()) {
                return Ux.future();
            }
            return Ux.future(queryList.get(VValue.IDX));
        });
    }

    private Future<Boolean> sendInternal(final String sessionId, final JsonObject params) {
        final JsonObject request = new JsonObject();
        final String smsCode = this.cache.generate();
        request.put(KName.CODE, smsCode);
        // 接收短信的人
        final String mobile = Ut.valueString(params, KName.MOBILE);
        final String tplCode = Ut.valueString(params, "tpl");
        // Inject 注入在 Service 中可用，但 Infusion 只在 Actor 中可用
        return SmsInfix.getClient().send(mobile, tplCode, request)
            // 2 分钟过期 = 120 秒
            .compose(nil -> this.cache.put(sessionId, smsCode))
            .compose(nil -> Ux.futureT());
    }

    @Override
    public Future<JsonObject> login(final String sessionId, final JsonObject params,
                                    final Session session) {
        // 此处跳过图片验证码，上一步已经处理过了
        final String code = Ut.valueString(params, "message");
        return this.cache.get(sessionId, true)
            // 验证
            .compose(item -> this.cache.verify(item, code, sessionId))
            // 手机查找
            .compose(verified -> {
                final String mobile = Ut.valueString(params, KName.MOBILE);
                return this.fetchUser(mobile);
            })
            // 令牌执行
            .compose(query -> this.tokenStub.execute(query.getKey(), session).compose(response -> {
                /*
                 * 追加 key 和 username
                 */
                response.put(KName.KEY, query.getKey());
                response.put(KName.USERNAME, query.getUsername());
                return Ux.future(response);
            }));
    }
}

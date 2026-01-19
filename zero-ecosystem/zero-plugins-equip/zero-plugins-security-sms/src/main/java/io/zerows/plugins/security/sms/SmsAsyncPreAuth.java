package io.zerows.plugins.security.sms;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.AsyncPreAuth;

@SPID("PreAuth/SMS")
public class SmsAsyncPreAuth implements AsyncPreAuth {
    private final SmsAuthConfig smsConfig = SmsAuthActor.configOf();

    @Override
    public Future<Kv<String, String>> authorize(final String identifier) {
        // 生成验证码
        final int length = this.smsConfig.getLength();
        final String captcha = RandomUtil.randomNumbers(length);
        return Future.succeededFuture(Kv.create(identifier, captcha));
    }
}

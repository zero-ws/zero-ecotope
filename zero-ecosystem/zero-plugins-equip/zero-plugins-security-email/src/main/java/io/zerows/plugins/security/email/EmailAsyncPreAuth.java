package io.zerows.plugins.security.email;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.zerows.plugins.security.service.AsyncPreAuth;

@SPID("PreAuth/EMAIL")
public class EmailAsyncPreAuth implements AsyncPreAuth {
    private final EmailAuthConfig emailConfig = EmailAuthActor.configOf();

    @Override
    public Future<Kv<String, String>> authorize(final String identifier) {
        // 生成验证码
        final int length = this.emailConfig.getLength();
        final String captcha = RandomUtil.randomNumbers(length);
        return Future.succeededFuture(Kv.create(identifier, captcha));
    }
}

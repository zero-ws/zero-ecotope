package io.zerows.plugins.sms;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.*;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.sms.SmsAccount;
import io.r2mo.xync.sms.SmsContext;
import io.r2mo.xync.sms.SmsCredential;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * Vert.x 版本短信发送构造器
 *
 * 重点说明：
 * 此处发送短信的上下文和对应的账号（access_id, access_secret）来构造不重复的 Account 和 Context。
 * 利用 Cc 缓存机制避免频繁创建对象。
 * </pre>
 *
 * @author lang : 2025-12-08
 */
@Slf4j
public class SmsWaitVertx implements UniProvider.Wait<SmsConfig> {

    // 账号缓存
    private static final Cc<String, UniAccount> CC_ACCOUNT = Cc.open();
    // 上下文缓存
    private static final Cc<String, UniContext> CC_CONTEXT = Cc.open();

    @Override
    public UniAccount account(final JObject params, final SmsConfig smsConfig) {
        final SmsCredential credential = smsConfig.getCredential();
        return CC_ACCOUNT.pick(() -> {
            // 构造基础的 UniAccount
            final SmsAccount account = new SmsAccount(credential)
                .signature(smsConfig.getSignName());

            // 日志记录
            log.info("[ ZERO ] ( SMS ) 构造短信发送 Access Id: {} / 签名: {}", account.getId(), account.signature());
            return account;
        }, String.valueOf(credential.hashCode()));
    }

    @Override
    public UniContext context(final JObject params, final SmsConfig smsConfig) {
        Objects.requireNonNull(smsConfig);
        return CC_CONTEXT.pick(() -> {
            // 构造上下文
            final SmsContext context = new SmsContext()
                .setTimeoutConnect(smsConfig.getTimeoutConnect())
                .setTimeoutRead(smsConfig.getTimeoutRead());

            // 是否设置了高级功能 (Region / Host)
            final String region = smsConfig.getRegion();
            if (StrUtil.isNotEmpty(region)) {
                context.setRegion(region);
            }
            final String host = smsConfig.getHost();
            if (StrUtil.isNotEmpty(host)) {
                context.setHost(host);
            }
            return context;
        }, String.valueOf(smsConfig.hashCode()));
    }

    @Override
    public UniContext contextClient(final JObject params, final SmsConfig smsConfig) {
        // 短信服务通常仅作为客户端发送，不支持接收上下文构建
        throw new _501NotSupportException("[ ZERO ] ( SMS ) 此方法不支持接收上下文对象！");
    }

    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> headers, final SmsConfig smsConfig) {
        // 1. 消息标识 ID
        String id = R2MO.valueT(params, "id");
        if (StrUtil.isEmpty(id)) {
            // 短信 ID 通常较短
            id = RandomUtil.randomNumbers(4);
        }
        final NormMessage<String> message = new NormMessage<>(id);

        // 2. 模板处理 (Template Code)
        final String template = R2MO.valueT(params, "template");
        message.params("template", template);
        log.info("[ ZERO ] ( SMS ) 构造短信 ID: {} / 模板：{}", id, template);

        // 3. 验证码处理 (Captcha)
        final String captcha = R2MO.valueT(params, "captcha");
        if (StrUtil.isNotEmpty(captcha)) {
            message.params("captcha", captcha);
        }

        // 4. 消息头处理
        if (headers != null) {
            headers.forEach(message::header);
        }
        return message;
    }
}
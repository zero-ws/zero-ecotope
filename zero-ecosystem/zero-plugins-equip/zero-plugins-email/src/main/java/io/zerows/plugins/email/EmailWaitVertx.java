package io.zerows.plugins.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.*;
import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.email.EmailAccount;
import io.r2mo.xync.email.EmailContext;
import io.r2mo.xync.email.EmailCredential;
import io.r2mo.xync.email.EmailDomain;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * <pre>
 * Vert.x 版本邮件发送构造器实现
 * 实现了 UniProvider.Wait 接口，负责将输入参数(params)与配置(EmailConfig)
 * 转换为统一的 Account, Message 和 Context 对象。
 * </pre>
 *
 * @author lang : 2025-12-07
 */
@Slf4j
class EmailWaitVertx implements UniProvider.Wait<EmailConfig> {

    // 发送者账号缓存 (避免频繁创建 Account 对象)
    private static final Cc<String, UniAccount> CC_ACCOUNT = Cc.open();
    // 上下文缓存 (避免频繁创建 Context 对象)
    private static final Cc<String, UniContext> CC_CONTEXT = Cc.open();

    /**
     * 构造账号信息
     *
     * @param params      输入参数 (可能包含 override 的签名、头像等)
     * @param emailConfig 邮件配置对象
     * @return 统一账号对象
     */
    @Override
    public UniAccount account(final JObject params, final EmailConfig emailConfig) {
        // 从配置中获取基础凭证 (Username/Password)
        final EmailCredential credential = emailConfig.getCredential();

        return CC_ACCOUNT.pick(() -> {
            // 1. 构造基础 EmailAccount
            final EmailAccount account = new EmailAccount(credential);
            log.info("[ PLUG ] ( Email ) 构造邮件发送账号: {} / 签名：{}", account.getId(), account.signature());

            // 2. 动态参数覆盖 (Params Override)
            // 签名设置
            final String signature = R2MO.valueT(params, "signature");
            Fn.jvmAt(StrUtil.isNotEmpty(signature), () -> account.signature(signature));

            // 头像设置
            final String avatar = R2MO.valueT(params, "avatar");
            Fn.jvmAt(StrUtil.isNotEmpty(avatar), () -> account.setAvatar(avatar));

            // 姓名设置 (显示名称)
            final String name = R2MO.valueT(params, "name");
            Fn.jvmAt(StrUtil.isNotEmpty(name), () -> account.setName(name));

            return account;
        }, String.valueOf(credential.hashCode()));
    }

    /**
     * 构造消息体
     *
     * @param params      输入参数
     * @param headers     消息头
     * @param emailConfig 邮件配置
     * @return 标准消息对象
     */
    @Override
    public UniMessage<String> message(final JObject params, final Map<String, Object> headers,
                                      final EmailConfig emailConfig) {
        // 1. 消息标识 (ID)
        String id = R2MO.valueT(params, "id");
        if (StrUtil.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }
        log.info("[ PLUG ] ( Email ) 构造邮件消息 ID: {}", id);

        final NormMessage<String> message = new NormMessage<>(id);

        // 2. 消息基础内容
        final String subject = R2MO.valueT(params, "subject");
        message.subject(subject);

        final String content = R2MO.valueT(params, "content");
        message.payload(content);

        // 3. 注入消息头
        if (headers != null) {
            headers.forEach(message::header);
        }
        return message;
    }

    /**
     * 构造发送上下文 (SMTP)
     */
    @Override
    public UniContext context(final JObject params, final EmailConfig emailConfig) {
        final EmailDomain domainSender = emailConfig.getSender();

        // 账号密码优先级处理
        this.buildAccount(params, emailConfig, domainSender);

        return this.buildContext(params, domainSender);
    }

    /**
     * 构造接收上下文 (IMAP/POP3)
     */
    @Override
    public UniContext contextClient(final JObject params, final EmailConfig emailConfig) {
        final EmailDomain domainReceiver = emailConfig.getReceiver();

        // 账号密码优先级处理
        this.buildAccount(params, emailConfig, domainReceiver);

        return this.buildContext(params, domainReceiver);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 账号密码优先级构造逻辑
     * <pre>
     * 1. Params (动态传参) - 最高优先级
     * 2. Domain Config (协议域配置) - 次优先
     * 3. Global Config (全局配置) - 最低优先级
     * </pre>
     */
    private void buildAccount(final JObject params, final EmailConfig config,
                              final EmailDomain domain) {
        // 处理 Username
        String username = R2MO.valueT(params, "username");
        if (StrUtil.isEmpty(username)) {
            // 如果 params 没有，检查 domain，如果 domain 也没有，用全局
            username = StrUtil.isEmpty(domain.getUsername()) ?
                config.getUsername() : domain.getUsername();
        }
        domain.setUsername(username);

        // 处理 Password
        String password = R2MO.valueT(params, "password");
        if (StrUtil.isEmpty(password)) {
            password = StrUtil.isEmpty(domain.getPassword()) ?
                config.getPassword() : domain.getPassword();
        }
        domain.setPassword(password);
    }

    /**
     * 构建上下文对象 (缓存化)
     */
    private UniContext buildContext(final JObject params, final EmailDomain domain) {
        Objects.requireNonNull(domain);
        // Cache Key: 协议 + 指纹
        return CC_CONTEXT.pick(() -> {
            // 1. 基础连接信息
            final EmailContext context = new EmailContext()
                .setHost(domain.getHost())
                .setPort(domain.getPort())
                .setSsl(domain.isSsl())
                .setProtocol(domain.getProtocol().name());

            // 2. 处理 Timeout (特殊扩展属性)
            int timeout = R2MO.valueT(params, UniContext.KEY_TIMEOUT, -1);

            // 如果 Params 没有指定 timeout，尝试从 Domain 的 extension 中获取
            if (timeout <= 0 && domain.hasExtension(UniContext.KEY_TIMEOUT)) {
                // EmailDomain 继承自 BaseConfig，拥有 getExtension 能力
                timeout = domain.getExtension(UniContext.KEY_TIMEOUT);
            }

            return context.setTimeout(timeout);
        }, domain.getProtocol() + "@" + domain.hashCode());
    }
}
package io.zerows.plugins.security.service;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;

/**
 * 前置服务，可直接查找，核心SPI，用于特殊前置场景
 * <pre>
 *     - Email 发邮件
 *     - SMS 发短信
 *     - OTP 模式的前序步骤
 * </pre>
 */
public interface AsyncPreAuth {

    Future<Kv<String, String>> authorize(String identifier);
}

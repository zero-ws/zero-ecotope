package io.zerows.plugins.security.service;

import io.vertx.ext.auth.hashing.HashingStrategy;

public class AsyncHashingStrategy {
    public static void main(final String[] args) {
        final HashingStrategy strategy = HashingStrategy.load();
        // falcon 管理密码
        String encoded = strategy.hash("sha512", null, null, "lang1017");
        System.out.println(encoded + "/" + strategy.getClass().getName());
        // 初始化密码
        encoded = strategy.hash("sha512", null, null, "12345678");
        System.out.println(encoded + "/" + strategy.getClass().getName());
        // admin 管理密码
        encoded = strategy.hash("sha512", null, null, "momo0216");
        System.out.println(encoded + "/" + strategy.getClass().getName());
        // user 账号密码
        encoded = strategy.hash("sha512", null, null, "keke0207");
        System.out.println(encoded + "/" + strategy.getClass().getName());
    }
}

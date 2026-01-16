package io.zerows.plugins.security.service;

import io.vertx.ext.auth.hashing.HashingStrategy;

public class AsyncHashingStrategy {
    public static void main(final String[] args) {
        final HashingStrategy strategy = HashingStrategy.load();
        final String encoded = strategy.hash("sha512", null, null, "lang1017");
        System.out.println(encoded + "/" + strategy.getClass().getName());
    }
}

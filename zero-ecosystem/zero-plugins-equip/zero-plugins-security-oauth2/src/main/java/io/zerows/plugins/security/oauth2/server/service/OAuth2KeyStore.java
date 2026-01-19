package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.zerows.cortex.management.StoreVertx;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OAuth2KeyStore {

    // Global singleton for the application lifetime
    private static final OAuth2KeyStore INSTANCE = new OAuth2KeyStore();

    private final KeyPair keyPair;
    private JWTAuth jwtAuth;

    private OAuth2KeyStore() {
        try {
            // Generate RSA KeyPair in memory
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            this.keyPair = kpg.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA keys", e);
        }
    }

    public static OAuth2KeyStore get() {
        return INSTANCE;
    }

    public synchronized JWTAuth getProvider() {
        // Lazy loading because we need Vertx
        if (this.jwtAuth == null) {
            final String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(this.keyPair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

            final String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(this.keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

            final Vertx vertx = StoreVertx.of().vertx();
            final JWTAuthOptions options = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("RS256")
                    .setBuffer(publicKey))
                .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("RS256")
                    .setBuffer(privateKey));

            this.jwtAuth = JWTAuth.create(vertx, options);
        }
        return this.jwtAuth;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }
}

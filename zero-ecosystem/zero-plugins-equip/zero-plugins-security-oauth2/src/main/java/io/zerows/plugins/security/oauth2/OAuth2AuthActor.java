package io.zerows.plugins.security.oauth2;

import io.r2mo.typed.domain.builder.BuilderOf;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.plugins.oauth2.OAuth2ServerActor;
import io.zerows.plugins.oauth2.client.BuilderOfOAuth2KeyStore;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.plugins.security.oauth2.server.OAuth2Api;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

@Actor(value = "security", sequence = -155)
public class OAuth2AuthActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (Objects.isNull(config) || Objects.isNull(config.options("oauth2"))) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final JsonObject oauth2J = config.options("oauth2");

        this.registrySecurity(vertxRef, oauth2J);

        // 路径注册日志
        OAuth2Api.OAUTH2_APIS.forEach(api -> this.vLog("[ OA2Security ] {}", api));

        return Future.succeededFuture(Boolean.TRUE);
    }

    private void registrySecurity(final Vertx vertxRef, final JsonObject oauth2J) {
        // 注册 KeyStore 数据部分详细信息
        final OAuth2Security security = OAuth2ServerActor.securityOf(vertxRef, oauth2J);
        this.vLog("[ OA2Security ] 安全管理注册：{}", oauth2J);
        final BuilderOf<KeyStoreOptions> builder = BuilderOf.of(BuilderOfOAuth2KeyStore::new);
        final OAuth2Security.KeyStoreConfig keystore = security.getKeyStore();
        final KeyStoreOptions options = builder.create(keystore);
        final JsonObject keystoreData = OAuth2Jwks.generate(options, keystore.getAlias());
        OAuth2ServerActor.keystoreOf(vertxRef, keystoreData);
        this.vLog("[ OA2Security ] Jwks 注册完成！！");
    }
}

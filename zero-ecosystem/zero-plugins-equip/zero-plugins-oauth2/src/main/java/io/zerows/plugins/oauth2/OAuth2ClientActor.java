package io.zerows.plugins.oauth2;

import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Compared;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.client.BuilderOfOAuth2Client;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2RegisteredClientDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2Config;
import io.zerows.specification.configuration.HConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Actor(value = "oauth2")
public class OAuth2ClientActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        // 使用 Optional 串联获取逻辑
        return Optional.ofNullable(config)
            .map(ignored -> OAuth2Manager.of().configOf(vertxRef))
            .map(OAuth2Config::getRegistration)
            .filter(registrations -> !registrations.isEmpty()) // 过滤掉空 Map
            .map(registrations -> {
                // 🟢 真正的业务逻辑写在这里
                // registrations 此时一定非空且有值
                final BuilderOf<Oauth2RegisteredClient> builderClient = BuilderOf.of(BuilderOfOAuth2Client::new);
                final List<Oauth2RegisteredClient> clients = new ArrayList<>();
                registrations.values().forEach(oauth2Config -> {
                    final Oauth2RegisteredClient client = builderClient.create(oauth2Config);
                    clients.add(client);
                });
                return this.saveAsync(clients).map(saved -> Boolean.TRUE);
            })
            // 🟡 如果上面任何一步是 null 或者 Map 为空，则返回这个默认值
            .orElse(Future.succeededFuture(Boolean.TRUE));
    }

    private Future<List<Oauth2RegisteredClient>> saveAsync(final List<Oauth2RegisteredClient> clients) {
        final ADB db = DB.on(Oauth2RegisteredClientDao.class);
        return db.<Oauth2RegisteredClient>fetchAllAsync().compose(stored -> {
            final Compared<Oauth2RegisteredClient> compared = R2MO.elementDiff(stored, clients, "clientId");
            final List<Oauth2RegisteredClient> processed = new ArrayList<>();
            return db.insertAsync(compared.queueC())
                .map(inserted -> {
                    this.vLog("[ OAuth2 ] --> ADD / 新增：{}", inserted.size());
                    processed.addAll(inserted);
                    return true;
                })
                .compose(nil -> db.updateAsync(compared.queueU()))
                .map(updated -> {
                    this.vLog("[ OAuth2 ] --> UPDATE / 修改：{}", updated.size());
                    processed.addAll(updated);
                    return processed;
                });
        }).compose(processed -> {
            this.vLog("[ OAuth2 ] 合计处理注册客户端数量：{}", processed.size());
            return Future.succeededFuture(processed);
        });
    }
}

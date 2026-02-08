package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class ExInitSource implements ExInit {

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> {
            log.info("{} XSource 初始化: {}", AtConstant.K_PREFIX, appJson.encode());
            /* X_SOURCE initialization */
            final JsonObject sourceJson = appJson.getJsonObject(KName.SOURCE);
            final XSource source = this.init(sourceJson, appJson);

            return DB.on(XSourceDao.class)
                /*
                 * Init second step: id as condition, save X_APP
                 */
                .upsertAsync(this.whereUnique(appJson), source)
                .compose(Ux::futureJ)
                /*
                 * Result Building
                 */
                .compose(updated -> Ux.future(this.result(appJson, updated)));
        };
    }

    @Override
    public JsonObject whereUnique(final JsonObject appJson) {
        final JsonObject filters = new JsonObject();
        filters.put(KName.APP_ID, appJson.getValue(KName.KEY));
        return filters;
    }

    @Override
    public JsonObject result(final JsonObject input,
                             final JsonObject sourceJson) {
        input.put(KName.SOURCE, sourceJson);
        return input;
    }

    private XSource init(final JsonObject input,
                         final JsonObject appJson) {
        /* key set */
        final XSource source = Ut.deserialize(input.copy(), XSource.class);
        source.setActive(Boolean.TRUE);
        source.setAppId(appJson.getString(KName.KEY));
        /* Basic Configuration */
        source.setJdbcConfig(new JsonObject());
        source.setMetadata(new JsonObject());
        source.setLanguage(appJson.getString(KName.LANGUAGE));
        if (Objects.isNull(source.getId())) {
            source.setId(UUID.randomUUID().toString());
        }
        return source;
    }
}

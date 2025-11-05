package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.common.AtMsg;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.function.Function;

import static io.zerows.extension.module.ambient.boot.At.LOG;

/*
 * EmApp Initialization
 */
public class ExInitApp implements ExInit {
    private static final LogOf LOGGER = LogOf.get(ExInitApp.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> {
            LOG.App.info(LOGGER, AtMsg.INIT_APP, appJson.encode());
            /* Deserialization */
            final XApp app = this.init(appJson);
            return DB.on(XAppDao.class)
                /*
                 * Init first step: UPSERT ( Insert / Update )
                 */
                .upsertAsync(this.whereUnique(appJson), app)
                .compose(Ux::futureJ)
                /*
                 * Result Building
                 */
                .compose(input -> Ux.future(this.result(input, appJson)));
        };
    }

    @Override
    public JsonObject result(final JsonObject input,
                             final JsonObject appJson) {
        final JsonObject result = new JsonObject();
        if (!Ut.isNil(appJson)) {
            result.mergeIn(appJson);
        }
        /* Data Source Input */
        if (!Ut.isNil(input)) {
            result.put(KName.SOURCE, input.getValue(KName.SOURCE));
        }
        return result;
    }

    @Override
    public JsonObject whereUnique(final JsonObject input) {
        final JsonObject filters = new JsonObject();
        filters.put(KName.KEY, input.getValue(KName.KEY));
        return filters;
    }

    private XApp init(final JsonObject input) {
        /* appKey generation */
        if (!input.containsKey(KName.APP_KEY)) {
            input.put(KName.APP_KEY, Ut.randomString(64));
        }
        /* logo */
        final JsonArray files = input.getJsonArray(KName.App.LOGO);
        if (null != files) {
            input.put(KName.App.LOGO, files.encode());
        }
        final XApp app = Ut.deserialize(input.copy(), XApp.class);
        /* active = true */
        app.setActive(Boolean.TRUE);
        return app;
    }
}

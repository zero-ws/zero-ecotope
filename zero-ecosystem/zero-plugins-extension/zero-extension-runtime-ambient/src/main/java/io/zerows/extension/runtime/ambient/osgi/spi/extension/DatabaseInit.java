package io.zerows.extension.runtime.ambient.osgi.spi.extension;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.runtime.ambient.eon.AtMsg;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.program.Ux;

import java.util.function.Function;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

public class DatabaseInit implements ExInit {

    private static final LogOf LOGGER = LogOf.get(DatabaseInit.class);

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> {
            LOG.App.info(LOGGER, AtMsg.INIT_DATABASE, appJson.encode());
            /* Database InJson */
            final JsonObject databaseJson = appJson.getJsonObject(KName.SOURCE);
            final Database database = Database.createDatabase(databaseJson);
            /*
             * Init third step: X_SOURCE stored into pool
             */
            return Rapid.<String, Database>object(KWeb.CACHE.DATABASE)
                .write(appJson.getString(KName.KEY), database)
                .compose(item -> Ux.future((JsonObject) item.toJson()))
                .compose(item -> Ux.future(this.result(appJson, item)));
        };
    }

    @Override
    public JsonObject result(final JsonObject input,
                             final JsonObject database) {
        LOG.App.info(LOGGER, AtMsg.INIT_DB_RT, database.encodePrettily());
        return input;
    }
}

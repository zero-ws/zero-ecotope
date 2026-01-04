package io.zerows.extension.module.ambient.spi;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.plugins.cache.HMM;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class ExInitDatabase implements ExInit {


    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> {
            log.info("[ XMOD ] ( App ) 应用数据库初始化：{}", appJson.encode());
            /* Database InJson */
            final JsonObject databaseJson = appJson.getJsonObject(KName.SOURCE);
            final Database database = Database.createDatabase(databaseJson);
            /*
             * Init third step: X_SOURCE stored into pool
             */
            return HMM.<String, Database>of(KWeb.CACHE.DATABASE)
                .put(appJson.getString(KName.KEY), database)
                .compose(item -> Ux.future((JsonObject) item.toJson()))
                .compose(item -> Ux.future(this.result(appJson, item)));
        };
    }

    @Override
    public JsonObject result(final JsonObject input,
                             final JsonObject database) {
        log.info("[ XMOD ] ( App ) 工作流数据库初始化：{}", database.encode());
        return input;
    }
}

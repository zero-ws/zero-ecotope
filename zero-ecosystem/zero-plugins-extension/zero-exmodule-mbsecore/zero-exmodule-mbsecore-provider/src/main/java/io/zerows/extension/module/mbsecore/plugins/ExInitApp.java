package io.zerows.extension.module.mbsecore.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.program.Ux;

import java.util.function.Function;

public class ExInitApp implements ExInit {

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> Ux.future(appJson)
            /* 合并初始化Schema信息，包括表更新/表创建 */
            .compose(AoRefine.combine().apply())
            /* M_ENTITY, M_FIELD, M_KEY, M_INDEX */
            .compose(AoRefine.schema().apply())
            /* M_MODEL, M_ATTRIBUTE, M_NEXUS */
            .compose(AoRefine.model().apply());
    }
}

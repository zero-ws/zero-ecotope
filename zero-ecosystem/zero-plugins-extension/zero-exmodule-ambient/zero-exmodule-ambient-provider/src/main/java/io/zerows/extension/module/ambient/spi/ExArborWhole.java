package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.spi.ExArborBase;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExArborWhole extends ExArborBase {
    @Override
    public Future<JsonArray> generate(final JsonObject categoryJ, final JsonObject configuration) {
        final JsonObject query = configuration.getJsonObject(KName.QUERY, new JsonObject());
        if (Ut.isNil(query)) {
            return this.combineArbor(categoryJ, null, configuration);
        }
        /*
         * When `query` is configured in TREE_CONFIG, fetch children from X_CATEGORY
         * by sigma + query condition (same logic as ExArborCatalog).
         * Without this, the whole-tree category produces no child directories.
         */
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.SIGMA, categoryJ.getValue(KName.SIGMA));
        condition.mergeIn(query, true);
        return ExArborCatalog.fetchCategories(condition)
            .compose(children -> this.combineArbor(categoryJ, children, configuration));
    }
}

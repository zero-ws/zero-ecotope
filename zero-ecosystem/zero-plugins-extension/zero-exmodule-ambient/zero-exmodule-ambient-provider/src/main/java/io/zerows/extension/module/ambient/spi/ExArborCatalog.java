package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XCategory;
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
public class ExArborCatalog extends ExArborBase {
    @Override
    public Future<JsonArray> generate(final JsonObject category, final JsonObject configuration) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.SIGMA, category.getValue(KName.SIGMA));
        final JsonObject query = configuration.getJsonObject(KName.QUERY, new JsonObject());
        condition.mergeIn(query, true);
        final Future<JsonArray> fetched = DB.on(XCategoryDao.class)
            .<XCategory>fetchAsync(condition)
            .compose(categories -> {
                /*
                 * Duplicate entry for key 'CODE'
                 * For service catalog, the name will be of the directory instead,
                 * Here provide duplicated issue fix
                 */
                final Set<String> names = new HashSet<>();
                final List<XCategory> compress = new ArrayList<>();
                categories.forEach(cat -> {
                    if (!names.contains(cat.getName())) {
                        compress.add(cat);
                        names.add(cat.getName());
                    }
                });
                return Ux.futureA(compress);
            })
            .map(item -> Ut.valueToJArray(item,
                KName.METADATA,
                KName.Component.TREE_CONFIG,
                KName.Component.RUN_CONFIG
            ));
        return fetched.compose(children -> this.combineArbor(category, children, configuration));
    }
}

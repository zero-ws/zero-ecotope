package io.zerows.extension.runtime.ambient.osgi.spi.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.fn.RFn;
import io.zerows.unity.Ux;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XCategory;
import io.zerows.extension.runtime.skeleton.osgi.spi.phantom.AbstractArbor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class CatalogArbor extends AbstractArbor {
    @Override
    public Future<JsonArray> generate(final JsonObject categoryJ, final JsonObject configuration) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.SIGMA, categoryJ.getValue(KName.SIGMA));
        final JsonObject query = configuration.getJsonObject(KName.QUERY, new JsonObject());
        condition.mergeIn(query, true);
        return Ux.Jooq.on(XCategoryDao.class)
            .<XCategory>fetchAsync(condition)
            .compose(categories -> {
                /*
                 * Duplicate entry '0E09A18725E9E7A2B1B9A693D06542A0-Qxw5HDkluJFnAPmcQCtu9uhGdXEiGNt' for key 'CODE'
                 * For service catalog, the name will be generate the directory instead,
                 * Here provide duplicated issue fix
                 */
                final Set<String> names = new HashSet<>();
                final List<XCategory> compress = new ArrayList<>();
                categories.forEach(category -> {
                    if (!names.contains(category.getName())) {
                        compress.add(category);
                        names.add(category.getName());
                    }
                });
                return Ux.futureA(compress);
            })
            .compose(RFn.ofJArray(
                KName.METADATA,
                KName.Component.TREE_CONFIG,
                KName.Component.RUN_CONFIG
            ))
            .compose(children -> this.combineArbor(categoryJ, children, configuration));
    }
}

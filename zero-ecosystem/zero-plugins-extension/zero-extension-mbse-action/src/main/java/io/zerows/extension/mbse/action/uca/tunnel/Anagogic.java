package io.zerows.extension.mbse.action.uca.tunnel;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.epoch.common.shared.datamation.KFabric;
import io.zerows.epoch.common.shared.datamation.KMap;
import io.zerows.epoch.common.shared.normalize.KIdentity;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.Database;
import io.zerows.epoch.corpus.metadata.commune.XHeader;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.corpus.web.cache.Rapid;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.modeling.Commercial;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtComponent;
import io.zerows.specification.modeling.HRule;

import java.util.Objects;
import java.util.function.Supplier;

/*
 * Tool for different injection
 * 1）Database injection method
 * 2) Dict processing for `Dict` processing here.
 * 3) Diode processing for
 * - BEFORE -->
 * - AFTER  <--
 * - AROUND <->
 * 4) Enable plug-in for processing
 * - Dict plug-in for key = JsonArray processing
 * -- Dict ( Assist ) plugin here
 */
class Anagogic {
    /*
     * Database processing
     */
    static Future<Database> databaseAsync(final Commercial commercial) {
        return Rapid.<String, Database>object(KWeb.CACHE.DATABASE_MULTI)
            .cached(commercial.app(), () -> Ux.future(commercial.database()));
    }

    static Future<Boolean> componentAsync(final JtComponent component, final Envelop envelop) {
        final JsonObject headers = envelop.headersX();
        final XHeader header = new XHeader();
        header.fromJson(headers);
        Ut.contract(component, XHeader.class, header);
        return Ux.future(Boolean.TRUE);
    }

    static Future<Boolean> componentAsync(final JtComponent component, final Commercial commercial, final Supplier<Future<KFabric>> supplier) {
        if (Objects.nonNull(commercial)) {
            return supplier.get().compose(fabric -> {
                /*
                 * JsonObject options inject ( without `mapping` node for Diode )
                 */
                final JsonObject options = Ut.valueJObject(commercial.options());

                Ut.contract(component, JsonObject.class, options);                  /* serviceConfig */
                Ut.contract(component, KIdentity.class, commercial.identity());      /* identifierComponent -> converted to identity */
                Ut.contract(component, KMap.class, commercial.mapping());    /* mappingConfig */
                Ut.contract(component, KFabric.class, fabric);                   /* dictConfig -> converted to fabric */
                Ut.contract(component, HRule.class, commercial.rule());        /* Rule Unique */

                return Future.succeededFuture(Boolean.TRUE);
            });
        } else {
            return Future.succeededFuture(Boolean.TRUE);
        }
    }
}

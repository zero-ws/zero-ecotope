package io.zerows.extension.mbse.action.uca.tunnel;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.database.OldDatabase;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtComponent;
import io.zerows.mbse.sdk.Commercial;
import io.zerows.platform.metadata.KFabric;
import io.zerows.platform.metadata.KIdentity;
import io.zerows.platform.metadata.KMap;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.HRule;
import io.zerows.support.Ut;

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
    static Future<OldDatabase> databaseAsync(final Commercial commercial) {
        return Rapid.<String, OldDatabase>object(KWeb.CACHE.DATABASE_MULTI)
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

package io.zerows.extension.runtime.crud.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.Fx;
import io.zerows.unity.Ux;
import io.zerows.common.datamation.KDictConfig;
import io.zerows.common.datamation.KDictSource;
import io.zerows.common.datamation.KDictUse;
import io.zerows.common.datamation.KFabric;
import io.zerows.core.constant.KName;
import io.zerows.core.web.mbse.atom.specification.KModule;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.module.cloud.zdk.spi.Dictionary;
import io.zerows.module.domain.atom.specification.KTransform;
import io.zerows.module.metadata.osgi.channel.Pocket;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.crud.util.Ix.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class FabricTran implements Tran {
    private transient final boolean isFrom;

    FabricTran(final boolean isFrom) {
        this.isFrom = isFrom;
    }

    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        if (in.canTransform()) {
            return this.fabric(in).compose(Fx.ifNil(() -> data, fabric ->
                this.isFrom ? fabric.inFrom(data) : fabric.inTo(data)));
        } else {
            return Ux.future(data);
        }
    }

    @Override
    public Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        if (in.canTransform()) {
            return this.fabric(in).compose(Fx.ifNil(() -> data, fabric ->
                this.isFrom ? fabric.inFrom(data) : fabric.inTo(data)));
        } else {
            return Ux.future(data);
        }
    }

    private Future<KFabric> fabric(final IxMod in) {
        final Envelop envelop = in.envelop();
        final KModule module = in.module();
        final KTransform transform = module.getTransform();
        return this.fabric(module, envelop).compose(dictData -> {
            final ConcurrentMap<String, JsonArray> dictMap = new ConcurrentHashMap<>(dictData);
            if (in.canJoin()) {
                /*
                 * Nested dictionary
                 */
                final KModule connect = in.connected();
                final KTransform transformConnect = connect.getTransform();
                /*
                 * Combine DiConsumer
                 */
                final ConcurrentMap<String, KDictUse> connectConsumer = transform.epsilon();
                if (Objects.nonNull(transformConnect)) {
                    connectConsumer.putAll(transformConnect.epsilon());
                }
                return this.fabric(connect, envelop).compose(dictConnect -> {
                    dictMap.putAll(dictConnect);
                    return Ux.future(KFabric.create()
                        .dictionary(dictMap)
                        .epsilon(connectConsumer)
                    );
                });
            } else {
                // No Connect Module
                return Ux.future(KFabric.create()
                    .dictionary(dictMap)
                    .epsilon(transform.epsilon())
                );
            }
        });
    }

    private Future<ConcurrentMap<String, JsonArray>> fabric(final KModule module, final Envelop envelop) {
        final KTransform transform = module.getTransform();
        if (Objects.isNull(transform)) {
            return Ux.future(new ConcurrentHashMap<>());
        }
        /* Epsilon */
        final ConcurrentMap<String, KDictUse> epsilonMap = transform.epsilon();
        /* Channel Infusion, Here will enable Pool */
        final Dictionary plugin = Pocket.lookup(Dictionary.class);
        /* Dict */
        final KDictConfig dict = transform.source();
        if (epsilonMap.isEmpty() || Objects.isNull(plugin) || !dict.validSource()) {
            /*
             * Direct returned
             */
            LOG.Rest.info(this.getClass(), "Infusion condition handler, {0}, {1}, {2}",
                epsilonMap.isEmpty(), Objects.isNull(plugin), !dict.validSource());
            return Ux.future(new ConcurrentHashMap<>());
        }
        // Calculation
        final List<KDictSource> sources = dict.configSource();
        final MultiMap paramMap = MultiMap.caseInsensitiveMultiMap();
        final JsonObject headers = envelop.headersX();
        paramMap.add(KName.SIGMA, headers.getString(KName.SIGMA));
        /*
         * To avoid final in lambda expression
         */
        return plugin.fetchAsync(paramMap, sources);
    }
}

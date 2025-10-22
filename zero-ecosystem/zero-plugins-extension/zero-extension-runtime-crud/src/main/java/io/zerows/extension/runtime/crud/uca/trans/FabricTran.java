package io.zerows.extension.runtime.crud.uca.trans;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KTransform;
import io.zerows.epoch.spi.Dictionary;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.mbse.metadata.KModule;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KDictUse;
import io.zerows.platform.metadata.KFabric;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
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
        final Dictionary plugin = HPI.findOverwrite(Dictionary.class);
        /* Dict */
        final KDictConfig dict = transform.source();
        if (epsilonMap.isEmpty() || Objects.isNull(plugin) || !dict.validSource()) {
            /*
             * Direct returned
             */
            log.info("[ ZMOD ] 条件处理插件：{}, {}, {}",
                epsilonMap.isEmpty(), Objects.isNull(plugin), !dict.validSource());
            return Ux.future(new ConcurrentHashMap<>());
        }
        // Calculation
        final List<KDictConfig.Source> sources = dict.configSource();
        final MultiMap paramMap = MultiMap.caseInsensitiveMultiMap();
        final JsonObject headers = envelop.headersX();
        paramMap.add(KName.SIGMA, headers.getString(KName.SIGMA));
        /*
         * To avoid final in lambda expression
         */
        return plugin.fetchAsync(paramMap, sources);
    }
}

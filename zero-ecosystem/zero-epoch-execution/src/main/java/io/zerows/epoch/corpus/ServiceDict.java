package io.zerows.epoch.corpus;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.common.shared.datamation.*;
import io.zerows.epoch.corpus.feature.FieldMapper;
import io.zerows.epoch.corpus.cloud.zdk.spi.Dictionary;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * DictSource
 * Dict
 * DictEpsilon
 */
@SuppressWarnings("all")
class ServiceDict {
    private static final Cc<Integer, Dictionary> CC_DICT = Cc.open();

    static ConcurrentMap<String, KDictUse> dictUse(final JsonObject epsilonJ) {
        return KDictUse.epsilon(epsilonJ);
    }

    static <T> Future<T> dictTo(final T record, final KFabric fabric) {
        final FieldMapper mapper = new FieldMapper();
        if (record instanceof JsonObject) {
            final JsonObject ref = (JsonObject) record;
            return fabric.inTo(ref)
                .compose(processed -> Ux.future(mapper.out(processed, fabric.mapping())))
                .compose(processed -> Ux.future((T) processed));
        } else if (record instanceof JsonArray) {
            final JsonArray ref = (JsonArray) record;
            return fabric.inTo(ref)
                .compose(processed -> Ux.future(mapper.out(processed, fabric.mapping())))
                .compose(processed -> Ux.future((T) processed));
        } else {
            return Ux.future(record);
        }
    }

    static Future<ConcurrentMap<String, JsonArray>> dictData(final KDictConfig dict, final MultiMap paramMap) {
        if (Objects.isNull(dict)) {
            /*
             * Not `Dict` configured
             */
            return ToCommon.future(new ConcurrentHashMap<>());
        }
        /*
         * Dict extract here
         */
        final ConcurrentMap<String, JsonArray> dictData = new ConcurrentHashMap<>();
        if (dict.valid()) {
            /*
             * Component Extracted
             */
            final Class<?> dictCls = dict.configComponent();
            if (Ut.isImplement(dictCls, Dictionary.class)) {
                /*
                 * JtDict instance for fetchAsync
                 */
                final Dictionary dictStub = CC_DICT.pick(() -> Ut.instance(dictCls), dict.hashCode());
                // FnZero.po?l(POOL_DICT, dict.hashCode(), () -> Ut.instance(dictCls));
                /*
                 * Param Map / List<Source>
                 */
                return dictStub.fetchAsync(paramMap, dict.configSource());
            } else return ToCommon.future(dictData);
        }
        return ToCommon.future(dictData);
    }

    static Future<KFabric> dictAtom(final KDictConfig dict, final MultiMap params,
                                    final KMap mapping, final String identifier) {
        return Ux.dictData(dict, params).compose(dictData -> {
            final KMapping mappingItem = mapping.child(identifier);
            final KFabric fabric = KFabric.create(mappingItem)
                .epsilon(dict.configUse())
                .dictionary(dictData);
            return Ux.future(fabric);
        });
    }
}

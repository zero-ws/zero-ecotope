package io.zerows.extension.runtime.ambient.osgi.spi.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.metadata.datamation.KDictSource;
import io.zerows.epoch.corpus.Ux;
import io.zerows.enums.EmDict;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.epoch.underlying.Dictionary;
import io.zerows.extension.runtime.ambient.agent.service.DatumService;
import io.zerows.extension.runtime.ambient.agent.service.DatumStub;
import io.zerows.extension.runtime.ambient.uca.dict.Dpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

/*
 * Dictionary implementation class
 */
public class ExAmbientDictionary implements Dictionary {
    private static final Cc<String, DatumStub> CC_DICT = Cc.openThread();

    @Override
    public Future<ConcurrentMap<String, JsonArray>> fetchAsync(final MultiMap paramMap,
                                                               final List<KDictSource> sources) {
        /*
         * Whether sources is empty
         */
        if (Objects.isNull(sources) || sources.isEmpty()) {
            /*
             * Empty processing
             */
            return Ux.future(new ConcurrentHashMap<>());
        } else {
            /*
             * Future merged here
             */
            final List<Future<ConcurrentMap<String, JsonArray>>> futures = new ArrayList<>();
            sources.forEach(source -> {
                final EmDict.Type type = source.getSourceType();
                final Dpm dpm = Dpm.get(type);
                if (Objects.nonNull(dpm)) {
                    futures.add(dpm.fetchAsync(source, paramMap));
                }
            });
            /*
             * Merged each futures here
             * 1) Tabular ( type -> JsonArray )      size > 0
             * 2) Category ( type -> JsonArray )     size > 0
             * 3) Assist ( type -> JsonArray )       size > 0
             */
            return Fx.compressM(futures).compose(dict -> {
                final StringBuilder report = new StringBuilder();
                report.append("[ PT ] Dictionary Total：").append(dict.size());
                dict.forEach((key, array) -> report
                    .append("\n\tkey = ").append(key)
                    .append(", value size = ").append(array.size()));
                LOG.Flow.info(this.getClass(), report.toString());
                return Ux.future(dict);
            });
        }
    }

    @Override
    public Future<JsonArray> fetchTree(final String sigma, final String type) {
        final DatumStub stub = CC_DICT.pick(DatumService::new);
        return stub.treeSigma(sigma, type);
    }

    @Override
    public Future<JsonArray> fetchList(final String sigma, final String type) {
        final DatumStub stub = CC_DICT.pick(DatumService::new);
        return stub.dictSigma(sigma, type);
    }
}

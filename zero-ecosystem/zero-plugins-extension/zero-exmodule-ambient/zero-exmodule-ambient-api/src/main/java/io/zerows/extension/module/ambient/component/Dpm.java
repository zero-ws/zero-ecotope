package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.zerows.platform.enums.EmDS;
import io.zerows.platform.metadata.KDictConfig;

import java.util.concurrent.ConcurrentMap;

/**
 * ## The Dict Interface
 *
 * ### 1. Intro
 *
 * You can add different dict type in current plugin and modular here.
 *
 * - TABULAR, Related to `X_TABULAR` ( Global List )
 * - ASSIST, Any static source with jooq Dao.
 * - CATEGORY, Related to `X_CATEGORY` ( Global Tree )
 * - NONE, ( Default here for the definition that could not be parsed )
 *
 * ### 2. Meaning
 *
 * The DPM means `Data Processing Management`.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Dpm {
    static Dpm get(final EmDS.Dictionary type) {
        return DpmTool.POOL_DPM.getOrDefault(type, null);
    }

    /**
     * Async source
     *
     * @param params {@link MultiMap} parameters that are formatted with vertx
     * @param source {@link KDictConfig.Source} definition of dict here.
     *
     * @return {@link Future}
     */
    Future<ConcurrentMap<String, JsonArray>> fetchAsync(KDictConfig.Source source, MultiMap params);

    /**
     * Sync source
     *
     * @param params {@link MultiMap} parameters that are formatted with vertx
     * @param source {@link KDictConfig.Source} definition of dict here.
     *
     * @return {@link ConcurrentMap}
     */
    ConcurrentMap<String, JsonArray> fetch(KDictConfig.Source source, MultiMap params);
}

package io.zerows.extension.mbse.modulat.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.mbse.modulat.uca.dock.Ark;
import io.zerows.extension.runtime.skeleton.osgi.spi.modeler.Modulat;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExModulat implements Modulat {
    @Override
    public Future<JsonObject> extension(final JsonObject appJson, final boolean open) {
        final String key = appJson.getString(KName.KEY);
        return this.extension(key, open).compose(moduleJ -> {
            /*
             * appJ + moduleJ to web response ( Final )
             */
            final JsonObject original = moduleJ.copy();
            original.mergeIn(appJson, true);
            return Ux.future(original);
        });
    }

    /*
     * {
     *     "configKey1": {},
     *     "configKey2": {}
     * }
     */
    @Override
    public Future<JsonObject> extension(final String appId, final boolean open) {
        Objects.requireNonNull(appId);
        final JsonObject appJ = new JsonObject();
        // 解决无法连接导致AppId为空的问题
        appJ.put(KName.KEY, appId);
        return Ark.configure().modularize(appId, open).compose(moduleJ -> {
            appJ.mergeIn((JsonObject) moduleJ, true);
            if (open) {
                // open = true 可启用“登录参数”
                return Ux.future(appJ);
            } else {
                // open = false 的时候才读取 bags 节点的数据，否则不读取
                return Ark.bag().modularize(appId, false).compose(bagJ -> {
                    final JsonArray bags = (JsonArray) bagJ;
                    appJ.put(KName.App.BAGS, bags);
                    return Ux.future(appJ);
                });
            }
        });
    }
}

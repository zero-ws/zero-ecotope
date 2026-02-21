package io.zerows.extension.module.modulat.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;


public class ExModulatCommon implements ExModulat {
    /**
     * 追加应用特殊的配置项对应值
     * <pre>
     *     最终成型的数据
     *     - mXxx = {}
     *     - mYyy = {}
     * </pre>
     * 特殊的 mXxx 的配置对应的值
     * <pre>
     *     1. Bag 中使用 UI_CONFIG 进行配置
     *        校验 mXxx 是否存在配置重复
     * </pre>
     *
     * @param appId 应用Id
     * @param open  是否包含开放性属性
     * @return 异步数据
     */
    @Override
    public Future<JsonObject> extension(final String appId, final boolean open) {
        Objects.requireNonNull(appId);
        final JsonObject appJ = Ut.vId(appId);
        return Ark.ofConfigure().modularize(appId, open).compose(moduleJ -> {
            appJ.mergeIn((JsonObject) moduleJ, true);
            if (open) {
                // open = true 可启用“登录参数”
                return Ux.future(appJ);
            }

            // open = false 的时候才读取 bags 节点的数据，否则不读取
            return Ark.ofBag().modularize(appId, false).compose(bagJ -> {
                final JsonArray bags = (JsonArray) bagJ;
                appJ.put(KName.App.BAGS, bags);
                return Ux.future(appJ);
            });
        });
    }
}

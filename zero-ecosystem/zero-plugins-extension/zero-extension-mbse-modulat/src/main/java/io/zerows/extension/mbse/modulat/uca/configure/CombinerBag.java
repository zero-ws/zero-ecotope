package io.zerows.extension.mbse.modulat.uca.configure;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.modulat.domain.tables.pojos.BBag;
import io.zerows.unity.Ux;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class CombinerBag implements Combiner<BBag, ConcurrentMap<String, BBag>> {
    @Override
    public Future<JsonObject> configure(final BBag bag, final ConcurrentMap<String, BBag> map) {
        final JsonObject uiConfig = Ut.toJObject(bag.getUiConfig());
        final JsonObject formConfig = Ut.visitJObject(uiConfig, KName.CONFIG, "_form");
        // ui capture
        final JsonArray ui = Ut.valueJArray(formConfig, "ui");
        if (Ut.isNil(ui)) {
            return Ux.future(uiConfig);
        }

        // ui calculation on children
        final ConcurrentMap<String, JsonArray> mapJ = new ConcurrentHashMap<>();
        map.forEach((code, itemBag) -> {
            final JsonObject itemJ = Ut.toJObject(itemBag.getUiConfig());
            final JsonArray itemUi = Ut.visitJArray(itemJ, KName.CONFIG, "_form", "ui");
            if (Ut.isNotNil(itemUi)) {
                mapJ.put(code, itemUi);
            }
        });

        // ui replace, here the `Ref` means reference and will be replaced directly
        /*
         * 此处构造 uiTo 的时候，一般会根据 BAG 的关系来执行构造，现阶段的版本的基本限制是表单中的 segment 部分的配置必须是 BAG 的名称，
         * 前缀还必须遵守 subject 的基础规范，也就是说同一个 B_BAG 包中的配置部分子包配置不可以拆分到表单的两段，每个子包必须有一个
         * 表单的 segment 来存放对应配置，整体配置模型如下
         * B_BAG ( parentId = null )    uiConfig
         *   B_BAG 子包                  uiConfig
         */
        final JsonArray uiTo = new JsonArray();
        ui.forEach(item -> {
            if (item instanceof JsonArray) {
                // JsonArray
                uiTo.add(item);
            } else {
                // String Literal
                uiTo.addAll(mapJ.getOrDefault(item.toString(), new JsonArray()));
            }
        });
        final JsonObject configRef = uiConfig.getJsonObject(KName.CONFIG);
        final JsonObject formRef = configRef.getJsonObject("_form");
        formRef.put("ui", uiTo);

        // Double Check for Ensure
        {
            configRef.put("_form", formRef);
            uiConfig.put(KName.CONFIG, configRef);
        }
        // combiner
        final Combiner<JsonObject, BBag> bagCombiner = Combiner.outBag();
        final Combiner<JsonObject, Collection<BBag>> childrenCombiner = Combiner.outChildren();
        return Ux.future(uiConfig)
            // bag
            .compose(response -> bagCombiner.configure(response, bag))
            // bag -> children
            .compose(response -> childrenCombiner.configure(response, map.values()));
    }
}

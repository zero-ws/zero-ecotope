package io.zerows.extension.mbse.basement.uca.apply;

import io.zerows.ams.constant.em.modeling.EmModel;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;

import static io.zerows.extension.mbse.basement.util.Ao.LOG;

class ModelDefault implements AoDefault {

    @Override
    public void applyJson(final JsonObject model) {
        LOG.Uca.debug(this.getClass(), "「DFT」模型输入值: {0}", model.encode());
        /*
         * 默认值：
         * key
         * keyField: 默认key
         * type
         * active
         * language
         * metadata
         */
        AoDefault.apply(model, "type", EmModel.Type.DIRECT.name());
        AoDefault.apply(model, "keyField", KName.KEY);
        AoDefault.apply(model);
    }
}

package io.zerows.extension.module.modulat.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.modeling.EmModel;

/**
 * 配置器，用于提取专用配置，替换原来的 Ark
 */
public interface EquipFor {

    Cc<String, EquipFor> CC_SKELETON = Cc.openThread();

    static EquipFor of(final boolean isOpen) {
        if (isOpen) {
            return CC_SKELETON.pick(EquipForOpen::new, EquipForOpen.class.getName());
        } else {
            return CC_SKELETON.pick(EquipForData::new, EquipForData.class.getName());
        }
    }

    Future<JsonObject> configure(String appId, EmModel.By by);
}

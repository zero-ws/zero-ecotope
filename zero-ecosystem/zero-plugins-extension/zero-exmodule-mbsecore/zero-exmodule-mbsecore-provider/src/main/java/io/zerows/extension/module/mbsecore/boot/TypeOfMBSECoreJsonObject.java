package io.zerows.extension.module.mbsecore.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.mbsecore.domain.tables.MAttribute;
import io.zerows.extension.module.mbsecore.domain.tables.MModel;

import java.util.List;
import java.util.Map;

public class TypeOfMBSECoreJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // MAttribute
            Map.of(
                MAttribute.M_ATTRIBUTE.SOURCE_CONFIG.getName(), MAttribute.M_ATTRIBUTE.getName(),
                MAttribute.M_ATTRIBUTE.SOURCE_EXTERNAL.getName(), MAttribute.M_ATTRIBUTE.getName(),
                MAttribute.M_ATTRIBUTE.SOURCE_REFERENCE.getName(), MAttribute.M_ATTRIBUTE.getName()
            ),
            // MModel
            Map.of(
                MModel.M_MODEL.RULE_UNIQUE.getName(), MModel.M_MODEL.getName(),
                MModel.M_MODEL.SPIDER.getName(), MModel.M_MODEL.getName()
            )
        );
    }
}

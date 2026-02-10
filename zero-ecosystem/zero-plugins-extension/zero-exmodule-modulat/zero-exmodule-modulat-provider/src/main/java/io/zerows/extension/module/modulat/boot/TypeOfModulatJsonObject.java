package io.zerows.extension.module.modulat.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.modulat.domain.tables.BBag;
import io.zerows.extension.module.modulat.domain.tables.BBlock;

import java.util.List;
import java.util.Map;

public class TypeOfModulatJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // BBag
            Map.of(
                BBag.B_BAG.UI_CONFIG.getName(), BBag.B_BAG.getName(),
                BBag.B_BAG.UI_STYLE.getName(), BBag.B_BAG.getName()
            ),
            // BBlock
            Map.of(
                BBlock.B_BLOCK.UI_CONFIG.getName(), BBlock.B_BLOCK.getName(),
                BBlock.B_BLOCK.UI_CONTENT.getName(), BBlock.B_BLOCK.getName(),
                BBlock.B_BLOCK.UI_STYLE.getName(), BBlock.B_BLOCK.getName()
            )
        );
    }
}

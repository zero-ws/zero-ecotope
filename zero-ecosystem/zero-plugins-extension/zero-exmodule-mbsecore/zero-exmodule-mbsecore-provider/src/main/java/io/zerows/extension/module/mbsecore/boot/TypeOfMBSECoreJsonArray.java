package io.zerows.extension.module.mbsecore.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.mbsecore.domain.tables.MAcc;
import io.zerows.extension.module.mbsecore.domain.tables.MIndex;
import io.zerows.extension.module.mbsecore.domain.tables.MKey;

import java.util.List;
import java.util.Map;

public class TypeOfMBSECoreJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // MAcc
            Map.of(
                MAcc.M_ACC.RECORD_JSON.getName(), MAcc.M_ACC.getName(),
                MAcc.M_ACC.RECORD_RAW.getName(), MAcc.M_ACC.getName()
            ),
            // MIndex
            Map.of(
                MIndex.M_INDEX.COLUMNS.getName(), MIndex.M_INDEX.getName()
            ),
            // MKey
            Map.of(
                MKey.M_KEY.COLUMNS.getName(), MKey.M_KEY.getName()
            )
        );
    }
}

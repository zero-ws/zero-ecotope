package io.zerows.extension.module.integration.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.integration.domain.tables.IDirectory;

import java.util.List;
import java.util.Map;

public class TypeOfIntegrationJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // IDirectory
            Map.of(
                IDirectory.I_DIRECTORY.VISIT_GROUP.getName(), IDirectory.I_DIRECTORY.getName(),
                IDirectory.I_DIRECTORY.VISIT_ROLE.getName(), IDirectory.I_DIRECTORY.getName(),
                IDirectory.I_DIRECTORY.VISIT_MODE.getName(), IDirectory.I_DIRECTORY.getName()
            )
        );
    }
}

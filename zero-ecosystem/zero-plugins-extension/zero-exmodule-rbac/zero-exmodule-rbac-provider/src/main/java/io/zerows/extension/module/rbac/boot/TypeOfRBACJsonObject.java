package io.zerows.extension.module.rbac.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.rbac.domain.tables.SResource;

import java.util.List;
import java.util.Map;

public class TypeOfRBACJsonObject extends TypeOfJsonObject {
    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // SResource
            Map.of(
                SResource.S_RESOURCE.SEEK_CONFIG.getName(), SResource.S_RESOURCE.getName(),
                SResource.S_RESOURCE.SEEK_SYNTAX.getName(), SResource.S_RESOURCE.getName()
            )
        );
    }
}

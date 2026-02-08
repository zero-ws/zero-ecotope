package io.zerows.extension.module.rbac.spi;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.rbac.domain.tables.SResource;

import java.util.Map;

public class TypeOfRBACJsonObject extends TypeOfJsonObject {
    @Override
    protected Map<String, String> regexMeta() {
        return Map.of(
            // SResource
            SResource.S_RESOURCE.SEEK_CONFIG.getName(), SResource.S_RESOURCE.getName(),
            SResource.S_RESOURCE.SEEK_SYNTAX.getName(), SResource.S_RESOURCE.getName()
        );
    }
}

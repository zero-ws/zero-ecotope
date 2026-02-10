package io.zerows.extension.module.mbseapi.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.mbseapi.domain.tables.IApi;
import io.zerows.extension.module.mbseapi.domain.tables.IService;

import java.util.List;
import java.util.Map;

public class TypeOfMBSEApiJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // IApi
            Map.of(
                IApi.I_API.PARAM_CONTAINED.getName(), IApi.I_API.getName(),
                IApi.I_API.PARAM_REQUIRED.getName(), IApi.I_API.getName(),
                IApi.I_API.PRODUCES.getName(), IApi.I_API.getName(),
                IApi.I_API.CONSUMES.getName(), IApi.I_API.getName()
            ),
            // IService
            Map.of(
                IService.I_SERVICE.DICT_CONFIG.getName(), IService.I_SERVICE.getName()
            )
        );
    }
}

package io.zerows.extension.module.mbseapi.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.mbseapi.domain.tables.IApi;
import io.zerows.extension.module.mbseapi.domain.tables.IJob;
import io.zerows.extension.module.mbseapi.domain.tables.IService;

import java.util.List;
import java.util.Map;

public class TypeOfMBSEApiJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // IApi
            Map.of(
                IApi.I_API.IN_MAPPING.getName(), IApi.I_API.getName(),
                IApi.I_API.IN_RULE.getName(), IApi.I_API.getName()
            ),
            // IJob
            Map.of(
                IJob.I_JOB.ADDITIONAL.getName(), IJob.I_JOB.getName(),
                IJob.I_JOB.DURATION_CONFIG.getName(), IJob.I_JOB.getName()
            ),
            // IService
            Map.of(
                IService.I_SERVICE.RULE_UNIQUE.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.CHANNEL_CONFIG.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.DICT_EPSILON.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.MAPPING_CONFIG.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.SERVICE_CONFIG.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.CONFIG_DATABASE.getName(), IService.I_SERVICE.getName(),
                IService.I_SERVICE.CONFIG_INTEGRATION.getName(), IService.I_SERVICE.getName()
            )
        );
    }
}

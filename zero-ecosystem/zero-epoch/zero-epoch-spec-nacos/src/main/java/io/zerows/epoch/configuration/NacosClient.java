package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;

interface NacosClient {
    Cc<String, NacosClient> CC_NACOS_CLIENT = Cc.openThread();

    static NacosClient of() {
        return CC_NACOS_CLIENT.pick(NacosClientImpl::new);
    }

    JsonObject readConfig(NacosMeta meta, NacosOptions serverOptions);
}

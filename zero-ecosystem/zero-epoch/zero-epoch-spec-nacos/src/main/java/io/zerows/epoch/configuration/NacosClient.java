package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;

interface NacosClient {
    Cc<String, NacosClient> CC_NACOS_CLIENT = Cc.openThread();

    static NacosClient of() {
        return CC_NACOS_CLIENT.pick(NacosClientImpl::new);
    }

    String readConfig(NacosMeta meta, NacosOptions serverOptions);
}

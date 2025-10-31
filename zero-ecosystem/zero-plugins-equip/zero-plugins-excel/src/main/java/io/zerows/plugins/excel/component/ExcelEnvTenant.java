package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.plugins.excel.metadata.ExTenant;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-06-12
 */
public class ExcelEnvTenant implements ExcelEnv<ExTenant> {
    @Override
    public ExTenant prepare(final JsonObject config) {
        if (!config.containsKey(YmlCore.excel.TENANT)) {
            return null;
        }


        final JsonObject tenantJ = Ut.ioJObject(config.getString(YmlCore.excel.TENANT));
        if (Ut.isNil(tenantJ)) {
            return null;
        }

        this.logger().debug("[ Έξοδος ] Configuration tenant for Importing: {0}", tenantJ.encode());
        return ExTenant.create(tenantJ);
    }
}

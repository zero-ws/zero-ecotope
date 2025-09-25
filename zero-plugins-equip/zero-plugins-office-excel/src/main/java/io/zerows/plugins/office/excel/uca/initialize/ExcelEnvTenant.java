package io.zerows.plugins.office.excel.uca.initialize;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.util.Ut;
import io.zerows.plugins.office.excel.atom.ExTenant;

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

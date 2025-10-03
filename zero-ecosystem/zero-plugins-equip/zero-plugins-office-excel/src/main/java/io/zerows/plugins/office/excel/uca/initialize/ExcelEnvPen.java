package io.zerows.plugins.office.excel.uca.initialize;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.support.Ut;
import io.zerows.plugins.office.excel.ExTpl;

/**
 * @author lang : 2024-06-12
 */
public class ExcelEnvPen implements ExcelEnv<ExTpl> {
    @Override
    public ExTpl prepare(final JsonObject config) {
        if (!config.containsKey(YmlCore.excel.PEN)) {
            return null;
        }

        final String componentStr = config.getString(YmlCore.excel.PEN);
        this.logger().debug("[ Έξοδος ] Configuration pen for Exporting: {0}", componentStr);

        if (Ut.isNil(componentStr)) {
            return null;
        }


        final Class<?> tplCls = Ut.clazz(componentStr, null);
        if (!Ut.isImplement(tplCls, ExTpl.class)) {
            return null;
        }
        return Ut.singleton(componentStr);
    }
}

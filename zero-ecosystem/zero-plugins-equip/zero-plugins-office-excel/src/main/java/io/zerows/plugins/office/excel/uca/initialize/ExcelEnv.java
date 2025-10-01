package io.zerows.plugins.office.excel.uca.initialize;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.program.Ut;

/**
 * @author lang : 2024-06-12
 */
public interface ExcelEnv<R> {

    Cc<String, ExcelEnv<?>> CCT_ENV = Cc.openThread();

    static ExcelEnv<?> of(final Class<?> classImpl) {
        return CCT_ENV.pick(() -> Ut.instance(classImpl), classImpl.getName());
    }

    R prepare(JsonObject config);

    default OLog logger() {
        return Ut.Log.plugin(this.getClass());
    }
}

package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.spec.YmSpec;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.plugins.excel.style.ExTpl;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2024-06-12
 */
@Slf4j
public class ExcelEnvPen implements ExcelEnv<ExTpl> {
    @Override
    public ExTpl prepare(final JsonObject config) {
        if (!config.containsKey(YmSpec.excel.pen)) {
            return null;
        }

        final String componentStr = config.getString(YmSpec.excel.pen);

        if (Ut.isNil(componentStr)) {
            return null;
        }


        final Class<?> tplCls = Ut.clazz(componentStr, null);
        if (!Ut.isImplement(tplCls, ExTpl.class)) {
            return null;
        }
        log.info("{} Pen 导出数据的画笔配置: {}", ExcelConstant.K_PREFIX, componentStr);
        return Ut.singleton(componentStr);
    }
}

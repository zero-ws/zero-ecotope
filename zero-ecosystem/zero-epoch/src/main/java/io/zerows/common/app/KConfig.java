package io.zerows.common.app;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.util.HUt;
import io.zerows.core.running.boot.KSetting;
import io.zerows.specification.configuration.HConfig;

/**
 * 「常用配置」
 * 读取配置专用，常用配置可以用来直接生成 {@link KSetting}
 *
 * @author lang : 2023-05-30
 */
public class KConfig implements HConfig {

    private final JsonObject options = new JsonObject();
    private Class<?> preCls;

    @Override
    public JsonObject options() {
        return this.options;
    }

    @Override
    public HConfig options(final JsonObject options) {
        this.options.mergeIn(HUt.valueJObject(options));
        return this;
    }

    @Override
    public Class<?> pre() {
        return this.preCls;
    }

    @Override
    public HConfig pre(final Class<?> preCls) {
        this.preCls = preCls;
        return this;
    }

    @Override
    public HConfig put(final String field, final Object value) {
        this.options.put(field, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final String field) {
        return (T) this.options.getValue(field);
    }
}

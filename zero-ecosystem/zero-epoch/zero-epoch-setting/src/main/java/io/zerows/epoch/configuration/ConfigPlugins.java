package io.zerows.epoch.configuration;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.r2mo.SourceReflect;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-13
 */
@Slf4j
public class ConfigPlugins extends ConfigNorm {
    private final ConcurrentMap<Class<?>, HConfig> pluginMap = new ConcurrentHashMap<>();

    public ConfigPlugins plugin(final JsonObject pluginsJ) {
        if (Objects.isNull(pluginsJ) || Ut.isNil(pluginsJ)) {
            return this;
        }
        pluginsJ.fieldNames().forEach(name -> {
            final Class<?> pluginCls = SourceReflect.clazz(name);
            if (Objects.nonNull(pluginCls)) {
                final JsonObject options = Ut.valueJObject(pluginsJ, name);
                this.pluginMap.put(pluginCls, new ConfigNorm().putOptions(options).putExecutor(pluginCls));
            }
        });
        return this;
    }

    public HConfig plugin(final Class<?> pluginCls) {
        return this.pluginMap.getOrDefault(pluginCls, null);
    }

    public Set<HConfig> plugin() {
        return new ConcurrentHashSet<>(this.pluginMap.values());
    }
}

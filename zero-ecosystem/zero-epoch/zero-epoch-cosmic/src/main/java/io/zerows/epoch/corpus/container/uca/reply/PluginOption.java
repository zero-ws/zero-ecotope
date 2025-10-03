package io.zerows.epoch.corpus.container.uca.reply;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.platform.constant.VName;
import io.zerows.epoch.corpus.metadata.element.JComponent;
import io.zerows.management.OZeroStore;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 插件专用配置项，其数据格式如
 * <pre><code>
 *     extension:
 *       region:
 *         component:
 *         config:
 *       auditor:
 *         component:
 *         config:
 * </code></pre>
 *
 * @author lang : 2024-06-27
 */
public class PluginOption {
    private static final Cc<String, PluginOption> CC_SKELETON = Cc.open();
    private final ConcurrentMap<String, JComponent> configMap = new ConcurrentHashMap<>();
    private final boolean isEnabled;

    private PluginOption(final Bundle owner) {
        // 是否配置了 extension 节点
        this.isEnabled = OZeroStore.is(YmlCore.extension.__KEY);
        if (!this.isEnabled) {
            return;
        }

        // 只有配置了的情况下才执行配置提取
        final JsonObject configData = OZeroStore.option(YmlCore.extension.__KEY);
        Ut.<JsonObject>itJObject(configData).forEach(configEntry -> {
            final Class<?> componentCls = Ut.clazz(Ut.valueString(configEntry.getValue(), VName.COMPONENT), null);
            if (Objects.nonNull(componentCls)) {
                final String key = configEntry.getKey();
                final JsonObject config = Ut.valueJObject(configEntry.getValue(), VName.CONFIG);
                final JComponent componentRef = JComponent.create(key, componentCls).bind(config);
                this.configMap.put(key, componentRef);
            }
        });
    }

    public static PluginOption of(final Bundle owner) {
        final String cacheKey = Ut.Bnd.keyCache(owner, PluginOption.class);
        return CC_SKELETON.pick(() -> new PluginOption(owner), cacheKey);
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public JComponent getComponent(final String key) {
        return this.configMap.getOrDefault(key, null);
    }
}

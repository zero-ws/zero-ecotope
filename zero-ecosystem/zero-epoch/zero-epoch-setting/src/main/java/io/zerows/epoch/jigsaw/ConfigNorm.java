package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.development.HLog;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「常用配置」ConfigNorm
 * 通用配置实现，基于 {@link HConfig}
 * <pre>
 *     子类：
 *     - {@link ConfigContainer}
 *     - {@link ConfigInstance}
 * </pre>
 *
 * @see HConfig
 * @since 2023-05-30
 */
@Slf4j
public class ConfigNorm implements HConfig, HLog {

    private final ConcurrentMap<String, Class<?>> executor = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Object> reference = new ConcurrentHashMap<>();
    /**
     * 📦 配置项容器。
     * <p>使用 Vert.x 的 {@link JsonObject} 管理键值对，便于与 Vert.x 生态统一。</p>
     * <p><b>注意：</b>默认可变，若需只读可在外层封装快照或拷贝。</p>
     */
    private final JsonObject options = new JsonObject();

    public ConfigNorm() {
    }

    @Override
    public <T> HConfig putRef(final String field, final T value) {
        this.reference.put(field, value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T ref(final String refKey) {
        return (T) this.reference.getOrDefault(refKey, null);
    }

    @Override
    public JsonObject options() {
        return this.options;
    }

    @Override
    public <T> T options(final Class<T> classYm) {
        return Ut.deserialize(this.options, classYm);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T options(final String field) {
        return (T) this.options.getValue(field);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T options(final String field, final T defaultValue) {
        return (T) this.options.getValue(field, defaultValue);
    }

    @Override
    public HConfig putOptions(final JsonObject options) {
        if (Ut.isNotNil(options)) {
            this.options.mergeIn(options, true);
        }
        return this;
    }

    @Override
    public HConfig putOptions(final String field, final Object value) {
        this.options.put(field, value);
        return this;
    }

    @Override
    public HConfig putExecutor(final String field, final Class<?> clazz) {
        this.executor.put(field, clazz);
        return this;
    }

    @Override
    public Class<?> executor(final String field) {
        return this.executor.get(field);
    }
}

package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.specification.configuration.HConfig;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「常用配置」KConfig
 *
 * <p>用于读取与承载“最小配置集合”的轻量配置器，实现 {@link HConfig} 接口。</p>
 *
 * <h2>🧩 设计要点</h2>
 * <ul>
 *   <li>🔧 <b>预处理器</b>：通过 指定一个预处理类（通常是某个配置预处理/校验/转换器的 Class）。</li>
 *   <li>📦 <b>配置容器</b>：内部使用 Vert.x 的 {@link JsonObject} 存放键值对（见 {@link #options}）。</li>
 *   <li>🔁 <b>链式调用</b>：配合 {@link Accessors#fluent()} 提供流式 API（如：{@code cfg.put("k","v").put("a",1)}）。</li>
 * </ul>
 *
 * <h2>⚠️ 并发与线程模型</h2>
 * <ul>
 *   <li>🧵 <b>线程安全</b>：本类未做同步控制；在 <i>单线程/同一事件循环</i> 中读取/写入更安全。</li>
 *   <li>🚦 <b>跨线程访问</b>：若需跨线程并发访问，请自行在外层加锁或在构建完成后改为只读使用。</li>
 * </ul>
 *
 * <h2>💡 典型用法</h2>
 * <pre>{@code
 * // 1) 构建配置并设置预处理器
 * ZeroConfig cfg = new ZeroConfig()
 *     .executor(MyPreprocessor.class)
 *     .option("endpoint", "https://api.example.com")
 *     .option("timeoutMs", 3000);
 *
 * // 2) 读取配置
 * String endpoint = cfg.option("endpoint");
 * Integer timeout = cfg.option("timeoutMs");
 *
 * // 3) 获取底层 JsonObject 以便与 Vert.x 生态对接
 * JsonObject raw = cfg.options();
 * }</pre>
 *
 * @author lang
 * @see HConfig
 * @since 2023-05-30
 */
public class ZeroConfig implements HConfig {

    private final ConcurrentMap<String, Class<?>> executor = new ConcurrentHashMap<>();
    /**
     * 📦 配置项容器。
     * <p>使用 Vert.x 的 {@link JsonObject} 管理键值对，便于与 Vert.x 生态统一。</p>
     * <p><b>注意：</b>默认可变，若需只读可在外层封装快照或拷贝。</p>
     */
    private final JsonObject options = new JsonObject();

    public ZeroConfig(final MMComponent component) {
        Objects.requireNonNull(component);
        this.options.mergeIn(component.getConfig(), true);
        this.executor.put(DEFAULT_CONFIG_KEY, component.getClass());
    }

    @Override
    public JsonObject options() {
        return this.options;
    }

    /**
     * ➕ 写入/覆盖配置项。
     *
     * <p>支持链式调用：{@code config.put("k1", v1).put("k2", v2)}</p>
     *
     * @param field 配置字段名（键） 🔑
     * @param value 配置值（可为任意可被 {@link JsonObject} 支持的类型） 🧱
     *
     * @return 当前 {@code HConfig} 实例（便于链式调用） 🔗
     */
    @Override
    public HConfig option(final String field, final Object value) {
        this.options.put(field, value);
        return this;
    }

    @Override
    public HConfig executor(final String field, final Class<?> clazz) {
        this.executor.put(field, clazz);
        return this;
    }

    @Override
    public Class<?> executor(final String field) {
        return this.executor.get(field);
    }

    /**
     * 🔍 读取配置项。
     *
     * <p>调用方需自行确保类型正确，建议在调用处进行必要的类型断言或转换。</p>
     *
     * @param field 配置字段名（键） 🔑
     * @param <T>   期望返回的类型参数
     *
     * @return 配置值；若键不存在则返回 {@code null}（与 {@link JsonObject#getValue(String)} 行为一致） 🫥
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T option(final String field) {
        return (T) this.options.getValue(field);
    }
}

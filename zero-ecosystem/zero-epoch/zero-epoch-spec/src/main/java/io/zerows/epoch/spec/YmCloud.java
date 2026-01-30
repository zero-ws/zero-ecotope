package io.zerows.epoch.spec;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ☁️ 动态云端配置容器 (Dynamic Cloud Configuration Container)
 *
 * <p>
 * 作为一个“配置黑洞”或“扩展坞”，该类不定义任何具体的云组件字段（如 Nacos, K8s），
 * 而是利用 Jackson 的 {@code AnySetter} 机制，将 YAML/JSON 中 {@code cloud:} 节点下的
 * 任意子节点动态捕获并转换为 Vert.x 原生的 {@link JsonObject} 存储。
 * </p>
 *
 * <pre>
 * 🧩 设计意图 (Design Intent):
 * 1. 解耦 (Decoupling) : 核心框架无需感知具体的云组件（Nacos/Consul/Etcd），仅负责透传配置。
 * 2. 动态 (Dynamic)    : 配置文件中新增组件支持无需修改 Java 代码。
 * 3. 兼容 (Compatible) : 内部自动将 {@code Map} 转为 {@code JsonObject}，无缝对接 Vert.x 生态。
 *
 * 🌰 映射示例 (Mapping Example):
 * [YAML Input]
 * vertx:
 * cloud:
 * nacos: { server-addr: "..." }  --> items.put("nacos", jsonObject)
 * k8s:   { namespace: "..." }    --> items.put("k8s", jsonObject)
 * </pre>
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmCloud implements Serializable {

    /**
     * 📦 内部动态存储容器
     * 存储所有未显式定义的云组件配置，Key 为组件名（如 "nacos"），Value 为配置详情。
     * 标记为 {@link JsonIgnore} 是为了防止双重序列化，必须通过 {@link #getItems()} 进行扁平化输出。
     */
    @JsonIgnore
    private Map<String, JsonObject> items = new HashMap<>();

    /**
     * 📥 动态属性捕获 (Capture Hook)
     *
     * <p>
     * 反序列化阶段（YAML -> Java）触发。
     * 当 Jackson 扫描到 {@code YmCloud} 中不存在的字段时，会调用此方法。
     * 此处执行了关键的数据类型转换：{@code java.util.Map -> io.vertx.core.json.JsonObject}。
     * </p>
     *
     * @param key   配置组件名 (e.g., "nacos", "zookeeper")
     * @param value 原始配置值 (通常由 Jackson 解析为 LinkedHashMap)
     */
    @JsonAnySetter
    public void add(final String key, final Map<String, Object> value) {
        this.items.put(key, new JsonObject(value));
    }

    /**
     * 📤 扁平化输出 (Flatten Hook)
     *
     * <p>
     * 序列化阶段（Java -> YAML/JSON）触发。
     * 将 {@code items} 内部的键值对“平铺”到当前对象的根层级，
     * 避免输出结果中出现多余的 {@code "items": { ... }} 层级。
     * </p>
     *
     * @return 包含所有动态配置的 Map 视图
     */
    @JsonAnyGetter
    public Map<String, JsonObject> getItems() {
        return this.items;
    }

    /**
     * 🔍 原生配置提取 (Raw Extraction)
     *
     * <p>
     * 根据组件名称直接获取其对应的 {@link JsonObject} 配置。
     * 这是获取云端连接参数的最直接方式，获取后可直接透传给 Vert.x 的 ConfigStore 或 Client。
     * </p>
     *
     * @param key 组件 Key (如 "nacos", "etcd")
     * @return 对应的 {@link JsonObject} 配置，如果不存在则返回 null
     */
    public JsonObject getItem(final String key) {
        return this.items.get(key);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
package io.zerows.epoch.configuration;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.io.Serializable;

/**
 * 双设计
 * <pre>
 *     1. 类型 T 构造其中的对象引用（反序列化之后的结果）
 *     2. 类型 {@link JObject} 构造原始数据引用
 * </pre>
 *
 * @author lang : 2025-12-16
 */
public class ConfigFs<T> implements Serializable {

    private static final JUtil UT = SPI.V_UTIL;
    private final T reference;
    private final JObject json;

    public ConfigFs(final JObject json, final Class<T> clazz) {
        this.json = json;
        this.reference = UT.deserializeJson(json, clazz);
    }

    public T refT() {
        return this.reference;
    }

    public JObject refJson() {
        return this.json;
    }
}

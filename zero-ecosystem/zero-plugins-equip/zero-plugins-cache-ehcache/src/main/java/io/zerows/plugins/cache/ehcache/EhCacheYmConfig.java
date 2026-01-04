package io.zerows.plugins.cache.ehcache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * <pre>
 *     配置位置:
 *     cache:
 *       ehcache:
 *         expiredAt: ???
 *         size: ???
 *         # 下边也可自动计算，默认使用 Object.class（无法解析时）
 *         classK: ???
 *         classV: ???
 * </pre>
 */
@Data
public class EhCacheYmConfig implements Serializable {
    private String expiredAt = "30s";       // 30s
    private int size = 10000;               // 尺寸信息
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classK = Object.class; // Key 类型
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classV = Object.class; // Value 类型

    public Duration expiredAt() {
        return R2MO.toDuration(this.expiredAt);
    }
}

package io.zerows.plugins.cache.caffeine;

import io.r2mo.base.util.R2MO;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * <pre>
 * 配置位置:
 * cache:
 *   caffeine:
 *     initialCapacity: 16
 *     maximumSize: 1000
 *     expiredAt: 30s
 *     idleAt: 10m
 *     recordStats: true
 *     weakKeys: false
 *     softValues: false
 * </pre>
 *
 * @author lang : 2025-12-31
 */
@Data
public class CaffeineYmConfig implements Serializable {

    // ------------------- 容量控制 -------------------
    /**
     * 初始的哈希表大小。
     * 有助于减少哈希表扩容时的开销。
     */
    private Integer initialCapacity = 16;

    /**
     * 最大缓存条目数 (Maximum Size)。
     * 当缓存数量达到该值时，Caffeine 会根据 Window TinyLfu 算法驱逐旧数据。
     * 如果为 null 或 <= 0，则不限制大小（慎用，可能导致 OOM）。
     */
    private Long maximumSize = 1000L;


    // ------------------- 时间控制 -------------------
    /**
     * 写入过期时间 (Expire After Write)。
     * 数据写入或更新后，经过指定时间过期。
     * 对应 Caffeine 的 .expireAfterWrite()
     */
    private String expiredAt = "30s";

    /**
     * 访问/闲置过期时间 (Expire After Access)。
     * 数据最后一次被读取或写入后，经过指定时间过期。适合做热点数据缓存。
     * 对应 Caffeine 的 .expireAfterAccess()
     * (默认 null，即不启用)
     */
    private String idleAt;


    // ------------------- 引用类型 (GC 相关) -------------------
    /**
     * 是否使用弱引用键 (Weak Keys)。
     * 启用后，当 Key 没有其他强引用时，会被 GC 回收。
     * 注意：启用此项后，key 必须使用 == 比较而不是 equals。
     */
    private Boolean weakKeys = false;

    /**
     * 是否使用弱引用值 (Weak Values)。
     * 启用后，当 Value 没有其他强引用时，会被 GC 回收。
     */
    private Boolean weakValues = false;

    /**
     * 是否使用软引用值 (Soft Values)。
     * 启用后，Value 会在 JVM 内存不足时被 GC 回收（比 Weak 存活时间长）。
     * 适合做对内存敏感的缓存。
     */
    private Boolean softValues = false;


    // ------------------- 监控 -------------------
    /**
     * 是否开启统计 (Record Stats)。
     * 开启后可以监控命中率 (Hit Rate)、加载时间等指标。
     * 生产环境建议视性能要求决定是否开启。
     */
    private Boolean recordStats = false;


    // ------------------- 辅助方法 -------------------

    public Duration expiredAt() {
        return R2MO.toDuration(this.expiredAt);
    }

    public Duration idleAt() {
        return R2MO.toDuration(this.idleAt);
    }
}
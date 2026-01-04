package io.zerows.plugins.redis.cache;

import io.r2mo.base.util.R2MO;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

/**
 * <pre>
 * 配置位置：
 * cache:
 *   redis:
 *     prefix: ZEROWS:CACHE:   # Redis Key 前缀，建议大写以区分
 *     expiredAt: 30m          # 默认过期时间
 *     nullValue: false        # 是否缓存空值（防穿透）
 *     nullValueAt: 60s        # 空值缓存的过期时间（通常较短）
 *     format: json            # 序列化格式: json / string
 * </pre>
 *
 * @author lang : 2026-01-01
 */
@Data
public class RedisYmConfig implements Serializable {

    /**
     * Redis Key 的全局前缀。
     * <p>
     * 作用：
     * 1. 命名空间隔离，防止与其他业务 Key 冲突。
     * 2. 在执行 clear 操作时，仅通过 pattern 匹配删除该前缀的 Key，避免误删整个数据库。
     */
    private String prefix = "ZEROWS:CACHE:";

    /**
     * 默认缓存过期时间。
     * <p>
     * 格式支持：30s, 5m, 1h, 1d 等。
     * 对应 Redis 的 EXPIRE 命令。
     */
    private String expiredAt = "30m";

    /**
     * 是否允许缓存空值 (Null Value)。
     * <p>
     * 场景：用于防止缓存穿透 (Cache Penetration)。
     * 如果数据库查询不到数据，是否将一个空标记写入 Redis。
     */
    private Boolean nullValue = false;

    /**
     * 空值缓存的过期时间。
     * <p>
     * 仅在 nullValue = true 时生效。
     * 通常设置得比 expiredAt 短（例如 60s），以便数据更新后能及时从数据库加载。
     */
    private String nullValueAt = "60s";

    /**
     * 数据序列化格式。
     * <p>
     * 可选值：
     * - "json": 使用 Jackson/Vert.x JsonObject 存储 (默认，通用性好)
     * - "string": 仅存储字符串 (性能最高，但仅适用于简单值)
     * - "binary": 二进制存储 (如 Protobuf/MsgPack，需额外适配)
     */
    private String format;

    // ------------------- 辅助方法 -------------------

    /**
     * 获取解析后的过期时间 Duration
     */
    public Duration expiredAt() {
        return R2MO.toDuration(this.expiredAt);
    }

    /**
     * 获取解析后的空值过期时间 Duration
     */
    public Duration nullValueAt() {
        return R2MO.toDuration(this.nullValueAt);
    }
}
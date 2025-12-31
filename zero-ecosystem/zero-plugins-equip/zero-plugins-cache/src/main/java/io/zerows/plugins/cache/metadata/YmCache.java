package io.zerows.plugins.cache.metadata;

import lombok.Data;

import java.io.Serializable;

/**
 * <pre>
 *     cache:
 *       caffeine:
 *         expired:          // SECONDS
 *         capacity:         // 最大缓存数量
 *         maximum-size:     // 最大缓存大小
 * </pre>
 *
 * @author lang : 2025-12-31
 */
@Data
public class YmCache implements Serializable {
    private YmCacheCaffeine caffeine;
}

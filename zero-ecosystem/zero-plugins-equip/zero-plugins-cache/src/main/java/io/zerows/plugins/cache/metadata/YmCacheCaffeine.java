package io.zerows.plugins.cache.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-12-31
 */
@Data
public class YmCacheCaffeine implements Serializable {
    private long expired = 30;  // 30s
    private int capacity = 1000; // 最大缓存数量
    @JsonProperty("maximum-size")
    private long maximumSize = 10 * 1024; // 最大缓存大小
}

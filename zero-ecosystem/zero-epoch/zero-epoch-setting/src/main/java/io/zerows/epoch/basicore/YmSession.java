package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-09
 */
@Data
public class YmSession implements Serializable {
    @JsonProperty("store-type")
    private String storeType;

    private int timeout = -1;   // 分钟

    private Cookie cookie;

    @Data
    public static class Cookie implements Serializable {
        private String name;
        @JsonProperty("max-age")
        private int maxAge;
    }
}

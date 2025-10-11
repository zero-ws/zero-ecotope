package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link YmSpec.vertx.cloud.nacos}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmNacos implements Serializable {
    private static final String KEY_SERVER_ADDR = "server-addr";
    private static final String KEY_FILE_EXTENSION = "file-extension";

    @JsonProperty(KEY_SERVER_ADDR)
    private String serverAddr;

    private String username;
    private String password;
    private String name;

    private Config config;

    private Discovery discovery;

    public static class Discovery implements Serializable {
        @JsonProperty(KEY_SERVER_ADDR)
        private String serverAddr;
        private String namespace;
    }

    public static class Config implements Serializable {
        @JsonProperty(KEY_SERVER_ADDR)
        private String serverAddr;
        private String namespace;
        private String prefix;
        @JsonProperty(KEY_FILE_EXTENSION)
        private String fileExtension;
    }
}

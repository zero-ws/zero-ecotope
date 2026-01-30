package io.zerows.epoch.spec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-11
 */
@Data
public class YmNeo4j implements Serializable {
    private String uri;
    private Authentication authentication;

    @Data
    public static class Authentication implements Serializable {
        private String username;
        private String password;
        private boolean encrypted = false;
    }
}

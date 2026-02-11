package io.zerows.plugins.security.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class YmSecurityDevelopment implements Serializable {
    @JsonProperty("header-name")
    private String headerName;
    @JsonProperty("header-value")
    private String headerValue;

    private String username;

    private String password;
}

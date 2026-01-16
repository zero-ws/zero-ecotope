package io.zerows.plugins.security.metadata;

import lombok.Data;

import java.io.Serializable;

@Data
public class YmSecurityAuthorization implements Serializable {
    private boolean enabled = false;
}

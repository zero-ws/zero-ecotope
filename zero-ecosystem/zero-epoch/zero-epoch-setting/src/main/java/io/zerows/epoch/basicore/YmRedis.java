package io.zerows.epoch.basicore;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-06
 */
@Data
public class YmRedis implements Serializable {
    private String host;
    private int port;
    private int timeout;
    private String password;
    private String database;
}

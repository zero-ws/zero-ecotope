package io.zerows.epoch.basicore;

import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.basicore.option.ClusterOptions;
import lombok.Data;

import java.io.Serializable;

/**
 * {@link VertxYml.vertx}
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmVertx implements Serializable {
    private YmVertxConfig config;
    private YmCloud cloud;
    private YmVertxConfig.Application application;
    private ClusterOptions cluster;
    private YmDataSource datasource;
    private YmSecurity security;
    private YmVertxData data;
}

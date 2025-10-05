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
    private YmVertxConfig config = new YmVertxConfig();
    private YmCloud cloud = new YmCloud();
    private YmVertxConfig.Application application = new YmVertxConfig.Application();
    private ClusterOptions cluster = new ClusterOptions();
    private YmDataSource datasource = new YmDataSource();
    private YmSecurity security = new YmSecurity();
    private YmVertxData data = new YmVertxData();
}

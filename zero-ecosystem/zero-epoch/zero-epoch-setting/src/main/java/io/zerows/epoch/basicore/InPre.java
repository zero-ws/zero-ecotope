package io.zerows.epoch.basicore;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
@Data
public class InPre implements Serializable {
    private InPreVertx vertx;
    private YmLogging logging;

    public InPreVertx.Config config() {
        return Objects.requireNonNull(this.vertx).getConfig();
    }

    public YmVertxConfig.Application application() {
        return Objects.requireNonNull(this.vertx).getApplication();
    }

    public YmCloud cloud() {
        return Objects.requireNonNull(this.vertx).getCloud();
    }
}

package io.zerows.extension.module.mbseapi.boot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.basicore.YmVertx;
import io.zerows.extension.module.mbseapi.plugins.JetPollux;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * 对应 vertx.yml 中的配置片段（新版），可承接 {@link YmSpec}
 * <pre>
 *     metamodel:
 *       router:
 *         path:                /api
 *         component:           {@link io.zerows.extension.module.mbseapi.plugins.JetPollux}
 *       deployment:
 *         worker:
 *           instances:         64
 *         agent:
 *           instances:         32
 * </pre>
 *
 * @author lang : 2025-12-22
 */
@Data
@Slf4j
public class YmMetamodel implements Serializable {
    private Router router = new Router();

    private YmVertx.Deployment deployment;

    @Data
    public static class Router implements Serializable {
        private String wall = "/api";       // 安全路径
        /**
         * 动态上下文和安全路径之间的关系：
         * <pre>
         *     动态上下文通常是位于 / 之后的动态 API 发布专用上下文，此上下文会配合 Uri 表结构中的动态路径执行计算，产生最终的动态路径等相关信息
         *     多用于核心系统的 API 发布路径计算，安全路径则作为备用！安全路径在动态之下
         * </pre>
         */
        private String context = "/";      // 动态上下文

        @JsonSerialize(using = ClassSerializer.class)
        @JsonDeserialize(using = ClassDeserializer.class)
        private Class<?> component = JetPollux.class;
    }

    public String apiContext() {
        return Objects.requireNonNull(this.router).context;
    }

    public String apiWall() {
        Objects.requireNonNull(this.router);
        final String context = this.router.context;
        final String wall = this.router.wall;
        return Ut.ioPath(context, wall);
    }

    public DeploymentOptions getWorkerOptions() {
        final DeploymentOptions options = this.optionOfDeployment(true);
        log.debug("[ XMOD ] ( Jet ) 动态 Worker 发布配置: {}", options);
        return options;
    }

    public DeploymentOptions getAgentOptions() {
        final DeploymentOptions options = this.optionOfDeployment(false);
        log.debug("[ XMOD ] ( Jet ) 动态 Agent 发布配置: {}", options);
        return options;
    }

    private DeploymentOptions optionOfDeployment(final boolean isWorker) {
        if (Objects.isNull(this.deployment)) {
            return new DeploymentOptions();
        }
        final JsonObject options;
        if (isWorker) {
            options = this.deployment.getWorker();
        } else {
            options = this.deployment.getAgent();
        }
        return new DeploymentOptions(options);
    }
}

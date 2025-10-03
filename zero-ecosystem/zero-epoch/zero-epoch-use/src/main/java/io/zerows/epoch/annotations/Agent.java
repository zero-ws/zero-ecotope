package io.zerows.epoch.annotations;

import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.enums.app.ServerType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For the specification, each agent should only publish one typed server.
 * It's defined by our team, it means that any standard bottle should not
 * contain different typed server instances.
 * But one agent could contain more than one servers distinguished by port.
 * ( Correct ): 1.Agent1 -- 2.HTTP -- 3.HttpServer1, HttpServer2;
 * ( Corrent ): 1.Agent1 -- 2.HTTP -- 3.HttpServer1
 * ( Wrong ): 1.Agent1 -- 2.HTTP -- 3.HttpServer1, RpcServer1;
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Agent {

    /**
     * Standard Instance Number
     * Default: 32
     *
     * @return deployment instance number
     */
    int instances() default KWeb.DEPLOY.INSTANCES;

    /**
     * Isolation Group
     * Default: __VERTX_ZERO__
     *
     * @return deployment group
     */
    String group() default KWeb.DEPLOY.VERTX_GROUP;

    /**
     * @return Whether enable HA
     */
    boolean ha() default KWeb.DEPLOY.HA;

    /**
     * Default server type: http
     *
     * @return The agent type for different servers.
     */
    ServerType type() default ServerType.HTTP;
}

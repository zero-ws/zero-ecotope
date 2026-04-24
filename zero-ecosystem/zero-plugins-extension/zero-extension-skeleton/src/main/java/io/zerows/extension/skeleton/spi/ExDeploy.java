package io.zerows.extension.skeleton.spi;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;

/**
 * ## 「Deploy」Application Deployment Uniform Interface
 *
 * <p>Provides the extension point for application instance deployment.
 * Each application module can implement this interface to provide
 * Docker container management, database provisioning, and runtime
 * directory setup for deployed application instances.</p>
 *
 * <p>The deployment workflow:</p>
 * <ol>
 *   <li>Validate deployment parameters (manifest, source)</li>
 *   <li>Provision database instance</li>
 *   <li>Create runtime directory structure under R2MO_HOME</li>
 *   <li>Load Docker image and start container</li>
 *   <li>Return deployment result with container/runtime metadata</li>
 * </ol>
 */
public interface ExDeploy {

    Cc<String, ExDeploy> CC_DEPLOY = Cc.open();

    static ExDeploy of(final Class<?> clazz) {
        return CC_DEPLOY.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    /**
     * Deploy an application instance.
     *
     * @param request Deployment request containing:
     *                - appId: application identifier
     *                - instanceName: unique instance name (e.g. HMS-001)
     *                - compositeDeploy: whether this is a composite deployment
     *                - manifest: parsed manifest.json from the release package
     *                - source: database source configuration
     * @return Deployment result containing:
     *         - dockerImage: the deployed Docker image tag
     *         - dockerContainer: the running container ID or name
     *         - databaseInstance: the provisioned database instance name
     *         - runtimeRoot: the runtime directory path
     *         - deploySteps: steps that were executed
     *         - status: DEPLOYED on success, FAILED on error
     */
    Future<JsonObject> deploy(JsonObject request);

    /**
     * Check whether the deployed instance is healthy.
     *
     * @param instanceKey The instance key (usually appId or instance name)
     * @return Health check result with status and optional message
     */
    default Future<JsonObject> healthCheck(final String instanceKey) {
        return Future.succeededFuture(new JsonObject()
            .put("status", "UNKNOWN")
            .put("message", "Health check not implemented"));
    }

    /**
     * Undeploy (tear down) an application instance.
     *
     * @param instanceKey The instance key to undeploy
     * @return Result of the undeploy operation
     */
    default Future<JsonObject> undeploy(final String instanceKey) {
        return Future.succeededFuture(new JsonObject()
            .put("status", "UNKNOWN")
            .put("message", "Undeploy not implemented"));
    }
}

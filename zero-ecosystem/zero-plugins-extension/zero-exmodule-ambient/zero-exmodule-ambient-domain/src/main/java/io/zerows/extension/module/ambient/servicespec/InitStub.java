package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.extension.skeleton.spi.ExPrerequisite;

/**
 * ## EmApp initializer
 * <p>
 * ### 1. Intro
 * <p>
 * This interface provide different mode to initialize application data that stored in `X_APP` & `X_SOURCE` table.
 * Here provide three ways to initialize application with configuration.
 * <p>
 * This service implementation called `At.initX` apis for the whole initialization workflow.
 * <p>
 * ### 2. Workflow
 * <p>
 * Please refer following table to check the workflow details:
 * <p>
 * |Phase|Related|Comments|
 * |:---|---|:---|
 * |1. EmApp|`X_APP`|Combine or Fetch application basic data.|
 * |2. Database|`X_SOURCE`|Re-calculate the database source configuration and convert to Database.|
 * |3. Extension|None|Call `AtPin.getInit()` to findRunning extension `Init` ( initializer ) and then call it.|
 * |4. Data Loading|None|Trigger data loading workflow to process OOB data.|
 * <p>
 * ### 3. API
 * <p>
 * For more details please refer each API document to check details.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface InitStub {
    // ----------------- Creation / Edition for EmApp ----------------------

    /**
     * 「Async」( Creation ) This api is for application initialization at first time.
     * <p>
     * Related Interface: {@link ExInit}
     *
     * @param appId {@link java.lang.String} The application primary key that stored in `KEY` field of `X_APP`.
     * @param data  {@link io.vertx.core.json.JsonObject} The data that will create application instance.
     * @return {@link io.vertx.core.Future}<{@link io.vertx.core.json.JsonObject}>
     */
    Future<JsonObject> initCreation(String appId, JsonObject data);


    /**
     * 「Async」( Edition ) This api is for application initialization at any time after 1st.
     * <p>
     * Related Interface: {@link ExInit}
     *
     * @param appName {@link java.lang.String} The application name that stored in `NAME` field of `X_APP`.
     * @return {@link io.vertx.core.Future}<{@link io.vertx.core.json.JsonObject}>
     */
    Future<JsonObject> initEdition(String appName);

    /**
     * 「Async」( Modeling Only ) This api is new for modeling initialization.
     * <p>
     * Related Interface: {@link ExInit}
     *
     * @param appName {@link java.lang.String} The application name that stored in `NAME` field of `X_APP`.
     * @return {@link io.vertx.core.Future}<{@link io.vertx.core.json.JsonObject}>
     */
    Future<JsonObject> initModeling(String appName);

    Future<JsonObject> initModeling(String appName, String outPath);

    /**
     * 「Async」Pre-Workflow before initialization when call this method.
     * <p>
     * Related Interface: {@link ExPrerequisite}
     *
     * @param appName {@link java.lang.String} The application name that stored in `NAME` field of `X_APP`.
     * @return {@link io.vertx.core.Future}<{@link io.vertx.core.json.JsonObject}>
     */
    Future<JsonObject> prerequisite(String appName);
}

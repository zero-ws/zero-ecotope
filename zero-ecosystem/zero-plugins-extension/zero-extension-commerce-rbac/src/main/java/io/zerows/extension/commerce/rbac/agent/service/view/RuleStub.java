package io.zerows.extension.commerce.rbac.agent.service.view;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.commerce.rbac.atom.ScOwner;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPath;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface RuleStub {
    /*
     * Process SPath
     *
     * 1) Read `runComponent` to web `HValve`
     * 2) Based join `HValue` to extract `KPermit` object
     * 3) Configure based join `KPermit`
     */
    Future<JsonObject> regionAsync(SPath paths);

    /*
     * Process SPath
     *
     * 1) Read all the related `SPocket` based join path
     * 2) The returned data structure
     * {
     *     "resource": {
     *         "v": {
     *             "mapping": {},
     *             "config": {},
     *             "value": ...
     *         },
     *         "h": {
     *             "mapping": {},
     *             "config": {},
     *             "value": ...
     *         },
     *         "q": {
     *             "mapping": {},
     *             "config": {},
     *             "value": ...
     *         }
     *     }
     * }
     * 3) The value of each node must be calculated based join `owner` and stored `resource`
     */
    Future<JsonObject> regionAsync(JsonObject pathData, ScOwner owner);

    /*
     * 内置处理 path -> code 集合，直接传入的 pathCodes 包含了当前 path 之下的子集合
     * viewData 则维持原始的 Body 经过处理之后的集合
     * {
     *     "resource1": {
     *         "xxx"
     *     }
     * }
     */
    Future<JsonObject> regionAsync(JsonObject condition, JsonObject viewData);
}

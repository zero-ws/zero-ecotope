package io.zerows.extension.runtime.skeleton.secure;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.web._60050Exception501NotSupport;

/**
 * 新接口，连同打结接口
 * 1. Twine 的父接口，对返回值可定义
 * 2. Twine 则直接继承自该接口，主要处理一对多需求
 * 3. 简化原始的 UserService 专用接口
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Tie<ID, T> {
    /*
     * JsonObject -> Tool
     */
    default Future<T> identAsync(final JsonObject condition) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    /*
     * ID -> Tool
     * Fetch the record
     */
    Future<T> identAsync(ID key);

    /*
     * ID -> Tool
     * Update the record
     */
    Future<T> identAsync(ID key, JsonObject updatedJ);
}

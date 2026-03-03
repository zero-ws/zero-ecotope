package io.zerows.extension.skeleton.spi;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._60050Exception501NotSupport;

import java.util.Collection;

/**
 * 新接口，连同打结接口
 * 1. Twine 的父接口，对返回值可定义
 * 2. Twine 则直接继承自该接口，主要处理一对多需求
 * 3. 简化原始的 UserService 专用接口
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface ScLink<ID, T> {
    /**
     * 根据条件查询关联信息
     *
     * @param condition DBE查询引擎语法
     * @return 查询结果
     */
    default Future<T> fetchAsync(final JsonObject condition) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    /**
     * 根据 ID 查询关联信息
     *
     * @param key ID 主键信息
     * @return 查询结果
     */
    Future<T> fetchAsync(ID key);

    /**
     * 根据 ID 更新关联信息
     *
     * @param key      ID 主键信息
     * @param updatedJ 更新信息，格式为 JsonObject，具体内容由实现类定义
     * @return 更新后的值
     */
    Future<T> saveAsync(ID key, JsonObject updatedJ);

    default Future<Boolean> removeAsync(final ID key) {
        return Future.succeededFuture(Boolean.TRUE);
    }

    /**
     * 新接口，连通接口
     * 1. 「找用户」被 ExUserEpic -> ExUser 通道调用，在 EmployeeService 中会调用 ExUser
     * 2. 「找关联」原始的 UserExtension，正向查找
     * 3. 「元数据」字段级的底层调用，执行字段转换
     * 4. 「找关联」原始的 Role / Group 多向查找
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface Extension<ID> extends ScLink<ID, JsonObject> {

        default Future<JsonArray> fetchAsync(final Collection<ID> keys) {
            return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
        }

        default Future<JsonObject> searchAsync(final String identifier, final JsonObject criteria) {
            return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
        }
    }
}

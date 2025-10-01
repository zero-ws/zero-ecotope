package io.zerows.extension.runtime.crud.uca.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.exception.web._80413Exception501NotImplement;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;

import java.util.function.Function;

/**
 * 核心行为库，生成函数专用，根据 IxMod 生成执行函数完成 JOIN 的复杂操作，主要针对
 * 特定的行为生成函数，最终函数作用于输入信息而实现完整的 CRUD 操作。
 *
 * @author lang : 2023-08-02
 */
@SuppressWarnings("unchecked")
public interface Operate<I, O> {
    // 删除 DELETE 语法
    static Operate<Object, Boolean> ofDelete() {
        return POOL.CCT_OPERATE.pick(SingleDelete::new, SingleDelete.class.getName());
    }

    // 查询 SELECT 语法
    static Operate<JsonObject, JsonArray> ofFetch() {
        return POOL.CCT_OPERATE.pick(OperateFetch::new, OperateFetch.class.getName());
    }

    // 查询：search，Qr语法
    static Operate<JsonObject, JsonObject> ofSearch() {
        return POOL.CCT_OPERATE.pick(OperateSearch::new, OperateSearch.class.getName());
    }

    // 聚集：COUNT
    static Operate<JsonObject, Long> ofCount() {
        return POOL.CCT_OPERATE.pick(OperateCount::new, OperateCount.class.getName());
    }

    /**
     * 操作生成函数，主要负责生成执行函数专用的方法，属于二阶函数，构造函数的参数为 {@link IxMod}，
     * 根据不同输入和输出构造对应的带有返回值的函数，此处的I是同步输入，而O最终会是异步返回，对应到
     * {@link Future} 类型中。
     *
     * @param in {@link IxMod} 模型
     *
     * @return {@link Function} 函数
     */
    default Function<I, Future<O>> annexFn(final IxMod in) {
        throw new _80413Exception501NotImplement();
    }
}

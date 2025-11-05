package io.zerows.extension.crud.uca;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.exception._80413Exception501NotImplement;

import java.util.function.Function;

/**
 * 核心行为库，生成函数专用，根据 IxMod 生成执行函数完成 JOIN 的复杂操作，主要针对
 * 特定的行为生成函数，最终函数作用于输入信息而实现完整的 CRUD 操作。
 *
 * @author lang : 2023-08-02
 */
@SuppressWarnings("all")
public interface Operate<I, O> {
    @Memory(Operate.class)
    Cc<String, Operate> CCT_SKELETON = Cc.openThread();

    // 删除 DELETE 语法
    static Operate<Object, Boolean> ofDelete() {
        return CCT_SKELETON.pick(OperateDelete::new, OperateDelete.class.getName());
    }

    // 查询 SELECT 语法
    static Operate<JsonObject, JsonArray> ofFetch() {
        return CCT_SKELETON.pick(OperateFetch::new, OperateFetch.class.getName());
    }

    // 查询：search，Qr语法
    static Operate<JsonObject, JsonObject> ofSearch() {
        return CCT_SKELETON.pick(OperateSearch::new, OperateSearch.class.getName());
    }

    // 聚集：COUNT
    static Operate<JsonObject, Long> ofCount() {
        return CCT_SKELETON.pick(OperateCount::new, OperateCount.class.getName());
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

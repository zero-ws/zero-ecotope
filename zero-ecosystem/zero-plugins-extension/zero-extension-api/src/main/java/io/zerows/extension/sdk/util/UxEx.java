package io.zerows.extension.sdk.util;

import io.r2mo.base.util.R2MO;
import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * 几个工具类的说明（统一归口处理）
 * <pre>
 *     1. 当前类：Utility X of Extension，扩展模块工具类（可访问所有扩展模块）
 *     2. {@link Ux} 核心框架编程专用工具类，追加了 Web 层和 DB 层的编程支持
 *     3. {@link Ut} - 基础工具类，零框架中的基础工具集
 *        {@link Fx} - Zero框架中的函数类，追加了异步编排等核心工具
 *        {@link Fn} - R2MO内部通用函数类
 * </pre>
 * 开发人员推荐使用：Ux / Ut / Fx 三个类进行日常开发，其中核心工具类还包括 {@link R2MO}，总体上比太多的 *Util 的访问要轻松很多
 *
 * @author lang : 2025-12-15
 */
public class UxEx {
    /*
     * 字典逻辑
     * 1）读取多个字典
     * 2）字典消费专用（同步/异步）
     */
    public static Future<ConcurrentMap<String, JsonArray>> dictData(final String sigma, final Set<String> types) {
        return UxExDatum.dictData(sigma, types);
    }

    public static void dictGet(final JsonArray source, final String code, final Consumer<String> consumer) {
        UxExDatum.dictGet(source, code, consumer);
    }

    public static String dictGet(final JsonArray source, final String code) {
        return UxExDatum.dictGet(source, code);
    }
}

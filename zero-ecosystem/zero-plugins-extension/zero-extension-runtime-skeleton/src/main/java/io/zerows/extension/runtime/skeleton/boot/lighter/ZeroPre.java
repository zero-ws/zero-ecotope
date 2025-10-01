package io.zerows.extension.runtime.skeleton.boot.lighter;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.core.database.jooq.JooqInfix;
import io.zerows.epoch.common.shared.boot.KEnvironment;
import io.zerows.core.web.cache.shared.MapInfix;
import io.zerows.plugins.office.excel.ExcelInfix;
import io.zerows.specification.configuration.boot.HMature;

/**
 * @author lang : 2023-06-10
 */
public class ZeroPre implements HMature.HPre<Vertx> {
    /**
     * 「Vertx启动后」（同步）扩展流程一
     * <p>
     * 流程一：Vertx原生插件初始化，带Vertx的专用启动流程，在Vertx实例启动之后启动
     * <pre><code>
     *         1. SharedMap提前初始化（Infix架构下所有组件的特殊组件预启动流程）
     *         2. 其他Native插件初始化
     *     </code></pre>
     * </p>
     *
     * @param vertx   Vertx实例
     * @param options 启动配置
     */
    @Override
    public Boolean beforeStart(final Vertx vertx, final JsonObject options) {
        /*
         * MapInfix作为初始化容器过程中第一个必须要使用的组件，只要系统重启用了它，那么就必须在
         * 容器启动之前执行初始化，特别是针对缓存数据会在实现过程中存在，此缓存数据用在如下位置
         * 1. 扩展模块配置中
         * 2. 扩展模块初始化中
         * 所有 Infix 不惧怕多次重复加载，本身具有幂等性操作。
         */
        MapInfix.init(vertx);
        /*
         * 内置插件顺序先 Jooq 再 Excel
         */
        JooqInfix.init(vertx);
        ExcelInfix.init(vertx);

        // 环境变量准备执行
        KEnvironment.initialize();

        return Boolean.TRUE;
    }
}

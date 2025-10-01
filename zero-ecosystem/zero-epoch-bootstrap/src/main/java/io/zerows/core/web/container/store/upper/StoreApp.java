package io.zerows.core.web.container.store.upper;

import io.zerows.epoch.constant.VString;
import io.zerows.common.app.KDS;
import io.zerows.core.web.container.store.under.StoreRouter;
import io.zerows.core.web.container.store.under.StoreServer;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.vital.HOI;

/**
 * 应用对接器，底层会直接针对应用执行相关初始化，解析应用专用配置 vertx-app.yml，新配置
 * <pre><code>
 *     1. 所属：{@link HArk} 数据结构
 *             - {@link HApp} 应用 x 1
 *             - {@link HOI} 租户 x 1
 *             - {@link KDS} 数据库 x N
 *               - PRIMARY：  主数据库
 *               - HISTORY：  历史数据库
 *               - WORKFLOW： 工作流数据库
 *               - DYNAMIC：  动态数据库，底层对接 X_SOURCE 表结构中定义的数据库
 *               - EXTENSION：（保留）自定义扩展数据库
 *     2. 关联下层：{@link StoreServer} 集合 x N
 *                {@link StoreRouter} 集合 x N ( context + router )
 * </code></pre>
 *
 * @author lang : 2024-05-03
 */
public interface StoreApp {
    // 关联应用基础信息
    HArk runAt();

    // 运行配置中的 Context 上下文环境
    default String context() {
        return VString.SLASH;
    }
}

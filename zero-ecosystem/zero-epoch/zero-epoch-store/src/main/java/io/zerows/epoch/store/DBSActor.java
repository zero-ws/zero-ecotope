package io.zerows.epoch.store;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.vertx.dbe.DBContext;
import io.r2mo.vertx.jooq.DBEx;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.ConfigDS;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import javax.sql.DataSource;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "DATABASE", sequence = -1017)
@Slf4j
public class DBSActor extends AbstractHActor {

    private static final Cc<String, DBContext> CC_CONTEXT = Cc.open();

    // -------------- 此处返回的是动态的 ---------------
    public static DBS ofDBS(final Database database) {
        final String key = String.valueOf(database.hashCode());
        return DBMany.of().put(key, database);
    }

    public static DataSource ofDataSource(final Database database) {
        return ofDBS(database).getDs();
    }

    // -------------- 下边全是返回默认的 ---------------
    public static DBS ofDBS() {
        return DBMany.of().get();
    }

    public static DBS ofDBS(final String name) {
        return DBMany.of().get(name);
    }

    public static Database ofDatabase() {
        return ofDBS().getDatabase();
    }

    public static String ofDatabaseCatalog() {
        return ofDBS().getDatabase().getInstance();
    }

    public static DataSource ofDataSource() {
        return ofDBS().getDs();
    }

    private static DBContext context() {
        // TODO: DBE-EXTENSION / 未来可扩展更多数据库类型
        //       目前仅支持 JooqDatabase 类型
        return CC_CONTEXT.pick(
            () -> HPI.findOne(DBContext.class, DBContext.DEFAULT_CONTEXT_SPID),
            DBContext.DEFAULT_CONTEXT_SPID);
    }

    public static <T> T ofContext(final Database database) {
        return context().context(database);
    }

    // -------------- Legacy模式下返回 Jooq 的操作 ---------------

    public static <T> T ofContext() {
        return ofContext(ofDatabase());
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (!(config instanceof final ConfigDS configDs)) {
            this.vLog("[ DBS ] DBSActor 因为配置异常跳过：{}", config);
            throw new _501NotSupportException("[ ZERO ] 数据库配置类型错误！");
        }

        this.vLog("[ DBS ] DBSActor 初始化数据源！");
        this.configure(configDs, vertxRef);

        final DBSAddOn addOn = DBSAddOn.of(vertxRef, config);

        return Future.succeededFuture(Boolean.TRUE);
    }

    public void configure(final ConfigDS configDs, final Vertx vertx) {
        try {
            if (configDs.dynamic()) {
                configDs.config().forEach((key, config) -> {
                    final Database database = config.ref();
                    final DBS dbs = DBMany.of().put(key, database);
                    this.vLog("[ DBS ] 数据库 key = {} 初始化完成，类型：{}", key, database.getType());
                    this.configure(dbs, vertx);
                });
            } else {
                // 只有 Master 库
                final DBS dbs = DBMany.of().put("master", configDs.ref());
                this.configure(dbs, vertx);
            }
        } catch (final Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 此方法的核心逻辑
     * <pre>
     *     1. 根据传入的 DBS 实例，构造向量表
     *        DBS hashCode -> Vertx 引用
     *        - {@link DBEx} 底层调用时只要拿到 DBS 就可以直接提取 Vertx 实例，用于初始化异步 Vertx 专用
     *     2. 根据传入的 DBS 实例，构造 JOOQ 配置
     *        - {@link DBEx} 底层调用可直接提取 JOOQ 配置 {@link DSLContext}
     * </pre>
     * 使用流程
     * <pre>
     *     1. 为了兼容 Legacy 模式，底层调用直接用 {@link DB}/{@see DBJooq}/{@see DBJoin} 做归口统一
     *        - 其中 {@link DB} 适用于所有模式
     *        - DBJooq / DBJoin 最终会封装成 package 包域可见，移除旧版的所有 Jooq
     *        - 异步模式调用 {@link DBEx} -> 内部组合调用 {@link DBE}
     *          - 没有引用 r2mo-vertx-jooq 时直接调用 {@link DBE}
     *          - 有引用 r2mo-vertx-jooq 时调用 {@link DBEx} 来初始化异步 Jooq 操作
     *     2. 新版编程也可直接使用 {@link DB} 来调用，只是不走 DBJooq / DBJoin 层
     *        - 新版也可直接使用 {@link DBEx} 初始化异步 Jooq 操作
     *     3. 内置流程缓存有
     *        - Vertx + DBS + daoCls / 生成基于实体的缓存，每个实体都会得到一个 {@link DBEx} 实例
     *     4. 新流程中 {@link DB} 可直接访问 {@link DBSManager} 来构造 {@link DBEx} 实例
     * </pre>
     *
     * @param dbs   数据源
     *              - 可直接提取 {@link Database} 实例，由于此处有扩展，类型为 {@link JooqDatabase}
     *              - 可直接提取 {@link DataSource} 实例
     * @param vertx Vertx 引用（异步模式必须，同步模式可忽略）
     *              - 同步模式的实现在 r2mo-dbe-jooq 模块中，它不依赖 {@link Vertx} 实例
     *              - 异步模式的实现在 r2mo-vertx-jooq 模块中，它依赖 {@link Vertx} 实例
     */
    private void configure(final DBS dbs, final Vertx vertx) {
        this.vLog("[ DBS ] 初始化 DBEx 数据库引擎：`{}`，懒加载。", dbs.getDatabase().getInstance());
        context().configure(dbs, vertx);
    }
}

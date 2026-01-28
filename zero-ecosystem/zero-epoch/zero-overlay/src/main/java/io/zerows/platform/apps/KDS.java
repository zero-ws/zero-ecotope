package io.zerows.platform.apps;

import io.r2mo.base.dbe.DBMany;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.common.MultiKeyMap;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 新版数据源 KDS 全称是 K-Data Source，K 在此处表示维度，意味着多数据源整体管理模型，它替换掉原来的
 * <pre>
 *     1. KDatabase, Database
 *     2. KDS<T>
 * </pre>
 * 直接针对数据源进行操作可以帮助管理所有应用所需的数据源核心模型，也方便在运行时动态切换数据库，目前的架构中，应用本身和数据源
 * 是绑定的，简单说就是每个应用会包含 {@link HApp} 和 {@link HArk} 两个核心模型，而数据源属于其中一部分，默认数据源是绑定
 * 的效果，但可以通过 KDS 动态切换数据源，此处的切换就直接方法 {@link DBMany} 按名称查找，如
 * <pre>
 *     1. master,               主数据库
 *     2. master-history,       主数据库的历史库
 *     3. master-workflow,      主数据库的工作流库
 * </pre>
 * 数据源的不同类型已经不重要，只是需要知道其注册流程即可
 * <pre>
 *     1. 静态注册：直接在 vertx.yml 启动配置文件中定义数据源信息，有多少就可以取多少
 *     2. 动态注册：通过访问 X_SOURCE 来实现注册流程，如果数据表中的 X_SOURCE 没有定义的数据源，则直接切换到静态注册的
 *                默认数据源（保证幂等性）
 * </pre>
 *
 * @author lang : 2025-10-22
 */
public class KDS {

    public static final String DEFAULT_DBS_HISTORY = "master-history";
    public static final String DEFAULT_DBS_WORKFLOW = "master-workflow";

    private static final DBMany DB_MANY = DBMany.of();
    /**
     * 多键哈希表，此处数据源的引用实例包括
     * <pre>
     *     1. appName -> KDS Instance
     *     2. appId   -> KDS Instance
     *     3. appKey  -> KDS Instance
     *     4. sigma   -> KDS Instance
     * </pre>
     * 此处的 KDS Instance 是当前应用的 KDS / name = master，也是默认数据源实例
     */
    private static final MultiKeyMap<KDS> STORED = new MultiKeyMap<>();

    /**
     * 当前应用的所有数据源实例集合，有多少算多少，最终的结构如
     * <pre>
     *     appName     --->       KDS       name-01 = dbs-01
     *     appId                            name-02 = dbs-02
     *     appKey                           name-03 = dbs-03
     *     sigma                            name-04 = dbs-04
     *     id                               .         ...
     *                                      .         ...
     *                                      .         ...
     *                                      name-NN = dbs-NN
     * </pre>
     * 每个应用只会拥有一个 KDS 实例，之中包含多个 DBS 实例，每个 DBS 实例对应一个具体的数据源配置
     * {@link DBS} 中可获取的
     * <pre>
     *     1. {@link Database}      数据库实例
     *     2. {@link DataSource}    数据源实例
     * </pre>
     * 所以此处的 dbs 是非 static 静态的，而是实例变量，而真正的 DBS 对象会存储在 {@link DBMany} 中进行统一管理，所以不用
     * 担心此处是否可以查找到对应的数据源实例的相关信息。这里的变量只是引用而非对象本身，真正的对象会在启动过程中按 name 存储在
     * {@link DBMany} 中。
     */
    private final ConcurrentMap<String, DBS> dbs = new ConcurrentHashMap<>();

    private final String appId;

    /**
     * KDS 是一个软容器，理论上讲它一旦进入标准生命周期就会和应用形成 1:1 的绑定，因此它会经历如下步骤
     * <pre>
     *     1. 应用启动阶段：半创建 KDS
     *        启动过程中会捕捉 Z_APP 环境变量来创建 KDS 实例，此时的 KDS 只是半成品，键中不包含其他如 appId / appKey / sigma
     *        等等，只包含 appName 信息
     *
     *     2. 应用预启动：半创建 KDS
     *        会调用 {@link #of(String)} 的方法，此方法也仅仅只是 KDS 的半成品，而且很有可能和之前启动阶段创建的 KDS 不是同一
     *        个实例，但是：由于 {@link DBS} 是存储在 {@link DBMany} 中，所以不会影响数据源的使用。
     *        *：预启动的主要目的是初始化环境上下文，且用于不启用 zero-extension-ambient 模块的情况下使用，这种场景中 KDS 永远
     *        将会是一个半成品，因为没有扩展模块来进行完善它，但这种场景并不影响数据源的访问，毕竟最终数据源的访问可直接依靠 name
     *        来进行查找，只要知道 name 就可以访问到对应的 {@link DBS} 实例。
     *
     *     3. 应用正式启动：完全创建 KDS
     *        直接调用 {@link #registry(HArk)} 方法重新初始化 KDS，此时的 KDS 是最终版本，并且之前创建的 KDS 会因为
     *        name / appId / appKey / sigma 四个键值相同而被覆盖掉，最终这些所有键值都可以访问到对应的 KDS 并且根据名称访问
     *        到 {@link DBMany} 中所管理的 {@link DBS} 实例。
     * </pre>
     *
     * @param appId 应用ID
     */
    private KDS(final String appId) {
        this.appId = appId;
    }

    // ------ KDS 静态API
    public static KDS of() {
        final String appName = ENV.of().get(EnvironmentVariable.Z_APP);
        return of(appName);
    }

    public static KDS of(final String key) {
        KDS instance = STORED.getOr(key);
        if (Objects.isNull(instance)) {
            instance = new KDS(key);
            STORED.put(key, instance);
        }
        return instance;
    }

    public static Database findCurrent() {
        return findDatabase(DBMany.DEFAULT_DBS);
    }

    public static Database findHistory() {
        return findDatabase(DEFAULT_DBS_HISTORY);
    }

    public static Database findCamunda() {
        return findDatabase(DEFAULT_DBS_WORKFLOW);
    }

    /**
     * 🔍 <b>findDatabase(name, appOr)</b> — 在指定应用上下文中按名称查找 {@link Database}。
     * 🧩 适用场景
     * <pre>
     *   🏷️ 多应用/多租户环境：需要显式从某个应用（appOr）的 KDS 中取某个命名数据源
     *   🎛️ 跨上下文访问：调用方已知“应用标识 + 数据源名”，希望精确获取对应 Database
     * </pre>
     * <p>
     * 🧠 行为说明
     * <pre>
     *   🔑 先通过 of(String) 获取目标应用的 KDS 实例，再以 name 索引其 DBS 并取出 Database
     *   ⚙️ 查找复杂度近似 O(1)，底层依赖 KDS 的并发映射与 DBMany 的集中管理
     * </pre>
     * <p>
     * ⚠️ 注意/边界
     * <pre>
     *   ❗ 若目标应用未注册该数据源名，kds.findRunning(name) 可能返回 null，进而触发 NullPointerException
     *   🧯 调用方可在外层加空值校验或封装成可选返回（如增加 Optional&lt;Database&gt; 重载）
     * </pre>
     *
     * @param name  数据源名称（例如：master、master-history、master-workflow）
     * @param appOr 目标应用标识（可为 appName / appId / appKey / sigma 中任意一种键）
     * @return 目标应用中名为 name 的 {@link Database} 实例
     */
    public static Database findDatabase(final String name, final String appOr) {
        final KDS kds = KDS.of(appOr);
        return kds.findRunning(name).getDatabase();
    }

    /**
     * 🔍 <b>findDatabase(name)</b> — 在“当前应用上下文”中按名称查找 {@link Database}。
     * 🧩 适用场景
     * <pre>
     *   ⚡ 单应用场景或已由环境变量（如 Z_APP）确定当前应用
     *   🧾 需要从本应用 KDS 中获取命名数据源（如默认库、历史库、工作流库）
     * </pre>
     * <p>
     * 🧠 行为说明
     * <pre>
     *   🧷 通过 of() 基于环境上下文得到当前应用的 KDS，再以 name 获取 DBS → Database
     *   🏁 常与快捷方法 findCurrent() / findHistory() / findCamunda() 配合使用
     * </pre>
     * <p>
     * ⚠️ 注意/边界
     * <pre>
     *   ❗ 若当前应用未注册该数据源名，kds.findRunning(name) 可能为 null，会导致 NullPointerException
     *   🧯 建议调用前确保目标数据源已通过 registry(...) 完成注册，或在外层做空值保护
     * </pre>
     *
     * @param name 数据源名称（例如：master、master-history、master-workflow）
     * @return 当前应用中名为 name 的 {@link Database} 实例
     */
    public static Database findDatabase(final String name) {
        final KDS kds = KDS.of();
        return kds.findRunning(name).getDatabase();
    }


    // ------ KDS 操作用 API

    /**
     * 注册数据源实例到当前 KDS 中
     *
     * @param dbs 数据源实例
     * @return 当前 KDS 实例
     */
    public KDS registry(final DBS dbs) {
        final Database database = dbs.getDatabase();
        final String name = database.name();
        // 1. 先做一级注册同步
        final DBS foundOr = DB_MANY.registry(name, dbs);
        // 2. 注册完成后将注册结果同步到当前 KDS 中
        this.dbs.put(name, foundOr);
        return this;
    }

    public KDS registry(final HArk ark) {

        return null;
    }

    public DBS findRunning(final String name) {
        DBS found = this.dbs.getOrDefault(name, null);
        if (Objects.isNull(found)) {
            found = DBMany.of().get(name);
        }
        if (Objects.nonNull(found)) {
            this.dbs.put(name, found);
        }
        return found;
    }


    /**
     * 查找当前 appId 对应的数据库
     *
     * @return 数据库实例
     */
    public DBS findRunning() {
        return this.findRunning(this.appId);
    }
    // ------ KDS 内部 API

    private String app() {
        return this.appId;
    }
}

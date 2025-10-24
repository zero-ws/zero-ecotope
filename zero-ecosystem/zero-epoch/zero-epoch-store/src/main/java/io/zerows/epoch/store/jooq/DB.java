package io.zerows.epoch.store.jooq;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.typed.common.Kv;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.store.DBSActor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DB {
    // region 单表访问器

    /**
     * 便捷工厂：仅指定 DAO 类，创建/复用 {@link ADB} 实例。🚀
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🧰 快速起步：无需关心数据源与映射，先拿到句柄再说。</li>
     *   <li>♻️ 延迟装配：数据源/映射在更高层由框架或运行时注入。</li>
     *   <li>🧪 测试用例：最少依赖，便于桩替换/Mock。</li>
     * </ul>
     *
     * @param clazz DAO 类（通常为 jOOQ 生成的 *Dao 类）
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final Class<?> clazz) {
        return ADB.of(clazz, (String) null, DBSActor.ofDBS());
    }

    /**
     * 便捷工厂：<b>默认数据源</b> + <b>指定映射文件</b>，创建/复用 {@link ADB} 实例。📦🗺️
     *
     * <p><b>行为</b>：</p>
     * <ul>
     *   <li>📡 数据源：通过 {@link DBSActor#ofDBS()} 自动解析默认数据源。</li>
     *   <li>🗺️ 字段映射：由 {@code filename} 解析构建 {@code R2Vector}。</li>
     * </ul>
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🔧 约定优于配置：项目有统一默认数据源。</li>
     *   <li>🧩 需要字段别名/驼峰/多语言等映射策略。</li>
     * </ul>
     *
     * @param clazz    DAO 类（通常为 jOOQ 生成的 *Dao 类）
     * @param filename 映射文件名（用于解析字段映射）
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final Class<?> clazz, final String filename) {
        return ADB.of(clazz, filename, DBSActor.ofDBS());
    }

    /**
     * 便捷工厂：<b>指定数据源</b>，创建/复用 {@link ADB} 实例（无映射文件）。⚙️🔌
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🗃️ 多数据源：读写分离、租户库、分库分表等需要显式选择数据源。</li>
     *   <li>📏 直连模式：不需要字段映射，沿用数据库原始命名。</li>
     * </ul>
     *
     * @param clazz DAO 类（通常为 jOOQ 生成的 *Dao 类）
     * @param dbs   数据源描述对象 {@link DBS}
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final Class<?> clazz, final DBS dbs) {
        return ADB.of(clazz, (String) null, dbs);
    }

    /**
     * 便捷工厂：<b>指定数据源</b> + <b>指定映射文件</b>，创建/复用 {@link ADB} 实例。🧩🎯
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🏭 企业生产：明确的数据源治理 + 统一字段映射规范。</li>
     *   <li>🔒 合规项目：表/列与领域模型差异大，需要稳定映射层。</li>
     *   <li>🧷 迁移过渡：老库命名不一致，通过映射平滑对接新模型。</li>
     * </ul>
     *
     * @param clazz    DAO 类（通常为 jOOQ 生成的 *Dao 类）
     * @param filename 映射文件名（用于解析字段映射）
     * @param dbs      数据源描述对象 {@link DBS}
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final Class<?> clazz, final String filename, final DBS dbs) {
        return ADB.of(clazz, filename, dbs);
    }

    /**
     * 便捷工厂：基于 {@link MDConnect} 一站式创建/复用 {@link ADB} 实例（默认数据源）。🧩⚡
     *
     * <p><b>行为</b>：</p>
     * <ul>
     *   <li>📦 从 {@code connect} 提取 DAO 类：{@code connect.getDao()}。</li>
     *   <li>🗺️ 从 {@code connect} 提取映射文件：{@code connect.getPojoFile()}（用于构建 {@code R2Vector}）。</li>
     *   <li>🔌 数据源采用默认：{@link DBSActor#ofDBS()}。</li>
     * </ul>
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🧰 元数据驱动：配置/元数据对象一次性携带 DAO 与映射信息。</li>
     *   <li>🚀 快速装配：无需显式传入数据源，遵循项目默认数据源约定。</li>
     *   <li>🧪 工具脚本/CLI：最少参数，快速拿到可用的异步 DB 句柄。</li>
     * </ul>
     *
     * @param connect 封装 DAO 类与映射文件的连接描述（如：{@code connect.getDao()}、{@code connect.getPojoFile()}）
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final MDConnect connect) {
        return ADB.of(connect.getDao(), connect.getPojoFile(), DBSActor.ofDBS());
    }

    /**
     * 便捷工厂：基于 {@link MDConnect} 与<b>指定数据源</b>创建/复用 {@link ADB} 实例。🧩🎯
     *
     * <p><b>行为</b>：</p>
     * <ul>
     *   <li>📦 从 {@code connect} 提取 DAO 类：{@code connect.getDao()}。</li>
     *   <li>🗺️ 从 {@code connect} 提取映射文件：{@code connect.getPojoFile()}（用于构建 {@code R2Vector}）。</li>
     *   <li>🧭 使用传入的 {@link DBS} 作为目标数据源，适配多库/租户等场景。</li>
     * </ul>
     *
     * <p><b>适用场景</b>：</p>
     * <ul>
     *   <li>🏭 企业生产：明确的数据源治理（主从、读写分离、租户库）。</li>
     *   <li>🔒 合规/灰度：按环境或租户动态切换数据源，同时复用统一映射策略。</li>
     * </ul>
     *
     * @param connect 封装 DAO 类与映射文件的连接描述（如：{@code connect.getDao()}、{@code connect.getPojoFile()}）
     * @param dbs     目标数据源描述对象（支持多数据源/租户切换）🔌
     *
     * @return 复用或新建的 {@link ADB} 实例
     */
    public static ADB on(final MDConnect connect, final DBS dbs) {
        return ADB.of(connect.getDao(), connect.getPojoFile(), dbs);
    }

    // endregion

    // region 多表访问器（双表）
    public static ADJ on(final DBRef ref) {
        return ADJ.of(DBSActor.ofDBS()).configure(ref);
    }

    public static ADJ on(final Join meta) {
        return on(meta, null, null, DBSActor.ofDBS());
    }

    public static ADJ on(final Join meta, final String leftPojo) {
        return on(meta, leftPojo, null, DBSActor.ofDBS());
    }

    public static ADJ on(final Join meta, final String leftPojo, final String rightPojo) {
        return on(meta, leftPojo, rightPojo, DBSActor.ofDBS());
    }

    public static ADJ on(final Join meta, final Kv<String, String> vectorPojo) {
        return on(meta, vectorPojo, DBSActor.ofDBS());
    }

    // ----- DBS 分割线
    public static ADJ on(final DBRef ref, final DBS dbs) {
        return ADJ.of(dbs).configure(ref);
    }

    public static ADJ on(final Join meta, final DBS dbs) {
        return on(meta, null, null, dbs);
    }

    public static ADJ on(final Join meta, final String leftPojo, final DBS dbs) {
        return on(meta, leftPojo, null, dbs);
    }

    public static ADJ on(final Join meta, final String leftPojo, final String rightPojo, final DBS dbs) {
        return on(meta, Kv.create(leftPojo, rightPojo), dbs);
    }

    public static ADJ on(final Join meta, final Kv<String, String> vectorPojo, final DBS dbs) {
        /*
         * FIX-DBE: 内部切换初始化流程，此处由于没有表名，无法构造完整的 DBRef，所以此处形成二阶段流程
         * - 直接传入 DBRef，这种场景 DBRef 是完整的
         * - 重新构造 DBRef，依赖 Join 对象，这种场景下构造的 DBRef 要在 configure 中去完善
         */
        return ADJ.of(dbs).configure(meta, vectorPojo);
    }
    // endregion
}

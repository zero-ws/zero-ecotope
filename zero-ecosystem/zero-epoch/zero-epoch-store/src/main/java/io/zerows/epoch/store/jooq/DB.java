package io.zerows.epoch.store.jooq;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.ADJ;
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
        return ADB.of(clazz, null, DBSActor.ofDBS());
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
        return ADB.of(clazz, null, dbs);
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

    /**
     * ✨ <b>on(DBRef)</b> — 基于已构造好的 {@link DBRef} 创建双表访问器（使用默认 {@link DBS} 上下文）。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>✅ 已从其它工厂/装配流程产出完整 {@link DBRef}（含两表、别名、连接键）。</li>
     *   <li>⚡ 想在“默认数据源/默认事务”下立即发起 JOIN 查询、更新、聚合统计等。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>🛠️ 直接复用标准化的 DB 引用定义，减少重复拼装。</li>
     *   <li>🧼 API 简洁：不关心上下文选择，默认即开即用。</li>
     * </ul>
     *
     * ⚠️ <b>注意</b>：
     * <ul>
     *   <li>🔀 若需指定数据源/库/事务边界，请改用 {@link #on(DBRef, DBS)}。</li>
     *   <li>🧱 依赖默认 {@link DBS} 的环境一致性，测试/生产切换需留意。</li>
     * </ul>
     *
     * @param ref 预先构造好的双表引用（含两表与连接信息）
     *
     * @return 使用默认 {@link DBS} 上下文的 {@link ADJ} 访问器
     */
    public static ADJ on(final DBRef ref) {
        return ADJ.of(ref, DBSActor.ofDBS());
    }

    /**
     * 🧭 <b>on(DBRef, DBS)</b> — 基于已构造好的 {@link DBRef} 创建双表访问器（绑定指定 {@link DBS} 上下文）。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>🏷️ 多库/多租户/读写分离环境下，需明确绑定上下文执行一次性或阶段性操作。</li>
     *   <li>🧾 需要更清晰的审计与事务边界可观测性。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>🎛️ 上下文可注入：支持在调用栈/拦截器中统一分发 {@code dbs}。</li>
     *   <li>🔒 与事务管理器/路由策略自然对齐。</li>
     * </ul>
     *
     * ⚠️ <b>注意</b>：
     * <ul>
     *   <li>📌 {@code dbs} 的生命周期与传播策略需与调用方一致，否则可能产生越权或跨库风险。</li>
     * </ul>
     *
     * @param ref 已包含两表与连接信息的引用
     * @param dbs 目标数据源/库/事务上下文
     *
     * @return 绑定指定 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final DBRef ref, final DBS dbs) {
        return ADJ.of(ref, dbs);
    }

    /**
     * 🔗 <b>on(meta, leftKey)</b> — 仅给出左键的 JOIN 入口（默认 {@link DBS}，右键采用默认键 {@link KName#KEY}）。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>🪪 左右表连接列名一致，或右表遵循系统默认主键/连接键。</li>
     *   <li>⚡ 需要以最少参数快速搭建双表引用并立刻操作。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>✂️ 参数极简，降低接入复杂度。</li>
     *   <li>🧭 默认值兜底，减少命名不一致带来的干扰。</li>
     * </ul>
     *
     * 🧱 <b>约束/规则</b>：
     * <ul>
     *   <li>🧾 {@code meta} 必须且只能包含两项（实体/DAO类 → 别名）。</li>
     *   <li>🗝️ {@code leftKey} 为空/空白 → 退回 {@link KName#KEY}，右键始终使用 {@link KName#KEY}。</li>
     * </ul>
     *
     * ⚠️ <b>注意</b>：
     * <ul>
     *   <li>🔧 若两表连接列名不同，请使用显式左右键或向量重载。</li>
     * </ul>
     *
     * @param meta    双表元信息：Class -&gt; alias（恰好两项）
     * @param leftKey 左表连接键（如 "u.id" 或 "id"）
     *
     * @return 基于默认 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final String leftKey) {
        return on(meta, leftKey, null, DBSActor.ofDBS());
    }

    /**
     * 🔗 <b>on(meta, leftKey, rightKey)</b> — 显式给出左右连接键的 JOIN 入口（默认 {@link DBS}）。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>🔀 左右表连接列名不同（例如主外键命名不一、历史库字段差异）。</li>
     *   <li>🧭 需要“明确对齐左右键”来避免与默认键冲突。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>🎯 消除歧义：显式约束左右键，提升可读性与稳定性。</li>
     *   <li>🧩 易于调试与审计：变更影响面可控。</li>
     * </ul>
     *
     * 🧱 <b>约束/规则</b>：
     * <ul>
     *   <li>🧾 {@code meta} 必须且只能包含两项（实体/DAO类 → 别名）。</li>
     *   <li>🗝️ {@code leftKey/rightKey} 任一为空/空白 → 自动回退 {@link KName#KEY}。</li>
     * </ul>
     *
     * @param meta     双表元信息：Class -&gt; alias（恰好两项）
     * @param leftKey  左表连接键
     * @param rightKey 右表连接键
     *
     * @return 基于默认 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final String leftKey, final String rightKey) {
        return on(meta, leftKey, rightKey, DBSActor.ofDBS());
    }

    /**
     * 🌐 <b>on(meta, leftKey, dbs)</b> — 指定 {@link DBS} + 仅左键（右键默认）的 JOIN 入口。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>🗝️ 右键沿用默认键（如主键 id），但需在特定 {@link DBS} 上下文中执行。</li>
     *   <li>🏷️ 多库/多租户场景下的轻量化连接声明。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>🎛️ 将“连接定义”与“上下文选择”解耦，按需绑定。</li>
     *   <li>⚙️ 适合策略化路由（读写分离/灰度流量）。</li>
     * </ul>
     *
     * 🧱 <b>约束/规则</b>：
     * <ul>
     *   <li>🧾 {@code meta} 恰好两项；{@code leftKey} 为空/空白 → {@link KName#KEY}；右键默认 {@link KName#KEY}。</li>
     * </ul>
     *
     * @param meta    双表元信息：Class -&gt; alias（恰好两项）
     * @param leftKey 左表连接键
     * @param dbs     指定的数据源/库/事务上下文
     *
     * @return 绑定指定 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final String leftKey, final DBS dbs) {
        return on(meta, leftKey, null, dbs);
    }

    /**
     * 🌐 <b>on(meta, leftKey, rightKey, dbs)</b> — 指定 {@link DBS} + 显式左右键的 JOIN 入口。
     *
     * 🔧 <b>实现要点</b>：
     * <ul>
     *   <li>🧰 对空/空白键自动回退为 {@link KName#KEY}，再封装为“键向量”统一传递。</li>
     * </ul>
     *
     * 💡 <b>使用建议</b>：
     * <ul>
     *   <li>🏢 多租户、灰度或读写分离时，显式传入 {@code dbs} 以提升可观测性与安全性。</li>
     *   <li>📊 与审计链路（链路ID、租户ID、事务ID）组合记录更清晰。</li>
     * </ul>
     *
     * 🧱 <b>约束/规则</b>：
     * <ul>
     *   <li>🧾 {@code meta} 必须且只能包含两项（实体/DAO类 → 别名）。</li>
     * </ul>
     *
     * @param meta     双表元信息：Class -&gt; alias（恰好两项）
     * @param leftKey  左表连接键（为空→{@link KName#KEY}）
     * @param rightKey 右表连接键（为空→{@link KName#KEY}）
     * @param dbs      指定的数据源/库/事务上下文
     *
     * @return 绑定指定 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final String leftKey, final String rightKey, final DBS dbs) {
        final String left = StrUtil.isEmpty(leftKey) ? KName.KEY : leftKey;
        final String right = StrUtil.isEmpty(rightKey) ? KName.KEY : rightKey;
        return on(meta, Kv.create(left, right), dbs);
    }

    /**
     * 🧩 <b>on(meta, vector)</b> — 直接传入“连接向量”的 JOIN 入口（默认 {@link DBS}）。
     *
     * 🧩 <b>适用场景</b>：
     * <ul>
     *   <li>🧱 调用方已将左右键封装为 {@code Kv&lt;String,String&gt;}（例如“左→右”的一对映射）。</li>
     *   <li>🧠 希望以“组合参数对象”提升描述性、可复用性与团队协作规范。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>📦 连接定义集中化：便于缓存/下发/复用。</li>
     *   <li>🧭 可与字典/元数据系统对齐（字段别名/标准化对照）。</li>
     * </ul>
     *
     * 🧱 <b>约束/规则</b>：
     * <ul>
     *   <li>🧾 {@code meta} 恰好两项（实体/DAO类 → 别名）。</li>
     * </ul>
     *
     * @param meta    双表元信息：Class -&gt; alias（恰好两项）
     * @param waitFor 连接向量（左键 → 右键）
     *
     * @return 基于默认 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final Kv<String, String> waitFor) {
        return on(meta, waitFor, DBSActor.ofDBS());
    }

    /**
     * 🧩 <b>on(meta, vector, dbs)</b> — 传入“连接向量”并绑定指定 {@link DBS} 的 JOIN 入口。
     *
     * 🧪 <b>参数校验</b>：
     * <ul>
     *   <li>🧯 {@code meta == null} 或 {@code meta.size() != 2} → 抛出 {@link _501NotSupportException}。</li>
     * </ul>
     *
     * 🌟 <b>优势/特征</b>：
     * <ul>
     *   <li>🧩 将连接关系与上下文解耦，适合策略化路由、演练与回放。</li>
     *   <li>🧱 便于以“配置/元数据”形式沉淀到平台层。</li>
     * </ul>
     *
     * ⚠️ <b>注意</b>：
     * <ul>
     *   <li>📌 向量键名建议带别名前缀（如 {@code u.id}），避免字段重名冲突。</li>
     * </ul>
     *
     * @param meta    双表元信息：Class -&gt; alias（恰好两项）
     * @param waitFor 连接向量（左键 → 右键）
     * @param dbs     指定的数据源/库/事务上下文
     *
     * @return 绑定指定 {@link DBS} 的 {@link ADJ} 访问器
     */
    public static ADJ on(final Join meta, final Kv<String, String> waitFor, final DBS dbs) {
        return ADJ.of(meta, waitFor, dbs);
    }
    // endregion


    // ------------------------ 下边是 Join 部分 -------------------------

    public static ADJ join() {
        return null;
    }
}

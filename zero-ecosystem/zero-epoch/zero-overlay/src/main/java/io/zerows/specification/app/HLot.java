package io.zerows.specification.app;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.atomic.HBelong;

import java.util.function.Function;

/**
 * 🏰 HLot: High-level Lot (Tenant Scope & Ownership)
 * <hr/>
 * <pre>
 * 1. 核心定义 (Core Definition)
 *    HLot 代表系统中的 "租户 (Tenant)" 或 "归属地 (Lot)"。
 *    它是 Aeon 架构的最高行政单位，定义了资源的 "所有权 (Ownership)" 和 "边界 (Scope)"。
 *
 * 2. 架构层级 (Architecture Hierarchy)
 *    系统的层级关系如下（从高到低）：
 *
 *    🏳️ Tier 1: HLot (租户/领地)
 *    |
 *    |-> 拥有 N 个运行实例 (Fleet Management)
 *    |
 *    └── 🛸 Tier 2: HArk (方舟/容器)  [外层 Shell: 负责环境、配置、生命周期]
 *    |
 *    |  ⛓️ (1:1 Binding / 伴生关系)
 *    |
 *    └── 🧩 Tier 3: HApp (应用/核心)  [内层 Kernel: 负责业务、数据、内容]
 *
 * 3. 详细规范 (Specification Detail)
 *    不同层级对应不同的元数据映射（Mapping to X_APP）：
 *
 * A) 业务数据层 (Inner: HApp)
 *    - 不支持动态建模，描述静态画像。
 *      - name           : 应用名称
 *      - ns             : 应用名空间
 *      - language       : 语言环境 (Java/Go/Node...)
 *
 * B) 运行环境层 (Outer: HArk)
 *    - 开放系统级字段，对接容器配置。
 *      - id             : 系统主键 (X_APP.KEY)
 *      - appKey         : 敏感凭证 (X_APP.APP_KEY)
 *      - code           : 业务编码 (X_APP.CODE)
 *
 * C) 租户拓扑层 (System: HLot)
 *    - 👤 CUBE Mode     : 单租户 / 单应用 (SaaS Lite)
 *    - 👥 SUITE Mode    : 单租户 / 多应用 (Enterprise Suite)
 *    - ☁️ SPACE Mode    : 多租户 / 多应用 (SaaS Platform)
 *    - 🌳 GALAXY Mode   : 多层级租户 (Hierarchical) -> 需启用 child()
 *
 * 4. 运行时获取 (Runtime Usage)
 *    应用环境直接对接 HArk。请通过 HAmbient 获取：
 *
 * ⚡ 场景 1: 单体模式 (CUBE)
 *    // 系统中只有一个 App，直接获取。
 *    HArk ark = HAmbient.running();
 *
 * ⚡ 场景 2: 动态多租户模式 (SPACE/GALAXY)
 *    // 需指定维度 (Sigma/TenantId) 来定位特定的 Ark。
 *    HArk ark = HAmbient.running(sigmaOrTenantId);
 * </pre>
 *
 * @author lang : 2023-06-07
 * @see HArk
 * @see HApp
 * @see io.zerows.specification.app.HAmbient
 * @see io.zerows.platform.enums.EmApp
 */
public interface HLot extends HBelong, Function<HLot, HLot> {

    /**
     * 🌳 获取子租户信息 (Hierarchical Tenant)
     * <pre>
     * 在多层级租户架构（如 Galaxy 模式）中，获取当前 HLot 下的子节点。
     * 这允许构建 "集团 -> 分公司 -> 部门" 的树状租户结构。
     * </pre>
     *
     * @param id 子租户标识 (Tenant ID)
     * @return HLot 子租户对象，若不存在返回 null
     */
    default HLot child(final String id) {
        return null;
    }

    /**
     * 🔗 挂载子租户 (Mount Child)
     * <pre>
     * 建立父子租户关系，将子租户实例 (HOI) 挂载到当前租户节点下。
     * </pre>
     *
     * @param id  子租户标识 (Tenant ID)
     * @param hoi 子租户对象 (High-level Lot Instance)
     */
    default void child(final String id, final HLot hoi) {

    }

    /**
     * 📦 获取租户元数据 (Get Metadata)
     * <pre>
     * 获取当前租户 (HLot) 绑定的完整配置数据或扩展属性。
     * 这通常包含了 X_APP 表中扩展字段 (metadata) 的内容，用于存储
     * 该租户特有的定制化配置。
     * </pre>
     *
     * @return {@link JsonObject} 租户元数据
     */
    JsonObject data();

    /**
     * 🔍 读取指定属性 (Read Property)
     * <pre>
     * 从租户元数据中提取指定字段的值。
     * 支持泛型自动转换。
     * </pre>
     *
     * @param field 属性名 (支持路径格式)
     * @param <T>   返回值的类型
     * @return T 属性值，若不存在可能返回 null
     */
    <T> T data(String field);

    /**
     * 💾 覆写元数据 (Overwrite Metadata)
     * <pre>
     * 设置或更新当前租户的完整配置数据。
     * 此操作通常用于初始化或全量更新租户配置。
     * </pre>
     *
     * @param data 新的元数据对象
     * @return {@link JsonObject} 设置后的元数据对象
     */
    JsonObject data(JsonObject data);

    /**
     * ✏️ 写入属性 (Write Property)
     * <pre>
     * 向租户元数据中写入或更新单个键值对。
     * 如果字段已存在则覆盖，不存在则新增。
     * </pre>
     *
     * @param field 属性名
     * @param value 属性值
     * @param <T>   值的类型
     */
    <T> void data(String field, T value);
}
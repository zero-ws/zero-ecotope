package io.zerows.epoch.jigsaw;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDId;

/**
 * 🧩 模块化扩展初始化器，主要用于构造扩展配置相关信息，执行模块化扩展的完整解析流程
 * <pre><code>
 *     1. 表名 -> Dao 的模块化解析 📊
 *     2. Excel 中 MDConnect / KModule 部分的核心解析 📈
 *     3. 最终更改 🔄
 *        Runtime.CRUD -> KModule 加载 📦
 *        MBSE.UI -> KColumn 加载 📋
 *        Excel ->   MDConnect 提取 📤
 * </code></pre>
 *
 * @author lang : 2024-05-08
 */
public interface EquipAt {

    Cc<String, EquipAt> CC_SKELETON = Cc.open();

    /**
     * 🎯 MDId 此处一定不会为空，计算 Cache 时的基本思路
     * <pre><code>
     * 1. 模块化环境 owner() -> Bundle 不为空，根据 Bundle 的基本数据计算
     *    普通环境则直接使用 EquipEntry.class 作为 Key
     * 2. 之后追加上配置对应的 id 信息，内层系统标识的核心 id（一般是目录名）
     * </code></pre>
     *
     * 📝 注：虽然 owner() -> Bundle 时，此处的 id 和 Bundle 本身信息相同，但是为了兼容不同模块化环境相关操作，此处依旧保留了不同的语义
     * <pre><code>
     *     语义1：owner 中的数据包含了 bundle 的 SymbolicName 信息以及版本信息 🏷️
     *     语义2：id 中的数据包含了目录的基础信息，一般是目录名 📁
     * </code></pre>
     */
    static EquipAt of(final MDId mdId) {
        final String cacheKey = EquipEntry.class.getName() + "@" + mdId.value();
        return CC_SKELETON.pick(EquipEntry::new, cacheKey);
    }

    /**
     * 🚀 模块化初始化入口
     *
     * @param configuration 模块化配置信息 ⚙️
     */
    void initialize(MDConfiguration configuration);
}
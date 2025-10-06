package io.zerows.specification.access.app;

import io.r2mo.spi.SPI;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.access.HBoundary;
import io.zerows.specification.access.cloud.HFrontier;
import io.zerows.specification.access.cloud.HGalaxy;
import io.zerows.specification.access.cloud.HSpace;
import io.zerows.spi.modeler.AtomNs;
import io.zerows.support.base.UtBase;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 🏢「应用实例」 🚀
 *
 * <hr/>
 * 此接口用于描述模型所属的应用 📦，注意是应用而不是项目 📋，应用和租户之间是 1:N 的关系 🔄，而租户和项目之间又是 1:N 的关系（非直接） 🔄
 * 整体结构遵循如下的属性图 🗺️：
 * <pre><code>
 *     🌐 Platform
 *      | - 🏢 Tenant
 *           | - 🏢 App1 ( 如 CMDB )
 *               | - 📁 模块：module
 *                   | - 📄 Module 1.1
 *               | - 🧩 模型：atom
 *                   | - 🧩 Atom 1.1 ( 模型跟着应用走 )
 *
 *           | - 🏢 App2 ( 如 ISO-27001 )
 *               | - 📁 模块：module
 *                   | - 📄 Module 2.1
 *                   | - 📄 Module 2.2
 *               | - 🧩 模型：atom
 *                   | - 🧩 Atom 2.1 ( 模型跟着应用走 )
 *
 *           | - 🏢 App3 ( 如 HOTEL / SMAVE )
 *               | - 📁 模块：module
 *                   | - 📄 Module 3.1
 *                   | - 📄 Module 3.2 ( SMAVE App )
 *                      | - 📋 Project 3.1
 *                      | - 📋 Project 3.2
 *               | - 🧩 模型：atom
 *                   | - 🧩 Atom 3.1 ( 模型跟着应用走 )
 * </code></pre>
 *
 * @author lang : 2023-05-21 👨‍💻
 */
public interface HApp extends HBoundary<String>,
    Function<HApp, HApp> {
    /**
     * 🌐 应用所属的 Boundary 信息 🧭，该 Boundary 可用于设置三方向的应用所属。
     * <pre><code>
     *     1. 🌐 {@link HFrontier}
     *     2. 🌌 {@link HGalaxy}
     *     3. 🚀 {@link HSpace}
     * </code></pre>
     * 🏠 默认为 DEFAULT 的边界，边界可以生成 realm 等信息。
     *
     * @return 🌐 {@link String} Boundary 信息
     */
    @Override
    default String realm() {
        return VValue.DEFAULT;
    }

    /**
     * 📤 提取单独的应用程序配置 ⚙️
     *
     * @param key 🔑 配置项
     * @param <T> 🧬 配置项类型
     *
     * @return 📥 {@link String}
     */
    <T> T option(String key);

    <T> void option(String key, T value);

    /**
     * 📤 提取单独应用程序的原始配置（包含了额外的配置信息） 📋
     *
     * @return 📦 {@link JsonObject}
     */
    JsonObject option();

    void option(JsonObject configurationJ, boolean clear);

    /**
     * 🔗 链接完成之后记录的所有方向的连接器对应的标识符 🏷️
     * <pre><code>
     *     - 🏗️ 底座资源信息                     ZONE
     *     - 🐳 CRI信息                         CONTAINER
     *     - 📦 部署计划关联                     DEPLOYMENT
     *       - 🎯 （目标）应用配置容器            DEPLOYMENT_TARGET
     *       - 📦 （源头）部署专用管理端          DEPLOYMENT_SOURCE
     * </code></pre>
     *
     * @return 🗃️ {@link ConcurrentMap}
     */
    default ConcurrentMap<EmApp.Online, String> connected() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 📁 当前应用之下的所有模块列表 🗂️，集合软引用，不做强引用 🔗
     *
     * @return 📄 模块列表
     */
    default Set<String> modules() {
        return Set.of();
    }


    @Override
    default HApp apply(final HApp app) {
        return this;
    }
    // 🚀 高频属性部分：----------------------------------------------------------

    /**
     * 🏷️ 应用程序名 🏢，对应到环境变量 Z_APP 中
     *
     * @return 🏷️ {@link String}
     */
    String name();

    HApp name(String name);

    /**
     * 🌐 应用程序所在名空间 📁，执行 {@link AtomNs} 的SPI可计算名空间
     * 📏 名空间规则为动态规则，此规则最终会限定当前应用程序的基本运行
     *
     * @return 🌐 {@link String}
     */
    String ns();

    HApp ns(String ns);

    /**
     * 🗝️ 当前应用的主键 🔑，用于从系统中提取应用主键专用，系统内置的应用主键
     * <pre><code>
     *     1. ⚡ 高优先级 id 属性
     *     2. 🧱 低优先级 key 属性（一定会存在，系统主键）
     * </code></pre>
     * ❌ 此值为 null 证明应用本身未配置或游离
     *
     * @return 🗝️ {@link String}
     */
    default String id() {
        final String appId = this.option(VName.APP_ID);
        return UtBase.isNil(appId) ? this.option(VName.KEY) : appId;
    }

    /**
     * 根据输入数据计算当前系统的 namespace 名空间信息，其中此方法会在两个核心场景使用
     * <pre>
     *     1. 提取应用名空间
     *     2. 提取模型（identifier）的名空间
     * </pre>
     * 名空间计算会根据 {@link AtomNs} 的 SPI 接口进行计算，最终提取名空间信息让全环境统一
     *
     * @param appName    应用名称
     * @param identifier 标识符
     *
     * @return 名空间
     */
    static String nsOf(final String appName, final String identifier) {
        // 查找合法的 SPI
        final AtomNs atomNs = SPI.findOne(AtomNs.class);
        if (UtBase.isNil(identifier)) {
            return atomNs.ns(appName);
        } else {
            return atomNs.ns(appName, identifier);
        }
    }

    static String nsOf(final String appName) {
        return nsOf(appName, null);
    }
}

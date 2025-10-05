package io.zerows.epoch.basicore.option;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.annotations.ClassYml;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;

/**
 * # 「Co」Vert.x 扩展 🚀
 * 此类是 vert.x 框架中的 `Options` 架构，用于 zero 框架中的集群配置。以下是一些配置部分： 📋
 * <pre>
 * 1. 您是否在 zero 框架中启用了集群（关于 vert.x 集群） ✅
 * 2. 设置集群后，您应为其提供一个默认的 `io.vertx.core.spi.cluster.ClusterManager` 🎯
 * 3. 默认的集群管理器是 `HazelcastClusterManager`（与 vert.x 相同） ⚙️
 * 4. 此处提供 `JsonObject` 引用以获取配置文件中的集群选项 🔧
 * </pre>
 * 以下是 `vertx.yml` 中的 yaml 结构： 📄
 * ---
 * **vertx.yml** 📝
 * ---
 * ```yaml
 * // <pre><code class="yaml">
 *  zero:
 *      vertx:
 *          clustered:
 *              enabled: true           # 启用集群
 *              manager: ""             # 默认集群管理器实现类名
 *              options:                # 集群的 JsonObject 配置
 * // </code></pre>
 * ```
 * <pre>
 * 请注意配置文件，此配置必须在 `vertx.yml` 文件中，而不是 zero 框架中的 `lime` 扩展， ⚠️
 * 也不是第三方配置，文件名必须固定（`vertx.yml`）。 📁
 * </pre>
 * > 注意：生成器将被忽略，因为 `ClusterManager` 序列化具有特定的代码逻辑。 🚫
 *
 * @author <a href="http://www.origin-x.cn">Lang</a> 👨‍💻
 */
// @DataObject(generateConverter = true, publicConverter = false)
@Data
@ClassYml
public class ClusterOptions implements Serializable {

    /**
     * 默认 `enabled`，false 📊
     * 是否在 zero 框架中启用集群模式 ✅
     **/
    private static final boolean ENABLED = false;

    /**
     * 默认 `manager`，HazelcastClusterManager ⚙️
     * 当 `enabled = true` 时此属性有效，您可以提供自定义的 🎯
     * ClusterManager 来覆盖默认的。 🔁
     **/
    private static final ClusterManager MANAGER = new HazelcastClusterManager();

    /**
     * 默认 `options`，不包含任何属性的 JsonObject 📦
     * 当您提供自定义 ClusterManager 时，您可能需要一些额外的 📝
     * 配置数据。 🔧
     **/
    private static final JsonObject OPTIONS = new JsonObject();

    /**
     * -- GETTER -- 📥
     * 获取 zero 框架中是否启用了集群模式。 ✅
     */
    private boolean enabled;
    /**
     * -- GETTER -- 📥
     * 此属性与其他属性不同，`manager` 的字面量是 java 🧠
     * ，此处 ClusterOptions 存储了通过转换器初始化的 `ClusterManager` 📦
     * 引用。对于开发人员来说，直接获取 ClusterManager 更加智能， 👨‍💻
     * 可以忽略实例构建代码流程。 ⚡
     */
    private ClusterManager manager;
    /**
     * -- GETTER -- 📥
     */
    private JsonObject options;

    /**
     * 默认构造函数 🏗️
     */
    public ClusterOptions() {
        this.enabled = ENABLED;
        this.manager = MANAGER;
        this.options = OPTIONS;
    }

    /**
     * 复制构造函数 📋
     *
     * @param other 创建此实例时要复制的其他 {@code ClusterOptions} 📄
     */
    public ClusterOptions(final ClusterOptions other) {
        this.enabled = other.isEnabled();
        this.manager = other.getManager();
        this.options = other.getOptions();
    }

    /**
     * 从 {@link io.vertx.core.json.JsonObject} 创建实例 📄
     *
     * @param json 从中创建的 JsonObject 📦
     */
    public ClusterOptions(final JsonObject json) {
        this();
        ClusterOptionsConverter.fromJson(json, this);
    }

    /**
     * 「Fluent」 ⚡
     * 当您想要修改集群模式时，可以调用此 API。 🛠️
     *
     * @param enabled 基于输入的集群模式开关 🔄
     *
     * @return 此实例的引用。 🔄
     */
    @Fluent
    public ClusterOptions setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * 「Fluent」 ⚡
     * 在选项中设置集群管理器以替换默认的 `ClusterManager`。 🎯
     *
     * @param manager 来自外部的另一个 `ClusterManager` 引用 📌
     *
     * @return 此实例的引用。 🔄
     */
    @Fluent
    public ClusterOptions setManager(final ClusterManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * 「Fluent」 ⚡
     * 在此处设置集群管理器的附加配置数据，如果您提供自定义定义的 ClusterManager， 📝
     * 您可以设置此附加配置来配置 ClusterManager 以进行 `options` 的调整。 🔧
     *
     * @param options 存储附加配置的 JsonObject 📦
     *
     * @return 此实例的引用。 🔄
     */
    @Fluent
    public ClusterOptions setOptions(final JsonObject options) {
        this.options = options;
        return this;
    }

    @Override
    public String toString() {
        return "ClusterOptions{enabled=" + this.enabled
            + ", manager=" +
            ((null == this.manager) ? "null" : this.manager.getClass().getName())
            + ", options="
            + this.options.encode() + '}';
    }

    /**
     * # 「Co」 Vert.x 扩展 🚀
     * <pre>
     * 此类是 `Options` 的 `Converter` 类，它就像 vert.x 框架中的任何其他转换器一样。 🔧
     * 在 vert.x 框架中，每个 `XOptions` 都至少包含一个转换器来处理 `JsonObject` 配置数据。 📊
     * 它提供类型检查和默认值注入功能。 ✅
     * </pre>
     * 此类是 ClusterOptions 辅助工具 🛠️
     * <p>
     * * enabled: 布尔类型 🔢
     * * manager: 字符串类，将被转换为 `ClusterManager` 📌
     * * options: JsonObject 📦
     * ---
     * {@link ClusterOptions} 的转换器 🔧
     * ---
     * > 注意：此类应使用 Vert.x codegen 从 {@link ClusterOptions} 原始类生成， 🏗️
     * 但由于存在 `Class<?>` 类型属性，自动生成器已被忽略。 🚫
     *
     * @author <a href="http://www.origin-x.cn">Lang</a> 👨‍💻
     */
    static final class ClusterOptionsConverter {

        private ClusterOptionsConverter() {
        }

        static void fromJson(final JsonObject json, final ClusterOptions obj) {
            if (json.getValue("enabled") instanceof Boolean) {
                obj.setEnabled(json.getBoolean("enabled"));
            }
            if (json.getValue(KName.OPTIONS) instanceof JsonObject) {
                obj.setOptions(json.getJsonObject(KName.OPTIONS));
            }
            final Object managerObj = json.getValue("manager");
            final Class<?> clazz = Ut.clazz(managerObj.toString());
            // 如果为 null，保持默认值 ⚙️
            final ClusterManager manager = Ut.instance(clazz);
            obj.setManager(manager);
        }
    }
}
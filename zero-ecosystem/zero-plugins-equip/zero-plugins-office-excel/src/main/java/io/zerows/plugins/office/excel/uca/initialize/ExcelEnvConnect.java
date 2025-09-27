package io.zerows.plugins.office.excel.uca.initialize;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.extension.HExtension;
import io.zerows.core.web.model.store.module.OCacheConfiguration;
import io.zerows.core.web.model.uca.normalize.EquipAt;
import io.zerows.core.web.model.uca.normalize.Replacer;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.atom.configuration.modeling.MDConnect;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-06-12
 */
public class ExcelEnvConnect implements ExcelEnv<MDConfiguration> {

    /**
     * 此处直接初始化环境中的连接配置信息，配置信息来源根据不同信息会有所区别：
     * <pre><code>
     *     Norm 环境（原始单机环境）
     *     1. 当前项目一般是启动器 Launcher，配置文件直接位于 src/main/resources 之下
     *     2. 旧版直接走 {@link HExtension}（每个模块一个）读取配置信息
     *     Osgi 环境
     *     1. 当前项目一般是一个独立 Bundle，配置文件依旧位于 src/main/resources 之下
     *     2. 读取配置时，直接读取当前环境中的配置信息
     * </code></pre>
     * 所以新版核心配置处理流程直接依赖 {@link OCacheConfiguration} 来实现，新版本的 vertx-excel.yml 配置会发生简单变化
     * <pre><code>
     * excel:
     *   pen: "io.zerows.plugins.office.excel.tpl.BlueTpl"
     *   environment:
     *     - name: environment.ambient.xlsx
     *       path: "init/oob/environment.ambient.xlsx"
     *       alias:
     *         - /src/main/resources/init/oob/environment.ambient.xlsx
     *   temp: /tmp/
     *   tenant: "init/environment.json"
     *   # 旧版
     *   mapping:
     *     # 导入内容处理
     *   # 新版
     *   mapping: "字符串格式，直接提供配置目录，此处配置目录为启动器或启动 Bundle 的目录"
     * </code></pre>
     *
     * @param config 配置数据
     */
    @Override
    public MDConfiguration prepare(final JsonObject config) {
        // 启动模块配置准备
        final MDConfiguration configuration = this.prepareSelf(config);

        // 扩展模块初始化，HExtension.initialize();
        final Set<HExtension> extensions = HExtension.initialize();
        extensions.forEach(HExtension::connect);
        return configuration;
    }

    private MDConfiguration prepareSelf(final JsonObject config) {
        final String configId = Ut.valueString(config, "configuration");
        if (Ut.isNil(configId)) {
            this.logger().debug("The excel configuration is wrong, please contact the administrator.");
            return null;
        }
        /*
         * 初始化当前环境中的基本配置信息，启动器配置位于
         * plugins/<configId>/ 目录之下，配置目录结构为新版结构
         */
        final OCacheConfiguration extension = OCacheConfiguration.of();
        MDConfiguration configuration = extension.valueGet(configId);
        if (Objects.isNull(configuration)) {
            this.logger().debug("[ Έξοδος ] Could not find configuration: id = {}, the system will web new one", configId);
            configuration = new MDConfiguration(configId);
        }
        final EquipAt component = EquipAt.of(configuration.id());
        component.initialize(configuration);                        // 已执行初始化的情况下此处不会再执行

        /*
         * 额外 attached 的基础配置信息，在执行此处之前，已经执行过内置的反射扫描流程了，所以此处不再担心找不到 Table 的情况，如果此处
         * 找不到 table 证明扫描过程出了问题，而这里的构造流程是构造内部 MDConnect 相关信息，而且和实体无关，主要是附加相关内容到环境里
         * 如果是 OSGI 环境，除非是 APP 类型的 Bundle 会包含此配置，由于其他类型的 Bundle 没有 HSetting，自然不会包含此配置。
         */
        final JsonArray connectA = Ut.valueJArray(config, KName.MAPPING);
        if (Ut.isNotNil(connectA)) {
            final Replacer<MDConnect> connectReplacer = Replacer.ofConnect();
            final List<MDConnect> connectList = connectReplacer.build(connectA);
            configuration.addConnect(connectList);
            this.logger().debug("[ Έξοδος ] Configuration of connect: {} has been added into current environment: id = {}",
                connectList.size(), configuration.id().value());
        }
        return configuration;
    }
}

package io.zerows.epoch.configuration;

import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.metadata.KDatabase;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.HLog;
import io.zerows.specification.storage.HStoreLegacy;
import io.zerows.spi.BootIo;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心配置存储，专用于存储启动配置，替换原始组件部分
 * <pre><code>
 *     1. 核心框架初始化部分
 *     2. Extension 扩展框架的 Pin 部分
 *     3. Scan 的类结果部分
 * </code></pre>
 * 高阶部分的核心接口
 * <pre><code>
 *     1. {@link HConfig}，组件配置接口
 *     2. {@see ZeroEnergy} 启动配置接口
 *     3. {@link ZeroLauncher} 启动器接口
 *     4. {@link BootIo} 启动选择器 / 组件加载器
 * </code></pre>
 * 此部分底层还可以走一个特殊的 {@link HStoreLegacy}，然后从 HStore
 * 中提取配置数据部分，这样可以实现配置数据的存储，而不是直接存储在内存中。完整的结构如：
 * <pre><code>
 *     1. stored：容器配置
 *     2. launcher：启动器配置
 *     3. extension：Zero Extension扩展模块配置
 *     4. infix：Infix架构下的插件配置
 * </code></pre>
 *
 * @author lang : 2023-05-30
 */
@Slf4j
public class ZeroSetting implements HSetting, HLog {
    // 扩展配置
    /** 扩展配置部分 **/
    private final ConcurrentMap<String, HConfig> extension = new ConcurrentHashMap<>();
    /** 插件配置 **/
    private final ConcurrentMap<String, HConfig> infix = new ConcurrentHashMap<>();
    // 基础配置
    /** 生命周期组件配置 **/
    private final ConcurrentMap<EmApp.LifeCycle, HConfig> boot = new ConcurrentHashMap<>();
    /** 容器主配置 */
    private HConfig container;
    /** 启动器配置 **/
    private HConfig launcher;

    private String idOrName;

    private ZeroSetting() {
    }

    public static ZeroSetting of() {
        return new ZeroSetting();
    }

    @Override
    public HConfig container() {
        return this.container;
    }

    public HSetting container(final HConfig container) {
        this.container = container;
        return this;
    }

    public HSetting id(final String idOrName) {
        this.idOrName = idOrName;
        return this;
    }

    @Override
    public String id() {
        return this.idOrName;
    }

    @Override
    public HConfig boot(final EmApp.LifeCycle lifeCycle) {
        return this.boot.get(lifeCycle);
    }

    public HSetting boot(final EmApp.LifeCycle lifeCycle, final HConfig config) {
        this.boot.put(lifeCycle, config);
        return this;
    }

    @Override
    public HConfig launcher() {
        return this.launcher;
    }

    public HSetting launcher(final HConfig launcher) {
        this.launcher = launcher;
        return this;
    }

    public HSetting extension(final String name, final HConfig config) {
        this.extension.put(name, config);
        return this;
    }

    @Override
    public HConfig extension(final String name) {
        return this.extension.get(name);
    }

    public HSetting infix(final EmApp.Native name, final HConfig config) {
        Objects.requireNonNull(name, "[ ZERO ] 内部插件名称不能为空！");
        this.infix.put(name.name(), config);
        return this;
    }

    public HSetting infix(final String name, final HConfig config) {
        this.infix.put(name, config);
        return this;
    }

    @Override
    public HConfig infix(final EmApp.Native name) {
        Objects.requireNonNull(name, "[ ZERO ] 内部插件名称不能为空！");
        return this.infix.get(name.name());
    }

    @Override
    public HConfig infix(final String name) {
        return this.infix.get(name);
    }

    @Override
    public boolean hasInfix(final String name) {
        return this.infix.containsKey(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ZeroSetting vLog() {
        final StringBuilder content = new StringBuilder();
        final HConfig config = this.launcher;
        final Class<?> launcherCls = config.executor();
        content.append("[ BOOT ] 核心组件：\n\uD83D\uDD25 主启动器 = ")
            .append(Objects.isNull(launcherCls) ? null : launcherCls.getName()).append("\n");
        this.boot.forEach((lifeCycle, bootConfig) -> {
            final Class<?> executor = bootConfig.executor();
            content.append("\t 生命周期：").append(lifeCycle).append(", ")
                .append(Objects.isNull(executor) ? null : executor.getName()).append("\n");
        });
        content.append("\uD83D\uDD11 配置ID = ").append(this.id())
            .append(" ( 类型 =").append(this.getClass().getName())
            .append(", hashCode = ").append(this.hashCode()).append(" )\n");
        if (this.container instanceof final ConfigContainer containerConfig) {
            final ClusterOptions options = containerConfig.ref();
            // 集群配置
            content.append("\uD83C\uDF10 集群配置（默认） = ").append(Objects.nonNull(options) ? options.getOptions() : null).append("\n");
            // 默认配置
            this.vLog(content, "\uD83D\uDFE1", new ArrayList<>() {
                {
                    this.add(containerConfig.delivery());
                    this.add(containerConfig.deployment());
                    this.add(containerConfig.shared());
                }
            });


            // 实例信息
            final Set<String> keySet = containerConfig.keyInstance();
            content.append("\t\uD83E\uDDEC 实例数量 = ").append(keySet.size()).append("\n");

            int index = 0;
            for (final String key : keySet) {
                final HConfig instance = containerConfig.instance(key);
                if (instance instanceof final ConfigInstance instanceConfig) {
                    content.append("\t\t实例 [").append(index).append("] -- ").append(instanceConfig.name()).append("\n");
                    final JsonObject vertxOptions = instanceConfig.options();
                    content.append("\t\t\uD83E\uDDEA Options = ").append(vertxOptions).append("\n");
                    // 默认配置
                    this.vLog(content, "\t\uD83D\uDD35", new ArrayList<>() {
                        {
                            this.add(instanceConfig.delivery());
                            this.add(instanceConfig.deployment());
                            this.add(instanceConfig.shared());
                        }
                    });
                }
                index++;
            }


            content.append("\t⚙️ 插件配置").append("\n");
            this.vLog(content, this.infix);


            content.append("\t⚙️ 扩展配置").append("\n");
            this.vLog(content, this.extension);

            content.append("\t\uD83D\uDDC4 数据库配置");
            this.vLog(content, this.infix.get(EmApp.Native.DATABASE.name()));
        } else {
            throw new _501NotSupportException("[ ZERO ] 主容器类型有异常！" + this.container.getClass());
        }
        log.info(content.toString());
        return this;
    }

    private void vLog(final StringBuilder content, final HConfig config) {
        if (Objects.isNull(config)) {
            return;
        }
        if (!(config instanceof final ConfigDS ds)) {
            throw new _501NotSupportException("[ ZERO ] 数据库配置类型有异常！" + config.getClass());
        }
        final KDatabase database = ds.ref();
        content.append("( dynamic = ").append(ds.dynamic()).append(", strict = ").append(ds.strict()).append(" )\n");
        content.append("\t\t\uD83D\uDFE9 主库：").append("key = ").append(ds.master()).append("\n");
        this.vLog(content, database);
        final ConcurrentMap<String, HConfig> slaveDatabase = ds.config();
        slaveDatabase.keySet().stream().filter(field -> !field.equals(ds.master())).forEach(field -> {
            final HConfig slaveConfig = slaveDatabase.get(field);
            if (Objects.nonNull(slaveConfig)) {
                content.append("\t\t\uD83D\uDFE6 从库：").append("key = ").append(field).append("\n");
                final KDatabase slaveDatabaseRef = slaveConfig.ref();
                this.vLog(content, slaveDatabaseRef);
            }
        });
    }

    private void vLog(final StringBuilder content, final KDatabase database) {
        content.append("\t\t\t数据库名：⚡️").append(database.getInstance()).append("\n");
        content.append("\t\t\t连接字符串：").append(database.getUrl()).append("\n");
    }

    private void vLog(final StringBuilder content, final ConcurrentMap<String, HConfig> configMap) {
        for (final String field : configMap.keySet()) {
            final HConfig config = configMap.get(field);
            if (IGNORE_SET.contains(field)) {
                continue;
            }
            if (Objects.isNull(config.ref())) {
                content.append("\t\t").append(field).append(" = ").append(config.options()).append("\n");
            } else {
                content.append("\t\t").append(field).append(" = ").append(config.ref().toString()).append("\n");
            }
        }
    }

    private static final Set<String> IGNORE_SET = Set.of(EmApp.Native.DATABASE.name());

    private void vLog(final StringBuilder content, final String prefix,
                      final List<HConfig> configList) {
        // 默认配置
        final HConfig deliveryConfig = configList.get(0);
        if (Objects.nonNull(deliveryConfig)) {
            content.append("\t").append(prefix).append(" Delivery = ").append(deliveryConfig.options()).append("\n");
        }
        final HConfig deploymentConfig = configList.get(1);
        if (Objects.nonNull(deploymentConfig)) {
            /*
             * 此处必须是特殊打印效果，要打印提供的默认配置项相关信息
             */
            content.append("\t").append(prefix).append(" Deployment = \n");
            final JsonObject deploymentJ = deploymentConfig.options();

            final JsonObject agentJ = Ut.valueJObject(deploymentJ, "agent");
            content.append("\t\t").append(" Agent ( Default ) = ").append(agentJ).append("\n");
            final JsonObject agentJOf = Ut.valueJObject(deploymentJ, "agentOf");
            agentJOf.fieldNames().forEach(field -> content.append("\t\t\t- ").append(field).append("\n"));

            final JsonObject workerJ = Ut.valueJObject(deploymentJ, "worker");
            content.append("\t\t").append(" Worker ( Default ) = ").append(workerJ).append("\n");
            final JsonObject workerOf = Ut.valueJObject(deploymentJ, "workerOf");
            workerOf.fieldNames().forEach(field -> content.append("\t\t\t- ").append(field).append("\n"));
        }
        final HConfig sharedConfig = configList.get(2);
        if (Objects.nonNull(sharedConfig)) {
            content.append("\t").append(prefix).append(" Shared = ").append(sharedConfig.options()).append("\n");
        }
    }
}
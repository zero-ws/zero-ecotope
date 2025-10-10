package io.zerows.epoch.configuration;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmBoot;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmDataSource;
import io.zerows.epoch.basicore.YmMvc;
import io.zerows.epoch.basicore.YmServer;
import io.zerows.epoch.basicore.YmVertx;
import io.zerows.epoch.basicore.YmWebSocket;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 将 {@link YmConfiguration} 转换成 {@link HSetting} 的核心实现逻辑，有必要会更改 {@link HSetting} 的接口设计，以
 * 保证为上层提供整个配置服务的能力，代码执行到此处已完成了 Nacos 对接，所以此处不再考虑配置本身的来源问题。
 *
 * @author lang : 2025-10-08
 */
class EquipZero implements Equip {
    @Override
    public HSetting initialize(final YmConfiguration configuration) {
        final ZeroSetting setting = ZeroSetting.of();
        // ID 绑定，对应 vertx.application.name 的值，无分配时使用随机数，推荐使用固定值
        setting.id(configuration.id());

        // 添加 launcher 启动周期的 setting
        this.initialize(setting, configuration.getBoot());

        // 添加 server 服务器的 setting
        this.initialize(setting, configuration.getServer());

        // 添加 vertx 核心配置
        this.initialize(setting, configuration.getVertx());

        // 扩展配置 extension 核心
        this.initialize(setting, configuration.extension());

        setting.vLog();
        return setting;
    }

    private void initialize(final ZeroSetting setting, final ConcurrentMap<String, JsonObject> extension) {
        if (Objects.isNull(extension) || extension.isEmpty()) {
            return;
        }
        extension.forEach((extensionKey, extensionConfig) -> {
            final HConfig config = new ConfigNorm();
            config.putOptions(extensionConfig);
            setting.extension(extensionKey, config);
        });
    }

    /**
     *
     * 直接对标 -> vertx.config.instance 数组
     * <pre>
     *     1. 默认模式下，instance 数组只有一个元素
     *     2. 集群模式下，instance 数组可以有多个元素，所以集群模式下主容器的配置采用 {@link ConfigContainer} 代替
     *        其他所有的插件等 {@link ConfigNorm}
     * </pre>
     * 整体管理状况如
     * <pre>
     *     1. {@link ZeroSetting#container()}
     *           = {@link HConfig} / 子类 {@link ConfigContainer}
     *             -> 主容器内置：
     *                唯一 {@link EmApp.Native#CLUSTER}
     *                高优先级 {@link EmApp.Native#SESSION}         > vertx.server.session （二选一）
     *                低优先级 {@link EmApp.Native#DELIVERY}        < vertx.config.instance[?].delivery
     *                低优先级 {@link EmApp.Native#DEPLOYMENT}      < vertx.config.instance[?].deployment
     *                低优先级 {@link EmApp.Native#SHARED}          < vertx.config.instance[?].shared
     *                此处除 session 以外，其他内容都拥有双配置，只是优先级选择不同而已
     *     2. 其他扩展配置
     *           = {@link HConfig} / 子类 {@link ConfigNorm}
     *             -> 功能插件：
     *                唯一 {@link EmApp.Native#CORS}
     *                唯一 {@link EmApp.Native#SECURITY}
     *     3. 业务层核心配置
     *           = {@link HConfig} / 子类 {@link ConfigNorm}
     *             -> 业务插件：
     * </pre>
     */
    private void initialize(final ZeroSetting setting, final YmVertx vertx) {
        Optional.ofNullable(vertx).ifPresent(vertxYml -> {
            // Cors
            final YmMvc mvc = vertxYml.getMvc();
            Optional.ofNullable(mvc)
                .ifPresent(mvcRef -> this.initializeT(setting, EmApp.Native.CORS, mvcRef::getCors));

            // Security
            this.initializeJ(setting, EmApp.Native.SECURITY, vertxYml::getSecurity);

            // Session 高优先级 --> 覆盖
            this.initializeJ(setting, EmApp.Native.SESSION, vertxYml::getSession);

            // Shared 低优先级配置
            this.initializeJ(setting, EmApp.Native.SHARED, vertxYml::getShared);

            // DataSource 数据库配置
            this.initializeT(setting, vertxYml::getDatasource);

            // REDIS 配置
            final YmVertx.Data vertxData = vertxYml.getData();
            if (Objects.nonNull(vertxData)) {
                // REDIS
                this.initializeJ(setting, EmApp.Native.REDIS, vertxData::getRedis);
            }

            /* 主容器配置，此配置会绑定到 container 方法中形成特殊配置 */
            final ConfigContainer container = ConfigContainer.of(vertxYml.getCluster(), vertxYml.getConfig());
            setting.container(container);
        });
    }

    /**
     * 三个和服务端直接相关的配置
     * <pre>
     *     1. {@link EmApp.Native#SERVER} / 唯一
     *     2. {@link EmApp.Native#SESSION} 低优先级         < vertx.session
     *     3. {@link EmApp.Native#WEBSOCKET} / 唯一
     * </pre>
     *
     * @param setting 设置信息
     * @param server  服务器配置
     */
    private void initialize(final ZeroSetting setting, final YmServer server) {
        Optional.ofNullable(server).ifPresent(serverYml -> {
            // server 服务器配置
            this.initializeJ(setting, EmApp.Native.SERVER, serverYml::combined);

            // 第一轮 session 配置
            this.initializeJ(setting, EmApp.Native.SESSION, serverYml::getSession);

            // websocket 核心配置
            final YmWebSocket websocket = serverYml.getWebsocket();
            Optional.ofNullable(websocket).ifPresent(websocketRef ->
                this.initializeJ(setting, EmApp.Native.WEBSOCKET, websocketRef::combined));
        });
    }

    private void initialize(final ZeroSetting setting, final YmBoot boot) {
        Objects.requireNonNull(boot, "[ ZERO ] 启动配置不能为空！");
        final ConfigNorm configLauncher = new ConfigNorm();
        Fn.jvmAt(Objects.nonNull(boot.getLauncher()), () -> configLauncher.putExecutor(boot.getLauncher()));
        setting.launcher(configLauncher);

        /* 启动过程中的生命周期组件配置 */
        this.initialize(setting, EmApp.LifeCycle.ON, boot::getOn);
        this.initialize(setting, EmApp.LifeCycle.OFF, boot::getOff);
        this.initialize(setting, EmApp.LifeCycle.PRE, boot::getPre);
        this.initialize(setting, EmApp.LifeCycle.RUN, boot::getRun);
    }

    @SuppressWarnings("all")
    private <T> void initializeT(final ZeroSetting setting, final EmApp.Native name,
                                 final Supplier<T> componentFn) {
        final T ref = componentFn.get();
        Fn.jvmAt(Objects.nonNull(ref), () -> {
            final ConfigNorm config = new ConfigNorm();
            // DEFAULT_REFERENCE
            setting.infix(name, config.putRef(ref));
        });
    }

    private void initializeT(final ZeroSetting setting, final Supplier<YmDataSource> sourceFn) {
        final YmDataSource source = sourceFn.get();
        Fn.jvmAt(Objects.nonNull(source), () -> {
            final HConfig database = ConfigDS.of(source);
            // 数据源处理
            setting.infix(EmApp.Native.DATABASE, database);
        });
    }

    private void initializeJ(final ZeroSetting setting, final EmApp.Native name,
                             final Supplier<?> componentFn) {
        final Object component = componentFn.get();
        if (Objects.isNull(component)) {
            return;
        }
        final JsonObject optionRef;
        if (component instanceof final JsonObject options) {
            optionRef = options;
        } else {
            optionRef = Ut.serializeJson(component);
        }
        Fn.jvmAt(Ut.isNotNil(optionRef), () -> {
            final ConfigNorm config = new ConfigNorm();
            // options
            setting.infix(name, config.putOptions(optionRef));
        });
    }

    private void initialize(final ZeroSetting setting, final EmApp.LifeCycle life,
                            final Supplier<MMComponent> componentFn) {
        final MMComponent component = componentFn.get();
        Fn.jvmAt(Objects.nonNull(component), () -> {
            final ConfigNorm config = new ConfigNorm();
            // options + DEFAULT_META
            setting.boot(life, config.putOptions(component.getConfig())
                .putExecutor(component.getComponent())
            );
        });
    }
}

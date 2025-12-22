package io.zerows.extension.module.mbseapi.boot;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.sdk.Axis;
import io.zerows.extension.module.mbseapi.component.JtMonitor;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.extension.skeleton.metadata.ModManagerBase;
import io.zerows.platform.constant.VValue;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 特殊管理器，直接被 Actor 调用，内置对接基类中的核心管理等相关模型
 * <pre>
 *     1. 提供 /configuration.json 配置信息
 *     2. 提供 metamodel: 节点配置信息
 *     3. 提供服务环境 {@link ServiceEnvironment}
 * </pre>
 * 管理器调用结构
 * <pre>
 *     1. {@link MDModuleActor} -> 内置 {@see MDModuleManager}，只能通过 Actor 调用
 *               继承
 *     2. {@link ModMBSEApiActor} -> 内置 {@see ModMBSEManager}，只能通过 Actor 调用
 *
 *     3. 外部：只能通过 {@link ModMBSEApiActor} 调用获取，统一归口
 *        两个 Manager 都不对外
 * </pre>
 *
 * @author lang : 2025-12-22
 */
@Slf4j
public class ModMBSEManager extends ModManagerBase<YmMetamodel> {
    private static ModMBSEManager INSTANCE;
    private static final Cc<String, ServiceEnvironment> AMBIENT = Cc.open();
    private YmMetamodel setting;
    private Class<Axis> axisCls;
    // 监控专用
    private static final AtomicInteger LOG_OPTION = new AtomicInteger(0);
    private final transient JtMonitor monitor = JtMonitor.create(this.getClass());

    private ModMBSEManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static ModMBSEManager of() {
        if (INSTANCE == null) {
            INSTANCE = new ModMBSEManager();
        }
        return INSTANCE;
    }

    @Override
    public void setting(final YmMetamodel setting) {
        this.setting = setting;
    }

    @Override
    public YmMetamodel setting() {
        return this.setting;
    }

    /**
     * 是否启用，若启用则特殊配置 {@link YmMetamodel} 一定不能为 null，此处检查会影响 Dynamic 路由的挂载，若不满足则直接跳过，两处配置
     * <pre>
     *     1. vertx.yml 中是否配置 metamodel 的动态路由信息
     *     2. ServiceEnvironment 环境信息检查，若环境信息不通过则直接返回 false
     * </pre>
     *
     * @return 启用状态
     */
    @Override
    public boolean isEnabled(final HBundle bundle) {
        if (Objects.isNull(this.setting)) {
            return false;
        }
        final Class<Axis> axisCls = this.getAxisCls();
        if (Objects.isNull(axisCls)) {
            return false;
        }
        if (VValue.ZERO == LOG_OPTION.getAndIncrement()) {
            log.info("{} 系统监测到动态系统路由组件：{}", JtConstant.K_PREFIX_BOOT, axisCls.getName());
        }
        final JsonObject configuration = this.configuration();
        if (VValue.ONE == LOG_OPTION.getAndIncrement()) {
            log.info("{} 监测到动态组件核心配置：{}", JtConstant.K_PREFIX_BOOT, configuration);
        }

        // 追加监控配置
        this.monitor.agentConfig(configuration);
        if (Objects.isNull(AMBIENT) || AMBIENT.isEmpty()) {
            this.monitor.workerFailure();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Class<Axis> getAxisCls() {
        if (Objects.nonNull(this.axisCls)) {
            return this.axisCls;
        }
        if (Objects.isNull(this.setting)) {
            return null;
        }
        final YmMetamodel.Router router = this.setting.getRouter();
        if (Objects.isNull(router)) {
            log.warn("{} 缺乏 router 配置，无法提取动态路由组件信息！", JtConstant.K_PREFIX_BOOT);
            return null;
        }
        final Class<?> cls = router.getComponent();
        if (!Ut.isImplement(cls, Axis.class)) {
            log.warn("{} 动态路由组件信息错误，指定的组件 {} 未实现 Axis 接口！", JtConstant.K_PREFIX_BOOT, cls.getName());
            return null;
        }
        this.axisCls = (Class<Axis>) cls;
        return this.axisCls;
    }

    public ConcurrentMap<String, ServiceEnvironment> serviceEnvironment() {
        return AMBIENT.get();
    }

    public void serviceEnvironment(final String appId, final ServiceEnvironment environment) {
        AMBIENT.put(appId, environment);
    }

    public void serviceEnvironment(final ConcurrentMap<String, ServiceEnvironment> environments) {
        AMBIENT.putAll(environments);
    }

    public Axis getAxis() {
        final Class<Axis> axisCls = this.getAxisCls();
        return Axis.ofOr(axisCls);
    }

    public Set<JtUri> getApis() {
        return AMBIENT.keySet().stream()
            .flatMap(appId -> AMBIENT.get(appId).routes().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}

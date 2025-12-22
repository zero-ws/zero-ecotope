package io.zerows.extension.module.mbseapi.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.extension.skeleton.metadata.MDSetting;
import io.zerows.specification.app.HAmbient;

/**
 * 替换旧版的 JetPolluxOptions，转换成 Metamodel 专用配置对象，直接检查核心环境中是否包含主配置即可
 * <pre>
 *     1. vertx.yml 中必须配置了 metamodel 节点
 *     2. deployment 主管 Agent / Worker 的实例发布
 *     3. 启动组件中对于路由有特定配置
 * </pre>
 *
 * @author lang : 2025-12-16
 */
@Actor(value = "metamodel", sequence = 1017)
public class ModMBSEApiActor extends MDModuleActor {
    private static final ModMBSEManager MANAGER = ModMBSEManager.of();

    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    /**
     * 执行特殊方法，提取 {@link HAmbient} 并且注册对应的配置，对接遗留系统
     */
    @Override
    protected Future<Boolean> startAsync(final MDConfiguration configuration, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }

    /**
     * 转换主配置中的特殊部分
     * <pre>
     *     metamodel:
     *       router:
     *         path:
     *         component:
     *       deployment:
     *         worker:
     *           instances:
     *         agent:
     *           instances:
     * </pre>
     * 这个步骤一定会在 {@link MDConfiguration} 配置初始化完成之后执行
     *
     * @param configuration 基础配置
     * @param vertxRef      Vertx实例引用
     *
     * @return 特殊配置对象，实际类型：{@link YmMetamodel}
     */
    @Override
    protected Object setConfig(final MDConfiguration configuration, final Vertx vertxRef) {
        final MDSetting<YmMetamodel> settingFor = MDSetting.of(YmMetamodelSetting::new);
        final YmMetamodel setting = settingFor.bootstrap(configuration.inSetting(), vertxRef);

        // 注册特定配置到管理器中
        MANAGER.setting(setting);
        return setting;
    }
}

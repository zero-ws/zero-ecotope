package io.zerows.extension.module.mbseapi.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HArk;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
@Slf4j
public class MDMBSEApiActor extends MDModuleActor {

    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    /**
     * 执行特殊方法，提取 {@link HAmbient} 并且注册对应的配置，对接遗留系统
     */
    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        Ke.banner("「Πίδακας δρομολογητή」- ( MBSEApi )");
        final ConcurrentMap<String, HArk> arkMap = ambient.app();

        // 应用数量统计
        log.info("{} HAmbient 在环境中检测到 {} 应用", KeConstant.K_PREFIX_BOOT, arkMap.size());
        if (arkMap.isEmpty()) {
            log.warn("{} HAmbient 环境配置有误，跳过 Metamodel 模块初始化！", KeConstant.K_PREFIX_BOOT);
            return Future.succeededFuture(Boolean.TRUE);
        }

        final ConcurrentMap<String, Future<ServiceEnvironment>> futureMap = new ConcurrentHashMap<>();
        arkMap.forEach((appId, each) -> {
            final ServiceEnvironment environment = new ServiceEnvironment(each);
            if (environment.isOk()) {
                // 没有 Ok 的场景下 environment 是不可以执行初始化的
                futureMap.put(appId, environment.init(vertxRef));
            } else {
                log.warn("{} 应用 {} 的 ServiceEnvironment 配置不完整，跳过初始化！", KeConstant.K_PREFIX_BOOT, appId);
            }
        });
        return Fx.combineM(futureMap).compose(processed -> {
            final MDMBSEManager manager = this.manager();
            manager.serviceEnvironment(processed);
            log.info("{} ServiceEnvironment 初始化完成！", KeConstant.K_PREFIX_BOOT);
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDMBSEManager manager() {
        return MDMBSEManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<MDCMetamodel> typeOfMDC() {
        return MDCMetamodel.class;
    }
}

package io.zerows.extension.module.ambient.boot;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DI;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.serviceimpl.DocBuilder;
import io.zerows.extension.module.ambient.servicespec.DocBStub;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.program.Ux;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 *     207  = {@link MDAmbientActor}
 *     1017 = {@see MDIntegrationActor} / @io.zerows.extension.module.integration.boot
 *     1106 = {@link SISDirectoryActor} ( Virtual )
 * </pre>
 * 此处 SISDirectory 必须要等待 integration 组件启动之后才可以
 */
@Actor(value = "extension", sequence = 1106, configured = false)
@Slf4j
public class SISDirectoryActor extends MDModuleActor {
    private static final DI PLUGIN = DI.create(MDAmbientActor.class);
    private static final AtomicBoolean IS_DOC = new AtomicBoolean(Boolean.TRUE);

    @Override
    protected String MID() {
        return SISID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        final AtConfig config = this.manager().config();

        /* 网盘配置 */
        log.info("{} 本地路径：\"{}\" @ type = {}, 目录类型：{}", KeConstant.K_PREFIX_STORE,
            config.getStorePath(), config.getFileStorage(), config.getFileIntegration());

        return this.startDocAsync(ambient, config);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDAmbientManager manager() {
        return MDAmbientManager.of();
    }

    private Future<Boolean> startDocAsync(final HAmbient ambient, final AtConfig config) {
        // 遍历 HAmbient 中的 HArk 启动
        final Set<Future<Boolean>> starterSet = new HashSet<>();
        ambient.app().forEach((appId, ark) -> starterSet.add(
            // 文档平台初始化
            Fx.combineB(Set.of(this.startDocAsync(ark, config)))
        ));
        // Document Engine 文档管理平台
        return Fx.combineB(starterSet);
    }

    private Future<Boolean> startDocAsync(final HArk v, final AtConfig config) {
        final boolean disabled = Ut.isNil(config.getFileIntegration());
        if (disabled) {
            if (IS_DOC.getAndSet(Boolean.FALSE)) {
                log.info("{} 文档平台已禁用 Document Platform Disabled !!", AtConstant.K_PREFIX);
            }
            return Future.succeededFuture(Boolean.TRUE);
        }
        // 此处提前调用 initialize 方法，此方法保证无副作用的多次调用即可
        final DocBStub docStub = PLUGIN.createSingleton(DocBuilder.class);
        // Here mapApp function extract `id`
        final HApp app = v.app();
        final String appId = app.id(); // Ut.valueString(appJ, KName.KEY);
        return docStub.initialize(appId, config.getFileIntegration()).compose(initialized -> {
            if (!initialized.isEmpty()) {
                log.info("{} / Name = {} 存储目录数 = {}", AtConstant.K_PREFIX, app.name(), initialized.size());
            }
            return Ux.futureT();
        });
    }
}

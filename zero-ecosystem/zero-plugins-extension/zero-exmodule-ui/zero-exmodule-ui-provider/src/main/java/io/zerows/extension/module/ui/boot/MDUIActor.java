package io.zerows.extension.module.ui.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.module.ui.common.UiConfig;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.specification.app.HAmbient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 1017, configured = false)
@Slf4j
public class MDUIActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        final MDUIManager manager = this.manager();
        final UiConfig config = manager.config();
        manager.compile(config);
        log.info("{} UI 初始化完成！", KeConstant.K_PREFIX_BOOT);
        return Future.succeededFuture(Boolean.TRUE);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected MDUIManager manager() {
        return MDUIManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<UiConfig> typeOfConfiguration() {
        return UiConfig.class;
    }
}

package io.zerows.extension.crud.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.crud.common.IxConfig;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.specification.app.HAmbient;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 1231, configured = false)
@Slf4j
public class MDCRUDActor extends MDModuleActor {
    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        final IxConfig config = this.manager().config();
        if (Objects.isNull(config)) {
            log.info("{} 配置丢失，跳过 CRUD 引擎 {}.", KeConstant.K_PREFIX_BOOT, this.MID());
            return Future.succeededFuture(Boolean.FALSE);
        }

        final boolean initialized = this.manager().configure();
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MDCRUDManager manager() {
        return MDCRUDManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<IxConfig> typeOfConfiguration() {
        return IxConfig.class;
    }
}

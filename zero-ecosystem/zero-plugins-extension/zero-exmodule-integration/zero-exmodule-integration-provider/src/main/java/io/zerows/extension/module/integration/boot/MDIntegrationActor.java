package io.zerows.extension.module.integration.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.module.integration.common.IsConfig;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.specification.app.HAmbient;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 1017, configured = false)
@Slf4j
public class MDIntegrationActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        Ke.banner("「Ολοκλήρωση」- Integration ( Is )");


        // 环境变量优先处理 Z_SIS_STORE
        final IsConfig config = this.manager().config();
        if (Objects.isNull(config)) {
            log.info("{} 集成功能未开启，请检查存储相关配置！", KeConstant.K_PREFIX_BOOT);
            return Future.succeededFuture(Boolean.TRUE);
        }
        final String storePath = ENV.of().get(EnvironmentVariable.SIS_STORE, config.getStoreRoot());
        config.setStoreRoot(storePath);

        return Future.succeededFuture(Boolean.TRUE);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDIntegrationManager manager() {
        return MDIntegrationManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<IsConfig> typeOfConfiguration() {
        return IsConfig.class;
    }
}

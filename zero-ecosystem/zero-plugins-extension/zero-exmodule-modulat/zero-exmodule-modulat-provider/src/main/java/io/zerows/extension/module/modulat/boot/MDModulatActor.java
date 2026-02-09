package io.zerows.extension.module.modulat.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "modulat", sequence = 1017, configured = false)
@Slf4j
public class MDModulatActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HArk ark, final Vertx vertxRef) {
        final HApp app = ark.app();
        final String appKey = app.id(); // Ut.valueString(appJ, KName.KEY);
        if (Objects.isNull(appKey)) {
            log.info("{} App Id = null, 跳过初始化!!", KeConstant.K_PREFIX_BOOT);
            return Future.succeededFuture(Boolean.TRUE);
        }
        final ExModulat modulat = HPI.findOneOf(ExModulat.class);
        // BUG: 解决无法提取 app 数据导致模块无法加载的问题
        return modulat.extension(app.data()).compose(nil -> Ux.futureT());
    }


    @Override
    @SuppressWarnings("unchecked")
    protected MDModulatManager manager() {
        return MDModulatManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<BkConfig> typeOfConfiguration() {
        return BkConfig.class;
    }
}

package io.zerows.extension.module.modulat.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;
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


        final JsonObject appJ = app.data();
        if (Ut.isNil(appJ)) {
            return Future.succeededFuture(Boolean.TRUE);
        }


        final String appId = Ut.valueString(appJ, KName.APP_ID);
        /*
         * 特殊说明，此处加载模块配置时，必须加载整个完整应用的核心配置，完整应用配置即当前应用没有子应用，而扩展模块或微应用通常会挂上子应用
         * 信息，若挂载了子应用信息，则说明当前应用是一个微应用或扩展模块，非子应用的判断条件如：
         * appId IS NULL
         * 而所有子应用都会在导入过程中赋予 appId 信息。
         */
        if (Ut.isNotNil(appId)) {
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

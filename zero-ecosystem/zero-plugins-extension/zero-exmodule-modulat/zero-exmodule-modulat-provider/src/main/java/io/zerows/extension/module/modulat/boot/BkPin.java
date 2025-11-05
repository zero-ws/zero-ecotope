package io.zerows.extension.module.modulat.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.module.modulat.common.Bk;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.spi.HPI;

import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BkPin implements HRegistry.Mod<Vertx> {

    public static Set<String> getBuiltIn() {
        return BkConfiguration.builtIn();
    }

    @Override
    public Future<Boolean> initializeAsync(final Vertx container, final HArk ark) {

        final HApp app = ark.app();
        final String appKey = app.id(); // Ut.valueString(appJ, KName.KEY);
        if (Objects.isNull(appKey)) {
            Bk.LOG.Init.warn(this.getClass(), "App Id = null, ignored initialized!!");
            return Ux.futureF();
        }
        final ExModulat modulat = HPI.findOverwrite(ExModulat.class);
        return modulat.extension(app.option()).compose(nil -> Ux.futureT());
    }
}

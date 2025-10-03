package io.zerows.extension.runtime.skeleton.refine;

import io.vertx.core.MultiMap;
import io.zerows.epoch.constant.KWeb;
import io.zerows.metadata.boot.KPivot;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.access.app.HAmbient;
import io.zerows.specification.access.app.HArk;

/**
 * @author lang : 2023-06-07
 */
class KeApp {
    static HArk ark(final MultiMap headers) {
        /* 1. X-App-Id 检索 */
        final String appId = headers.get(KWeb.HEADER.X_APP_ID);
        if (Ut.isNotNil(appId)) {
            return ark(appId);
        }


        /* 2. X-Sigma 检索 */
        final String sigma = headers.get(KWeb.HEADER.X_SIGMA);
        if (Ut.isNotNil(sigma)) {
            return ark(sigma);
        }

        /* 3. X-App-Key 检索 */
        final String appKey = headers.get(KWeb.HEADER.X_APP_KEY);
        if (Ut.isNotNil(appKey)) {
            return ark(appKey);
        }
        return null;
    }

    static HArk ark(final String value) {
        final HAmbient ambient = KPivot.running();
        return ambient.running(value);
    }

    static HArk ark() {
        final HAmbient ambient = KPivot.running();
        return ambient.running();
    }
}

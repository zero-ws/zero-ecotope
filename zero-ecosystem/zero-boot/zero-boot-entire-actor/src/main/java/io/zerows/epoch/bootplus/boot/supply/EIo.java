package io.zerows.epoch.bootplus.boot.supply;

import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.constant.spec.VWeb;
import io.zerows.epoch.common.shared.app.KIntegration;

import java.util.Objects;

/**
 * @author lang : 2023-06-10
 */
class EIo {

    static String ioPartyB(final String key, final KIntegration integration) {
        Objects.requireNonNull(key);
        final String hit = key.replace("/", "");
        /*
         * running/{vendor}/{hit}/{vendor}.json
         */
        return VWeb.RUNTIME + VString.SLASH
            + integration.getVendorConfig() + VString.SLASH
            + hit + VString.SLASH
            + integration.getVendor() + VString.DOT + VPath.SUFFIX.JSON;
    }
}

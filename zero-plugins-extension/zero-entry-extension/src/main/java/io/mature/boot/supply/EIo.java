package io.mature.boot.supply;

import io.zerows.ams.constant.VPath;
import io.zerows.ams.constant.VString;
import io.zerows.ams.constant.spec.VWeb;
import io.zerows.common.app.KIntegration;

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

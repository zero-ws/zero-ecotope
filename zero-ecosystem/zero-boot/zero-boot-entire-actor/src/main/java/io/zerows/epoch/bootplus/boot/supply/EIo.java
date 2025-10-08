package io.zerows.epoch.bootplus.boot.supply;

import io.zerows.platform.constant.VClassPath;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.metadata.KIntegration;

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
        return VClassPath.RUNTIME + VString.SLASH
            + integration.getVendorConfig() + VString.SLASH
            + hit + VString.SLASH
            + integration.getVendor() + VString.DOT + VValue.SUFFIX.JSON;
    }
}

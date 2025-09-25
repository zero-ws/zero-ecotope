package io.zerows.ams.util;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VValue;
import io.zerows.core.uca.net.IPHost;

import java.net.InetAddress;
import java.util.Locale;

final class UNetwork {
    private UNetwork() {
    }

    /**
     * @return ip address of ipv4 format
     */
    static String getIPv4() {
        return IPHost.getInstance().getExtranetIPv4Address();
    }

    static String getHostName() {
        return Fn.jvmOr(InetAddress::getLocalHost).getHostName();
    }

    /**
     * @return ip address of ipv6 format
     */
    static String getIPv6() {
        return IPHost.getInstance().getExtranetIPv6Address();
    }

    /**
     * @return ip address of common format ( detect by system about 4 or 6 )
     */
    static String getIP() {
        return IPHost.getInstance().getExtranetIPAddress();
    }

    static String netUri(final String url) {
        if (null == url) {
            return null;
        }
        if (url.contains("?")) {
            return url.split("\\?")[0];
        } else {
            return url;
        }
    }

    static JsonObject netStatus(final String line) {
        if (null == line || !line.contains(" ")) {
            return new JsonObject();
        }
        final String[] splitted = line.split(" ");
        if (2 == splitted.length) {
            final String method = splitted[VValue.IDX].trim().toUpperCase(Locale.getDefault());
            final String uri = splitted[VValue.ONE].trim();
            return new JsonObject().put("method", method).put("uri", uri);
        } else {
            return new JsonObject();
        }
    }
}

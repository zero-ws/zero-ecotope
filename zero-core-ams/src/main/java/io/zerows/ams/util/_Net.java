package io.zerows.ams.util;

import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2023/4/28
 */
class _Net extends _Modeler {

    protected _Net() {
    }

    /**
     * 读取本机网络IPV4地址
     *
     * @return IPV4地址
     */
    public static String netIPv4() {
        return UNetwork.getIPv4();
    }

    /**
     * 读取本机网络中的主机名
     *
     * @return 主机名
     */
    public static String netHostname() {
        return UNetwork.getHostName();
    }

    /**
     * 读取本机网络中的IPV6地址
     *
     * @return IPV6地址
     */
    public static String netIPv6() {
        return UNetwork.getIPv6();
    }

    /**
     * 读取网络IP地址，系统自动检查 V4 或  V6
     *
     * @return IP地址
     */
    public static String netIP() {
        return UNetwork.getIP();
    }

    /**
     * 网络状态信息检查
     *
     * @param line 网络状态信息
     *
     * @return JsonObject
     */
    public static JsonObject netStatus(final String line) {
        return UNetwork.netStatus(line);
    }

    /**
     * 计算网络标识
     *
     * @param url 网络地址
     *
     * @return 网络标识
     */
    public static String netUri(final String url) {
        return UNetwork.netUri(url);
    }
}

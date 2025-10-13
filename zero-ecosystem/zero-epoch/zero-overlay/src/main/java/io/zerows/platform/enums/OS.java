package io.zerows.platform.enums;

import cn.hutool.core.util.StrUtil;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public enum OS {
    NA,             // NA
    MAC_OS,         // Mac Os
    UNIX,           // UNIX
    LINUX,          // LINUX
    WINDOWS;        // WINDOWS

    public static OS from(final String os) {
        if (StrUtil.isEmpty(os)) {
            return OS.NA;
        }
        if (os.startsWith("Windows")) {
            return OS.WINDOWS;
        } else if (os.startsWith("Linux")) {
            return OS.LINUX;
        } else if (os.startsWith("Mac")) {
            return OS.MAC_OS;
        } else {
            return OS.UNIX;
        }
    }
}

package io.zerows.enums.app;

import cn.hutool.core.util.StrUtil;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public enum OsType {
    NA,             // NA
    MAC_OS,         // Mac Os
    UNIX,           // UNIX
    LINUX,          // LINUX
    WINDOWS;        // WINDOWS

    public static OsType from(final String os) {
        if (StrUtil.isEmpty(os)) {
            return OsType.NA;
        }
        if (os.startsWith("Windows")) {
            return OsType.WINDOWS;
        } else if (os.startsWith("Linux")) {
            return OsType.LINUX;
        } else if (os.startsWith("Mac")) {
            return OsType.MAC_OS;
        } else {
            return OsType.UNIX;
        }
    }
}

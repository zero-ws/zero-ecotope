package io.zerows.core.util;

import io.zerows.epoch.support.UtBase;

/**
 * @author lang : 2023-06-19
 */
class _ED extends _Compare {
    /*
     * Encryption method for string
     * 1) encryptMD5
     * 2) encryptSHA256
     * 3) encryptSHA512
     *
     * 4.1) encryptBase64
     * 4.2) decryptBase64
     *
     * 5.1) encryptUrl
     * 5.2) decryptUrl
     *
     * 6.1) encryptRSAP / decryptRSAV ( Mode 1 )
     * 6.2) encryptRSAV / decryptRSAP ( Mode 2 )
     */

    public static String encryptBase64(final String username, final String password) {
        final String input = username + ":" + password;
        return UtBase.encryptBase64(input);
    }

    public static String encryptJ(final Object value) {
        return Jackson.encodeJ(value);
    }

    public static <T> T decryptJ(final String literal) {
        return Jackson.decodeJ(literal);
    }
}

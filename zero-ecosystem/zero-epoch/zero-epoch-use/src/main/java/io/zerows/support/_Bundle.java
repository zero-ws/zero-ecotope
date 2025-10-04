package io.zerows.support;

import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-17
 */
class _Bundle extends _Ai {

    public static class Bnd {


        public static String keyCache(final Bundle bundle, final Class<?> clazz) {
            return BundleInfo.keyCache(bundle, clazz);
        }
    }
}

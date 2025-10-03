package io.zerows.epoch.program;

import io.r2mo.function.Fn;
import io.zerows.platform.exception._11000Exception404SPINotFound;
import org.osgi.framework.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author lang : 2023-07-07
 */
class BundleSPI {

    static <T> T service(final Class<?> interfaceCls, final Bundle bundle) {
        final String name = nameSPI(interfaceCls, bundle);
        if (Ut.isNil(name)) {
            throw new _11000Exception404SPINotFound(BundleSPI.class);
        }
        return Fn.jvmOr(() -> {
            final Class<?> implClass = bundle.loadClass(name);
            return Ut.instance(implClass);
        });
    }

    private static String nameSPI(final Class<?> interfaceCls, final Bundle bundle) {
        final URL url = bundle.getEntry("META-INF/services/" + interfaceCls.getName());
        String serviceName = null;
        if (url != null) {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    s = s.trim();
                    // 查找第一个非 Empty 且没有被注释的行
                    if ((!s.isEmpty()) && (s.charAt(0) != '#')) {
                        serviceName = s;
                        break;
                    }
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
                throw new _11000Exception404SPINotFound(BundleSPI.class);
            }
        }
        return serviceName;
    }
}

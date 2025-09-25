package io.zerows.core.util;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.zerows.core.exception.BootingException;
import io.zerows.core.exception.WebException;
import io.zerows.core.exception.internal.SPINullException;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.spi.HorizonIo;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

/**
 * @author lang : 2023-07-07
 */
class BundleSPI {

    @SuppressWarnings("unchecked")
    static <T> Future<T> failOut(final Class<?> exceptionCls,
                                 final Class<?> target, final Object... args) {
        Objects.requireNonNull(exceptionCls);
        if (WebException.class.isAssignableFrom(exceptionCls)) {

            // Web
            return Future.failedFuture(failWeb((Class<? extends WebException>) exceptionCls, target, args));
        } else if (BootingException.class.isAssignableFrom(exceptionCls)) {

            // Booting
            return Future.failedFuture(failBoot((Class<? extends BootingException>) exceptionCls, target, args));
        } else {

            // 501
            return Future.failedFuture(failWeb(_501NotSupportException.class, BundleSPI.class));
        }
    }

    static <T extends WebException> WebException failWeb(final Class<T> exceptionCls,
                                                         final Class<?> target, final Object... args) {
        final WebException failure = failCommon(exceptionCls, target, args);
        final HorizonIo io = serviceIo(target);
        return failure.io(io);
    }

    static <T extends BootingException> BootingException failBoot(final Class<T> exceptionCls,
                                                                  final Class<?> target, final Object... args) {
        final BootingException failure = failCommon(exceptionCls, target, args);
        final HorizonIo io = serviceIo(target);
        return failure.io(io);
    }

    private static <T> T failCommon(final Class<T> exceptionCls,
                                    final Class<?> target, final Object... args) {
        final Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = target;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return Ut.instance(exceptionCls, newArgs);
    }

    static HorizonIo serviceIo(final Class<?> clazz) {
        final Bundle bundle = FrameworkUtil.getBundle(clazz);
        final HorizonIo io;
        if (Objects.isNull(bundle)) {
            io = Ut.service(HorizonIo.class);
        } else {
            io = Ut.Bnd.serviceSPI(HorizonIo.class, bundle);
        }
        return io;
    }

    static <T> T service(final Class<?> interfaceCls, final Bundle bundle) {
        final String name = nameSPI(interfaceCls, bundle);
        if (Ut.isNil(name)) {
            throw new SPINullException(BundleSPI.class);
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
                throw new SPINullException(BundleSPI.class);
            }
        }
        return serviceName;
    }
}

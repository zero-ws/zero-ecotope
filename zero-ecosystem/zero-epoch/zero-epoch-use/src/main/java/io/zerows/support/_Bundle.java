package io.zerows.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.sdk.osgi.OCommand;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.ServiceDependency;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-17
 */
class _Bundle extends _Ai {

    public static class Bnd {

        public static void commandBind(final BundleContext context,
                                       final Class<?> commandCls,
                                       final String... args) {
            BundleCmd.commandBind(context, commandCls, args);
        }

        public static OCommand commandBuild(final String value,
                                            final Map<String, OCommand> store) {
            return BundleCmd.commandBuild(value, store);
        }

        public static void commandRun(final Bundle bundle, final Consumer<Bundle> consumer) {
            BundleCmd.commandRun(bundle, consumer);
        }

        // OSGI 新接口
        public static <T> T serviceSPI(final Class<T> interfaceCls) {
            final Bundle bundle = FrameworkUtil.getBundle(interfaceCls);
            return BundleSPI.service(interfaceCls, bundle);
        }

        public static <T> T serviceSPI(final Class<T> interfaceCls, final Bundle bundle) {
            return BundleSPI.service(interfaceCls, bundle);
        }

        public static <T> T service(final Class<T> interfaceCls, final Bundle bundle) {
            return BundleService.service(interfaceCls, bundle);
        }

        public static <T> T serviceOr(final Class<T> interfaceCls, final Bundle bundle) {
            return Objects.isNull(bundle) ? Ut.service(interfaceCls) : BundleService.service(interfaceCls, bundle);
        }

        public static <T> List<T> serviceList(final Class<T> interfaceCls, final Bundle bundle) {
            return BundleService.serviceList(interfaceCls, bundle);
        }

        // OSGI IO 配置专用接口
        public static JsonObject ioDefaultJ(final String filename, final Class<?> clazz) {
            final Bundle bundle = FrameworkUtil.getBundle(clazz);
            return BundleIo.ioDefault(filename, bundle);
        }

        public static JsonObject ioDefaultJ(final String filename, final Bundle bundle) {
            return BundleIo.ioDefault(filename, bundle);
        }

        public static JsonObject ioCombineJ(final String filename, final Class<?> clazz) {
            final Bundle bundle = FrameworkUtil.getBundle(clazz);
            return BundleIo.ioCombine(filename, bundle);
        }

        public static JsonObject ioCombineJ(final String filename, final Bundle bundle) {
            return BundleIo.ioCombine(filename, bundle);
        }

        public static JsonObject ioConfigureJ(final String filename, final Class<?> clazz) {
            final Bundle bundle = FrameworkUtil.getBundle(clazz);
            return BundleIo.ioPriority(filename, bundle);
        }

        public static JsonObject ioConfigureJ(final String filename, final Bundle bundle) {
            return BundleIo.ioPriority(filename, bundle);
        }

        public static JsonObject ioJObject(final String filename, final Bundle bundle) {
            return BundleIo.ioJObject(filename, bundle);
        }

        public static JsonArray ioJArray(final String filename, final Bundle bundle) {
            return BundleIo.ioJArray(filename, bundle);
        }

        public static JsonObject ioYamlJ(final String filename, final Bundle bundle) {
            return BundleIo.ioYamlJ(filename, bundle);
        }

        public static JsonArray ioYamlA(final String filename, final Bundle bundle) {
            return BundleIo.ioYamlA(filename, bundle);
        }

        public static List<String> ioDirectory(final String directory, final Bundle bundle) {
            return BundleIo.ioDirectory(directory, bundle, false);
        }

        public static List<String> ioDirectoryN(final String directory, final Bundle bundle) {
            return BundleIo.ioDirectory(directory, bundle, true);
        }

        public static List<String> ioFile(final String directory, final Bundle bundle, final String extension) {
            return BundleIo.ioFile(directory, bundle, false, extension);
        }

        public static List<String> ioFileN(final String directory, final Bundle bundle, final String extension) {
            return BundleIo.ioFile(directory, bundle, true, extension);
        }

        public static JsonObject ioJObject(final String filename, final Bundle bundle, final String dir) {
            final String path = Ut.ioPath(dir, filename);
            return Objects.isNull(bundle) ? Ut.ioJObject(path) : BundleIo.ioJObject(path, bundle);
        }

        public static JsonArray ioJArray(final String filename, final Bundle bundle, final String dir) {
            final String path = Ut.ioPath(dir, filename);
            return Objects.isNull(bundle) ? Ut.ioJArray(path) : BundleIo.ioJArray(path, bundle);
        }

        public static boolean ioExist(final String filename, final Bundle bundle, final String dir) {
            final String path = Ut.ioPath(dir, filename);
            return Objects.isNull(bundle) ? Ut.ioExist(path) : Objects.nonNull(BundleIo.ioURL(path, bundle));
        }

        public static JsonObject ioYamlJ(final String filename, final Bundle bundle, final String dir) {
            final String path = Ut.ioPath(dir, filename);
            return Objects.isNull(bundle) ? Ut.ioYaml(path) : BundleIo.ioYamlJ(path, bundle);
        }

        public static JsonArray ioYamlA(final String filename, final Bundle bundle, final String dir) {
            final String path = Ut.ioPath(dir, filename);
            return Objects.isNull(bundle) ? Ut.ioYaml(path) : BundleIo.ioYamlA(path, bundle);
        }

        /**
         * 计算 Cache 专用的 key 值，包含两个维度
         * <pre><code>
         *     1. Bundle 的名称：bundle.getSymbolicName()
         *     2. Bundle 的版本：bundle.getVersion().getQualifier()
         * </code></pre>
         *
         * @param bundle Bundle
         *
         * @return Key
         */
        public static String keyCache(final Bundle bundle) {
            return BundleInfo.keyCache(bundle, true);
        }

        public static String keyCache(final Bundle bundle, final boolean version) {
            return BundleInfo.keyCache(bundle, version);
        }

        public static String keyCache(final Bundle bundle, final Class<?> clazz) {
            return BundleInfo.keyCache(bundle, clazz);
        }

        public static Component addDependency(final Component callback,
                                              final Supplier<ServiceDependency> serviceSupplier,
                                              final Class<?>... serviceClsArr) {
            return BundleInfo.addDependency(callback, serviceSupplier, serviceClsArr);
        }
    }
}

package io.zerows.core.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.BootingException;
import io.zerows.core.exception.WebException;
import io.zerows.core.spi.HorizonIo;
import io.zerows.module.metadata.zdk.running.OCommand;
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

        public static HorizonIo serviceIo(final Class<?> clazz) {
            return BundleSPI.serviceIo(clazz);
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

        // --------------------- Exception
        public static <T extends WebException> WebException failWeb(
            final Class<T> exceptionCls, final Class<?> target, final Object... args) {
            return BundleSPI.failWeb(exceptionCls, target, args);
        }

        public static <T extends BootingException> BootingException failBoot(
            final Class<T> exceptionCls, final Class<?> target, final Object... args) {
            return BundleSPI.failBoot(exceptionCls, target, args);
        }

        /**
         * 此处的特殊性是不可以带 boolean 的检查参数，通常外层可能为
         * <pre><code>
         *     if(condition){
         *         return {@link Future#failedFuture(Throwable)};
         *     }
         * </code></pre>
         * 上述代码流程中只是截断返回，而不是截断抛出异常，基于此设计，此处的方法不包含 boolean 参数，不可以像 outWeb 那种模式直接
         * throw 抛出，抛出异常在异步数据流中无意义。
         * <p>
         * 简化的代码部分：
         * <pre><code>
         *     1. 形态1：
         *        final WebException error = Ut.Bnd.failWeb(_404MobileNotFoundException.class, this.getClass(), mobile);
         *        return Future.failedFuture(error);
         *     2. 形态2：
         *        return Future.failedFuture(
         *            Ut.Bnd.failWeb(_401ImageCodeWrongException.class, this.getClass(), imageCode)
         *        );
         *     3. 旧版：
         *        return RFn.outWeb(_400SigmaMissingException.class, HHotel.class);
         * </code></pre>
         * 注：这两个方法的区别
         * <pre><code>
         *     1. {@see RFn#outWeb(Class, Object...)}()}，此方法不支持 OSGI 环境，旧版异步流中只有一个方法返回了 Future，基于此
         *        未来所有版本都会替换成 {@link Future} 的形式，所以旧版会直接 @Deprecated 掉
         *     2. 当前方法支持 OSGI 环境，可在 OSGI 模块化环境中直接使用。
         *     3. 当前方法除了 {@link WebException} 以外，还支持 {@link BootingException}，操作的异常类型更全。
         * </code></pre>
         * 旧版中的 outWeb / outBoot / out 三个核心方法是抛出异常，此异常依旧保留，虽然在 OSGI 环境中可能会有一定的兼容问题，但依旧可以
         * 在异常发生的时候再执行相关重构，所以如此就统一了对应的异常处理。
         * 总结起来参考等价方法
         * <pre><code>
         *     旧环境                      新环境（带 OSGI）
         *     RFn.out
         *     RFn.outWeb                  throw Ut.Bnd.failWeb
         *     RFn.outBoot                 throw Ut.Bnd.failBoot
         *     Ut.failWeb                 Ut.Bnd.failWeb
         *     （异步）                    Ut.Bnd.failOut
         * </code></pre>
         * 异步模式不再提供旧方法（高频编程位置），毕竟 throw 抛异常的模式是位于旧版的启动流程中最多，而不需要让这些异常带有特定的意义存在，
         * 所以直接使用 RFn.out??? 的方式构造也无关结果。
         *
         * @param exceptionCls 异常类，必须是 WebException 或 BootingException，其他异常转换成 501
         * @param target       目标类，一般是 getClass() 结果或当前类
         * @param args         参数表
         * @param <T>          主要配合外层的 <T> 定义
         *
         * @return 异步结果 Monad
         */
        public static <T> Future<T> failOut(final Class<?> exceptionCls,
                                            final Class<?> target, final Object... args) {
            return BundleSPI.failOut(exceptionCls, target, args);
        }
    }
}

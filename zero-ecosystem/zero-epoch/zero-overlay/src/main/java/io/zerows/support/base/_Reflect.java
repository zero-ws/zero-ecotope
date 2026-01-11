package io.zerows.support.base;

import io.r2mo.SourceReflect;
import io.vertx.core.Future;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author lang : 2023/4/28
 */
class _Reflect extends _Random {
    protected _Reflect() {
    }
    
    /**
     * 判断 implCls 是否实现了 interfaceCls 接口，或者是否是 interfaceCls 的子类
     * 若 implCls 不是父类，则递归检索父类直到最终的 Object 类
     *
     * @param implCls      实现类
     * @param interfaceCls 接口类
     * @return 是否实现
     */
    public static boolean isImplement(final Class<?> implCls, final Class<?> interfaceCls) {
        return SourceReflect.isImplement(implCls, interfaceCls);
    }

    /**
     * 判断 findRunning 是否是一个类
     *
     * @param value 值
     * @return 是否是类
     */
    public static boolean isClass(final Object value) {
        return TType.isClass(value);
    }

    /**
     * 根据类名在系统中反射查找该类，构造 Class<?> 对象
     * 整个流程分两步
     * 1. 如果找到的该类是 null，则使用默认的 instanceCls 返回
     * 2. 如果找到该类则使用其操作。
     * 为了让类加载器实现模块级操作，可传入额外的类加载器，优先从该类加载器中查找
     * <pre><code>
     * - loader
     * - Thread.currentThread().getContextClassLoader()
     * - ClassLoader.getSystemClassLoader()
     * - ClassLoader.getPlatformClassLoader()
     * </code></pre>
     *
     * @param className   类名
     * @param instanceCls 默认的类
     * @param loader      类加载器
     * @return Class<?> 对象
     */
    public static Class<?> clazzBy(final String className, final Class<?> instanceCls,
                                   final ClassLoader loader) {
        return SourceReflect.clazz(className, instanceCls, loader);
    }


    /**
     * 根据类名在系统中反射查找该类，构造 Class<?> 对象
     * 整个流程分两步
     * 1. 如果找到的该类是 null，则使用默认的 instanceCls 返回
     * 2. 如果找到该类则使用其操作。
     * 为了让类加载器实现模块级操作，可传入额外的类加载器，优先从该类加载器中查找
     * <pre><code>
     * - loader
     * - Thread.currentThread().getContextClassLoader()
     * - ClassLoader.getSystemClassLoader()
     * - ClassLoader.getPlatformClassLoader()
     * </code></pre>
     * <p>
     * 改名的主要目的是旧系统和模块应用中的调用会有冲突
     *
     * @param className 类名
     * @param loader    类加载器
     * @return Class<?> 对象
     */
    public static Class<?> clazzBy(final String className, final ClassLoader loader) {
        return SourceReflect.clazz(className, null, loader);
    }

    /**
     * 根据类名在系统中反射查找该类，构造 Class<?> 对象
     * 整个流程分两步
     * 1. 如果找到的该类是 null，则使用默认的 instanceCls 返回
     * 2. 如果找到该类则使用其操作。
     * 为了让类加载器实现模块级操作，可传入额外的类加载器，优先从该类加载器中查找
     * <pre><code>
     * - loader
     * - Thread.currentThread().getContextClassLoader()
     * - ClassLoader.getSystemClassLoader()
     * - ClassLoader.getPlatformClassLoader()
     * </code></pre>
     *
     * @param className   类名
     * @param instanceCls 默认的类
     * @return Class<?> 对象
     */
    public static Class<?> clazz(final String className, final Class<?> instanceCls) {
        return SourceReflect.clazz(className, instanceCls);
    }

    /**
     * 根据类名在系统中反射查找该类，构造 Class<?> 对象
     * 整个流程分两步
     * 1. 如果找到的该类是 null，则使用默认的 instanceCls 返回
     * 2. 如果找到该类则使用其操作。
     * 为了让类加载器实现模块级操作，可传入额外的类加载器，优先从该类加载器中查找
     * <pre><code>
     * - loader
     * - Thread.currentThread().getContextClassLoader()
     * - ClassLoader.getSystemClassLoader()
     * - ClassLoader.getPlatformClassLoader()
     * </code></pre>
     *
     * @param className 类名
     * @return Class<?> 对象
     */
    public static Class<?> clazz(final String className) {
        return SourceReflect.clazz(className);
    }

    /**
     * 根据类名在系统中反射查找该类，并构造该类对应的实例，转换成T
     *
     * @param clazz  类名
     * @param params 构造参数
     * @param <T>    T
     * @return T
     */
    public static <T> T instance(final Class<?> clazz, final Object... params) {
        return SourceReflect.instance(clazz, params);
    }

    /**
     * 根据类名在系统中反射查找该类，并构造该类对应的实例，转换成T
     *
     * @param className 类名
     * @param params    构造参数
     * @param <T>       T
     * @return T
     */
    public static <T> T instance(final String className, final Object... params) {
        return SourceReflect.instance(clazz(className), params);
    }

    /**
     * （单例）根据类名在系统中反射查找该类，并构造该类对应的实例，转换成T
     *
     * @param className 类名
     * @param params    构造参数
     * @param <T>       T
     * @return T
     */
    public static <T> T singleton(final String className, final Object... params) {
        return SourceReflect.singleton(clazz(className), params);
    }

    /**
     * （单例）根据类名在系统中反射查找该类，并构造该类对应的实例，转换成T
     *
     * @param clazz  类名
     * @param params 构造参数
     * @param <T>    T
     * @return T
     */
    public static <T> T singleton(final Class<?> clazz, final Object... params) {
        return SourceReflect.singleton(clazz, params);
    }

    /**
     * 根据提供信息查找构造函数
     *
     * @param clazz  类
     * @param params 构造参数
     * @param <T>    T
     * @return 构造函数
     */
    public static <T> Constructor<T> constructor(final Class<?> clazz, final Object... params) {
        return UInstance.constructor(clazz, params);
    }

    /**
     * 检查传入类是否带有默认无参构造函数
     *
     * @param clazz 类
     * @return 是否带有默认无参构造函数
     */
    public static boolean isDefaultConstructor(final Class<?> clazz) {
        return UInstance.isDefaultConstructor(clazz);
    }

    /**
     * 设置某个对象的成员属性值
     *
     * @param instance 对象
     * @param name     属性名
     * @param value    属性值
     * @param <T>      属性值类型
     */
    public static <T> void field(final Object instance, final String name, final T value) {
        SourceReflect.value(instance, name, value);
    }

    /**
     * 获取某个对象的成员属性值
     *
     * @param instance 对象
     * @param field    属性对象
     * @param <T>      属性值类型
     */
    public static <T> void field(final Object instance, final Field field, final T value) {
        SourceReflect.value(instance, field, value);
    }

    /**
     * 获取某个对象的成员属性值
     *
     * @param instance 对象
     * @param name     属性名
     * @param <T>      属性值类型
     * @return 属性值
     */
    public static <T> T field(final Object instance, final String name) {
        Objects.requireNonNull(instance);
        return SourceReflect.value(instance, name);
    }

    /**
     * 获取某个类中的 static 常量或变量，可直接提取
     * 1. 接口常量
     * 2. 静态公有 / 私有常量
     *
     * @param interfaceCls 接口类
     * @param name         常量名
     * @param <T>          常量值类型
     * @return 常量值
     */
    public static <T> T field(final Class<?> interfaceCls, final String name) {
        return SourceReflect.value(interfaceCls, name);
    }

    /**
     * 实例方法调用
     *
     * @param instance 实例
     * @param name     方法名
     * @param args     参数
     * @param <T>      返回值类型
     * @return 返回值
     */
    public static <T> T invoke(final Object instance, final String name, final Object... args) {
        return UInvoker.invokeObject(instance, name, args);
    }

    /**
     * 静态方法调用
     *
     * @param clazz 类
     * @param name  方法名
     * @param args  参数
     * @param <T>   返回值类型
     * @return 返回值
     */
    public static <T> T invokeStatic(final Class<?> clazz, final String name, final Object... args) {
        return UInvoker.invokeStatic(clazz, name, args);
    }

    /**
     * 实例方法调用（异步版本）
     *
     * @param instance 实例
     * @param method   方法
     * @param args     参数
     * @param <T>      T
     * @return Future
     */
    public static <T> Future<T> invokeAsync(final Object instance, final Method method, final Object... args) {
        return UInvoker.invokeAsync(instance, method, args);
    }
}

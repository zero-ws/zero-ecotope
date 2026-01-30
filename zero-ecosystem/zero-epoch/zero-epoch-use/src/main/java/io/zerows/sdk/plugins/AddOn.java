package io.zerows.sdk.plugins;

import com.google.inject.Key;
import io.zerows.specification.configuration.HActor;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

/**
 * 配合 {@link HActor} 的插件专用接口，整个插件模式的设计是为了配合两种场景
 * <pre>
 *     1. 开发人员的 DI 依赖注入场景
 *     2. 普通启动场景下提取 默认 的对象：Singleton（单例模式）
 * </pre>
 * 整体架构
 * <pre>
 *     内置 {@link DI} 依赖注入对象的 Manager
 *     Manager 本身是单例模式，且负责其中 DI 对象的生命周期，包含一个默认的 DI 名称，内部维护一个哈希表
 *        {@link HActor}     ->    Manager ( 单例 )
 *                           ->    {@link AddOn} ( 单例 ) 插件切入口 -> {@link #getKey()}
 *     ------------------------------------------------------------------------------------------
 *                           ->    {@link Provider}  -> {@link #createInstance()}
 *                                                   -> {@link Named} -> {@link #createInstance(String)}
 *                                                   -> {@link Singleton} -> {@link #createSingleton()}
 * </pre>
 * <p>
 * 此处的 {@link DI} 就是实际所需的对象类型，也是最终使用 {@link Inject} 注解进行依赖注入的对象。
 *
 * @author lang : 2025-10-14
 */
public interface AddOn<DI> {
    /**
     * 传给 ID 容器的 Key 对象，此处的 Key 对象有两种形态
     * <pre>
     *     1. 带有 {@link Named} 注解的 Key 对象    -> 多个实例创建
     *     2. 不带有 {@link Named} 注解的 Key 对象  -> 单例模式
     * </pre>
     *
     * @return Key 对象
     */
    Key<DI> getKey();


    /**
     * 使用场景
     * <pre>
     *     1. 在注入模式下，若定义了 {@link Singleton}，则使用此方法进行实例创建。
     *     2. 在普通模式下，直接采用 Xxx.of().createSingleton() 创建默认对象。
     * </pre>
     * 虽然是默认对象，单实际在底层有可能是使用了全局的 Map，所以这种场景下不论哪个对象访问的都是统一对象，因此
     * 可保证此处 Singleton 的语义以及它的唯一性。
     *
     * @return 单例对象
     */
    DI createSingleton();


    /**
     * 除开单例模式的名称使用了默认名称以外，其他名称都使用的是随机名称，确保每次调用都是不同的对象。
     *
     * @return 新建对象
     */
    default DI createInstance() {
        return this.createInstance(UUID.randomUUID().toString());
    }

    DI createInstance(String name);

    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface Name {
        String value() default "";
    }
}

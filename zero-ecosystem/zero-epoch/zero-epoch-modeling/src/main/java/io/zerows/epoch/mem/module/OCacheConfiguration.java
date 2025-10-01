package io.zerows.epoch.mem.module;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.corpus.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.configuration.module.children.MDPage;
import io.zerows.epoch.corpus.configuration.module.modeling.MDConnect;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.running.OCache;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 配置管理器，用于管理基本配置信息，在 OSGI 环境中安装时直接传入 URL 来完成配置的加载，此加载流程在不同环境中会有所区别
 * <pre><code>
 *     1. Norm 环境
 *        标准环境中，配置信息直接在启动时执行加载，所有扩展模块直接将对应配置添加到环境中即可，每个模块一个：
 *        - 启动器模块一个
 *        - 自定义模块一个
 *        - Extension 扩展模块一个
 *     2. Osgi 环境
 *        热插拔环境中，配置信息每个 Bundle 一个，直接根据 Bundle 对应的名称 / 版本信息来计算配置信息
 * </code></pre>
 *
 * @author lang : 2024-05-07
 */
public interface OCacheConfiguration extends OCache<MDConfiguration> {

    Cc<String, OCacheConfiguration> CC_SKELETON = Cc.open();


    static OCacheConfiguration of(final Bundle bundle) {
        return CC_SKELETON.pick(() -> new OCacheConfigurationAmbiguity(bundle),
            Ut.Bnd.keyCache(bundle, OCacheConfigurationAmbiguity.class));
    }

    static OCacheConfiguration of() {
        return of(null);
    }

    /**
     * 是否已经初始化，如果已经初始化则不再执行第二次，此处 id 有两个值
     * <pre><code>
     *     1. Norm 环境中为直接构造时传入的值，一般是目录名：plugins/<id>/，这种场景中的 id 手动赋值
     *     2. Osgi 环境中为 Bundle 的 SymbolicName 信息，也会对应目录名：plugins/<id>/
     * </code></pre>
     * 此方法最终会保证不论在哪种环境中，对应的配置信息都只会加载一次，不执行重复加载，一般加载流程是在初始化流程中执行的，一旦执行过后就不再
     * 执行第二次，两种机制都会保证在完整启动环境中直接加载的基础配置信息，流程在不同环境中会有所区别：
     * <pre><code>
     *     1. Norm 环境
     *        - 直接启动过程中会调用 {@link OCacheDao} 中的初始化方法，直接将扫描的配置信息加载到环境中，扫描信息针对
     *          Pojo / Dao / Table
     *        - 启动完成之后所有的 plugins 之下的目录都会自动生成对应的 {@link MDConfiguration}，每个配置信息都回对应一个模块，此时
     *          模块本身并不是 Bundle，即纯配置信息，这种场景之下的配置信息依靠 plugins/<id>/ 执行区分，不同的模块对应的配置信息放在不同
     *          的目录中完成。
     *        - 最终提供的配置信息如下：
     *          - 启动专用配置：{@link MDConfiguration}
     *          - 扩展模块专用配置：module-01 = {@link MDConfiguration}
     *                           module-02 = {@link MDConfiguration}
     *                           module-03 = ...
     *
     *      2. Osgi 环境
     *        - OSGI 环境在每个模块启动时会根据 {@link Bundle} 对应的 SymbolicName 信息来计算配置信息，每个模块对应一个配置信息，
     *          最终会将信息存储在 {@link OCacheConfiguration} 中，如此执行之后，每个 Bundle 都会拥有自己独立的配置，且可以执行热插拔
     *          管理。
     *        - 最终提供的配置信息如下：
     *          - 启动专用配置：{@link MDConfiguration}，修改 vertx-excel.yml 文件将对应的 MDConnect 配置直接放到外联处理中，对接
     *            {@link HSetting} 实现启动程序的配置对齐，启动程序会比普通的 Bundle 多一份 {@link HSetting} 配置，形成一个整体意义
     *            上的包含关系
     *            {@link HSetting} + {@link MDConfiguration} 来构造完整配置信息
     *            BundleType = APP
     *          - 扩展模块专用配置：bundle-01 = {@link MDConfiguration}
     *                           bundle-02 = {@link MDConfiguration}
     *                           bundle-03 = ...
     *            BundleType = PLUGIN
     *       3. BundleType = APP / PLUGIN
     *          APP 类型的 Bundle 会多出应用程序配置信息，应用程序配置和基础环境中的启动器配置对齐，最终配置表如下
     *          - 启动/应用配置 {@link HSetting}
     *          - Bundle 配置 {@link MDConfiguration}
     * </code></pre>
     *
     * @param id 配置 id
     *
     * @return 是否已经初始化
     */
    static boolean initialized(final String id) {
        return CC_SKELETON.get().values().stream()
            .anyMatch(meta -> Objects.nonNull(meta.valueGet(id)));
    }

    // ----------------- 全局方法，用于提取全局扫描的所有 MDConnect
    static MDConnect entireConnect(final String tableOr) {
        return CC_SKELETON.get().values().stream()
            .flatMap(meta -> meta.valueSet().stream())
            .map(meta -> meta.inConnect(tableOr))
            .filter(Objects::nonNull)
            .findAny().orElse(null);
    }

    static Set<MDConnect> entireConnect() {
        return CC_SKELETON.get().values().stream()
            .flatMap(meta -> meta.valueSet().stream())
            .flatMap(meta -> meta.inConnect().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    static ConcurrentMap<String, MDPage> entireWeb() {
        return CC_SKELETON.get().values().stream()
            .flatMap(meta -> meta.valueSet().stream())
            .flatMap(meta -> meta.inWeb().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toConcurrentMap(MDPage::key, item -> item));
    }

    Set<MDConfiguration> valueSet();
}

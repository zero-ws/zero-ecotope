package io.zerows.epoch.configuration;

import io.zerows.epoch.boot.ZeroLauncher;
import io.zerows.platform.enums.EmBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.storage.HStoreLegacy;
import io.zerows.spi.BootIo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心配置存储，专用于存储启动配置，替换原始组件部分
 * <pre><code>
 *     1. 核心框架初始化部分
 *     2. Extension 扩展框架的 Pin 部分
 *     3. Scan 的类结果部分
 * </code></pre>
 * 高阶部分的核心接口
 * <pre><code>
 *     1. {@link HConfig}，组件配置接口
 *     2. {@link ZeroEnergy} 启动配置接口
 *     3. {@link ZeroLauncher} 启动器接口
 *     4. {@link BootIo} 启动选择器 / 组件加载器
 * </code></pre>
 * 此部分底层还可以走一个特殊的 {@link HStoreLegacy}，然后从 HStore
 * 中提取配置数据部分，这样可以实现配置数据的存储，而不是直接存储在内存中。完整的结构如：
 * <pre><code>
 *     1. stored：容器配置
 *     2. launcher：启动器配置
 *     3. extension：Zero Extension扩展模块配置
 *     4. infix：Infix架构下的插件配置
 *     5. registry：注册表，注册表中保存了应该有的配置路径
 * </code></pre>
 *
 * @author lang : 2023-05-30
 */
public class ZeroSetting implements HSetting {
    /** 扩展配置部分 **/
    private final ConcurrentMap<String, HConfig> extension = new ConcurrentHashMap<>();
    /** 插件配置 **/
    private final ConcurrentMap<String, HConfig> infix = new ConcurrentHashMap<>();
    /** 生命周期组件配置 **/
    private final ConcurrentMap<EmBoot.LifeCycle, HConfig> boot = new ConcurrentHashMap<>();
    /** 容器主配置 */
    private HConfig container;
    /** 启动器配置 **/
    private HConfig launcher;

    private String idOrName;

    private ZeroSetting() {
    }

    public static ZeroSetting of() {
        return new ZeroSetting();
    }

    @Override
    public HConfig container() {
        return this.container;
    }

    public HSetting container(final HConfig container) {
        this.container = container;
        return this;
    }

    public HSetting id(final String idOrName) {
        this.idOrName = idOrName;
        return this;
    }

    @Override
    public String id() {
        return this.idOrName;
    }

    @Override
    public HConfig boot(final EmBoot.LifeCycle lifeCycle) {
        return this.boot.get(lifeCycle);
    }

    public HSetting boot(final EmBoot.LifeCycle lifeCycle, final HConfig config) {
        this.boot.put(lifeCycle, config);
        return this;
    }

    @Override
    public HConfig launcher() {
        return this.launcher;
    }

    public HSetting launcher(final HConfig launcher) {
        this.launcher = launcher;
        return this;
    }

    public HSetting extension(final String name, final HConfig config) {
        this.extension.put(name, config);
        return this;
    }

    @Override
    public HConfig extension(final String name) {
        return this.extension.get(name);
    }

    public HSetting infix(final String name, final HConfig config) {
        this.infix.put(name, config);
        return this;
    }

    @Override
    public HConfig infix(final String name) {
        return this.infix.get(name);
    }

    @Override
    public boolean hasInfix(final String name) {
        return this.infix.containsKey(name);
    }
}
package io.zerows.specification.configuration;

/**
 * 「模块启动器」模块启动专用接口
 * 辅助启动器，使用 OSGI 模式启动模块，内置启动容器类型为泛型，同 {@link HLauncher} 一样可直接完成启动之前的动作
 * 此启动器更契合在启动容器过程中的 Bundle 生命周期处理，主要包括：
 * <pre><code>
 *     - configure：注册模块之前的配置预处理，配置完成后 Resolved
 *     - install：安装模块, 完成之后 Installed
 *     - start：启动模块（激活）
 *     - stop：停止模块
 *     - unregister：注销模块
 *     - cleanup: 清除数据、验证
 * </code></pre>
 * OSGI 中的 Bundle 的生命周期如下：
 * <pre><code>
 *               |
 *               v
 *     <----- INSTALLED
 *     |         |
 *     |         |
 *     |         v
 *     |      RESOLVED --> STARTING --> ACTIVE --> STOPPING
 *     |         |   ^                                 |
 *     |         |   |                                 |
 *     |         |   ----------------------------------|
 *     |         v
 *     |----> UNINSTALLED
 * </code></pre>
 *
 * @author lang : 2023-05-25
 */
public interface HInstall<BND> {
    default void configure(final BND bundle) {
    }

    default void install(final BND bundle) {
    }

    void start(BND bundle);

    void stop(BND bundle);

    default void unregister(final BND bundle) {
    }

    default void cleanup(final BND bundle) {
    }

    /**
     * 「装配器」专用于配置读写
     * 1. 目录固定（每个目录固定一个HFit组件）
     * 2. 每个组件搭载
     * -- IO缓存（分布式）
     * -- 数据缓存（分布式）
     * -- Git区域（分布式）
     * 3. 提供基础API方法
     * -- 配置读取
     * ---- 单配置
     * ---- 多配置
     * ---- 分页/分组
     * -- 配置写入
     * ---- 单配置
     * ---- 查找型单配置
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HFit {

    }
}

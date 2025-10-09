package io.zerows.specification.configuration;

import io.r2mo.typed.annotation.SPID;
import io.zerows.platform.enums.EmApp;

/**
 * 基于启动配置存储专用配置器，内置包含
 * <pre><code>
 *     1. {@link HBoot} 本次启动详细信息
 *        - {@link HEnergy} 能量配置，系统加载的内容
 *        - {@link Class} 启动器类
 *        - {@link Class} 运行程序的主类
 *        - {@link String[]} 当前启动时的运行参数
 *     2. {@link HEnergy} 能量配置，系统加载的内容
 *        - {@link EmApp.Mode} 应用模式
 *        - {@link EmApp.LifeCycle} 生命周期
 * </code></pre>
 *
 * @author lang : 2023-05-31
 */
@Deprecated
public interface HStation {
    /**
     * 返回核心启动配置
     *
     * @return {@link HBoot}
     */
    HBoot boot();

    /**
     * 绑定启动相关信息，此处之所以要有一个绑定启动器的过程，在于启动器的选择会经历上层计算，其中包括
     * <pre>
     *     1. 启动参数的输入
     *     2. 优先级运算
     *     3. 自定义启动器的设置
     * </pre>
     * 上述结束之后才会确认最终启动器，所以此处从 {@link HBoot} 中分离出来，兼容静态配置和动态配置的差异——只有不存在
     * 输入配置的场景下，才考虑静态配置，否则一切以动态配置为主，此处是优先级的核心运算，新版只需要绑定参数即可，因为
     * 启动器已经直接依赖 {@link SPID} 中的优先级设定来完成启动器的选择，无需再做额外的计算，但启动参数仍然需要绑定，
     * 启动参数决定了当前配置的运行模式。
     *
     * @param mainClass 主类
     * @param args      启动参数
     *
     * @return {@link HStation}
     */
    HStation bind(Class<?> mainClass, String[] args);
}

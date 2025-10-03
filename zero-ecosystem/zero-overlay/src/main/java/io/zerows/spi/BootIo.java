package io.zerows.spi;

import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.configuration.HEnergy;

/**
 * 「主配置入口规范」BootIo
 *
 * <p>面向不同平台/产品线（如：Aeon、SMAVE 等）的一致化启动配置抽象。该接口用于：</p>
 * <ul>
 *   <li>🚀 <b>提供启动器</b>：返回底层容器/框架的 {@link HLauncher} 实例，以便完成真正的启动流程；</li>
 *   <li>⚡ <b>解析能量配置</b>：基于启动类与命令行参数组装 {@link HEnergy}（统一的配置/上下文“能量包”）。</li>
 * </ul>
 *
 * <h2>🧭 设计背景与目录规范</h2>
 * <p>不考虑 Zero Framework 自身的内部启动细节（其会直接执行 {@link HLauncher} 流程），
 * 但统一遵循核心目录与配置约定。不同平台（如 Aeon 平台、SMAVE 平台）可拥有各自的基础配置文件规范，
 * 但都应映射到统一的启动入口与能量配置上。统一配置键值与路径等常量定义建议集中在
 * <code>VBoot</code>（参见 {@see VBoot}）。</p>
 *
 * <h2>🧩 使用约定</h2>
 * <ul>
 *   <li>🔎 <b>发现机制</b>：推荐通过 SPI（如：{@code java.util.ServiceLoader} 或项目内 {@code SPI.findOne}）发现 {@code BootIo} 实现；</li>
 *   <li>🧱 <b>无状态实现</b>：建议实现类保持无状态或仅持有只读元信息，以便被重复复用；</li>
 *   <li>🧵 <b>线程模型</b>：接口本身不做并发保证；若实现类内部存在状态，请自行保证线程安全；</li>
 *   <li>📂 <b>配置来源</b>：实现可从类路径、外部目录、环境变量、系统属性、命令行参数等多源合并为 {@link HEnergy}；</li>
 *   <li>🧪 <b>校验与容错</b>：建议在能量装配阶段进行必要的校验（必填项、目录存在性等），并提供清晰的异常/日志。</li>
 * </ul>
 *
 * <h2>💡 典型调用示例</h2>
 * <pre>{@code
 * // 通过 SPI 查找 BootIo 实现
 * BootIo bootIo = SPI.findOne(BootIo.class);
 *
 * // 1) 解析能量配置（基于启动类与 args）
 * HEnergy energy = bootIo.energy(MyBoot.class, args);
 *
 * // 2) 获取启动器并启动
 * HLauncher<MyServer> launcher = bootIo.launcher();
 * launcher.start( /* HOn/onComponent * /, server -> {
 *     // server: 已初始化的核心实例
 *     // TODO: 启动完成后的业务初始化
 * });
 * }</pre>
 *
 * <h2>⚠️ 异常与日志建议</h2>
 * <ul>
 *   <li>❌ 未发现实现：上层（如 KLauncher）应在 SPI 未找到实现时抛出清晰的异常（例如：{@code _11010Exception500BootIoMissing}）。</li>
 *   <li>📝 日志：在能量装配、路径解析、配置合并等关键节点输出可定位问题的日志。</li>
 * </ul>
 *
 * @author lang
 * @since 2023-05-30
 */
public interface BootIo {

    /**
     * 提供主启动器实例，用于驱动当前容器/框架的启动流程。
     *
     * <p>🎯 语义：抽象“如何启动”，屏蔽不同底层实现的差异（如 Vert.x、Netty、自研容器等）。</p>
     *
     * @param <T> 启动后返回的核心实例类型（例如：服务器对象、容器句柄等），由具体 {@link HLauncher} 实现决定 🧩
     *
     * @return 可用的 {@link HLauncher} 启动器实例（不应为 {@code null}） 🚀
     */
    <T> HLauncher<T> launcher();

    /**
     * 基于启动类与命令行参数解析并装配“能量配置” {@link HEnergy}。
     *
     * <p>🔌 语义：抽象“配好再启动”。该方法负责从多渠道（类路径、外部配置、环境变量等）
     * 汇聚与校验启动所需的<strong>最小必要配置</strong>与<strong>扩展配置</strong>。</p>
     *
     * @param bootCls 启动类（通常用于定位配置根、计算扫描路径、确定归属应用等） 📌
     * @param args    启动参数（通常会合并进 {@link HEnergy}，并在上层注入到 {@code HConfig} 的 {@code "arguments"} 字段） 🧵
     *
     * @return 组装完成且可用于后续流程的 {@link HEnergy} 实例（不应为 {@code null}） ⚡
     */
    HEnergy energy(Class<?> bootCls, String[] args);
}

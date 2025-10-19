package io.zerows.spi;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.management.StoreSetting;
import io.zerows.specification.configuration.HBoot;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.configuration.HSetting;

import java.util.function.Consumer;

/**
 * 「主配置入口规范」BootIo
 *
 * <p>面向不同平台/产品线（如：Aeon、SMAVE 等）的一致化启动配置抽象。该接口用于：</p>
 * <ul>
 *   <li>🚀 <b>提供启动器</b>：返回底层容器/框架的 {@link HLauncher} 实例，以便完成真正的启动流程；</li>
 *   <li>⚡ <b>解析能量配置</b>：基于启动类与命令行参数组装 {@link HEnergy}（统一的配置/上下文"能量包"）。</li>
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
 *   <li>🔎 <b>发现机制</b>：推荐通过 SPI（如：{@code java.util.ServiceLoader} 或项目内 {@code SPI.findOneAsync}）发现 {@code BootIo} 实现；</li>
 *   <li>🧱 <b>无状态实现</b>：建议实现类保持无状态或仅持有只读元信息，以便被重复复用；</li>
 *   <li>🧵 <b>线程模型</b>：接口本身不做并发保证；若实现类内部存在状态，请自行保证线程安全；</li>
 *   <li>📂 <b>配置来源</b>：实现可从类路径、外部目录、环境变量、系统属性、命令行参数等多源合并为 {@link HEnergy}；</li>
 *   <li>🧪 <b>校验与容错</b>：建议在能量装配阶段进行必要的校验（必填项、目录存在性等），并提供清晰的异常/日志。</li>
 * </ul>
 *
 * <h2>💡 典型调用示例</h2>
 * <pre>{@code
 * // 通过 SPI 查找 BootIo 实现
 * BootIo bootIo = SPI.findOneAsync(BootIo.class);
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
 * {@link HBoot} 和 {@link HEnergy} 的协同工作原理
 * <pre>
 *     ZeroLauncher -> {@link BootIo} -> {@link HBoot} ( 启动组件集 )
 *                                        - {@link EmApp.LifeCycle} = Class<?>
 *                                    -> {@link HEnergy} ( 完整配置集 )
 *                                        - {@link EmApp.LifeCycle} = {@link HConfig}
 *                                                                     -> {@link HSetting} 提供最小单元配置
 *                                                                          {@link EmApp.LifeCycle} = {@link HConfig}
 *                                                                          {@link EmApp.Mode}       = {@link HConfig}
 *                                                                     -> {@see YmConfiguration} -> {@link HSetting}
 *                                       其他组件的能量配置集
 *                                       {@link HEnergy}
 *                                        - configKey = {@link HConfig}, 此处 configKey 使用唯一计算法则，不同的 configKey 应用不同的配置
 *                                          {YmConfiguration} 和 {@link HSetting} 的区别在于 {@link HSetting} 会拉平配置，不论深度如何的
 *                                          Yml 文件都会直接被 {@link HSetting} 拉平（自定义配置除外）
 *                                                                     -> {@link StoreSetting} 管理 {@link HSetting}
 *                                                                        一层：app-name = {@link HSetting}
 *                                                                             二层：configKey = {@link HConfig}
 *                 -> 启动流程
 *                    1. {@link HLauncher#start} 启动 -> 构造 {@link Vertx} 实例
 *                    2. {@link HBoot#withPre} 启动 -> 预处理阶段，基于 {@link Vertx} 执行
 *                       {@link HBoot#whenOn}  启动 -> 构造启动配置
 *                       此时 withPre 和 whenOn 是双设计模型
 *                       1）withPre -> 负责预处理内置组件、非业务相关组件、所有被依赖的组件
 *                                     存在多个依赖流程时直接执行链式依赖处理
 *                       2）whenOn  -> 构造启动配置，为第三步做准备
 *                    3. {@link HLauncher#start(HConfig.HOn, Consumer)} 中的 {@link Consumer} 被触发，此处 {@link Consumer} 充当了启动
 *                       回调，开发过程中自定义的部分内容在此处被触发完成
 *                    4. {@link Consumer} 执行过程中依旧可以访问 {@link StoreSetting}，通过 configKey 提取 HConfig 配置信息
 *                       HConfig 的主结构
 *                        - options = {@link JsonObject} 配置信息
 *                        - executor = Map ( name = Class ) 的组件信息
 * </pre>
 *
 * @author lang
 * @since 2023-05-30
 */
public interface BootIo {

    /**
     * 🏗️ 构建启动配置组件
     * <pre>
     *     🎯 设计意图：
     *     - 📌 基于主启动类构建完整的启动配置组件
     *     - 🏷️ bootCls 即 main 主函数所在的 Class
     *     - 🔄 提供统一的启动配置访问接口
     *     - 🚀 支持不同平台的启动配置差异化处理
     *
     *     🧩 组件构成：
     *     - 应用类型标识 (app)
     *     - 启动参数数组 (inArgs)
     *     - 主启动类 (inMain)
     *     - 容器启动器 (launcher)
     *     - 预启动组件 (withPre)
     *     - 生命周期组件 (whenOn/whenRun/whenOff)
     * </pre>
     *
     * @param bootCls 启动类（通常是包含 main 函数的主类） 📌
     *
     * @return 🧩 {@link HBoot} 启动配置组件
     */
    HBoot boot(Class<?> bootCls);

    /**
     * 基于启动类与命令行参数解析并装配"能量配置" {@link HEnergy}。
     *
     * <p>🔌 语义：抽象"配好再启动"。该方法负责从多渠道（类路径、外部配置、环境变量等）
     * 汇聚与校验启动所需的<strong>最小必要配置</strong>与<strong>扩展配置</strong>。</p>
     *
     * <pre>
     *     🎯 设计意图：
     *     - 📌 bootCls 即 main 主函数 Class，作为配置根路径和应用标识
     *     - 🧩 基于启动类进行配置文件定位和扫描路径计算
     *     - 🔄 合并多源配置（classpath、外部目录、环境变量、系统属性、命令行参数）
     *     - 🛡️ 进行配置校验和必填项检查
     *     - 📊 构建统一的能量配置包用于后续启动流程
     *
     *     📁 配置来源优先级：
     *     1. 🚀 命令行参数 (args) - 最高优先级
     *     2. 🏠 环境变量 - 高优先级
     *     3. 📦 系统属性 - 中等优先级
     *     4. 📄 外部配置文件 - 中等优先级
     *     5. 📚 classpath 配置文件 - 最低优先级
     *
     *     ⚠️ 注意事项：
     *     - bootCls 用于确定应用归属和配置根路径
     *     - args 会合并到 HEnergy 中并注入到 HConfig 的 "arguments" 字段
     *     - 返回的 HEnergy 不应为 null，确保后续流程可用
     * </pre>
     *
     * @param bootCls 启动类（通常用于定位配置根、计算扫描路径、确定归属应用等） 📌
     * @param args    启动参数（通常会合并进 {@link HEnergy}，并在上层注入到 {@code HConfig} 的 {@code "arguments"} 字段） 🧵
     *
     * @return 组装完成且可用于后续流程的 {@link HEnergy} 实例（不应为 {@code null}） ⚡
     */
    HEnergy energy(Class<?> bootCls, String[] args);
}
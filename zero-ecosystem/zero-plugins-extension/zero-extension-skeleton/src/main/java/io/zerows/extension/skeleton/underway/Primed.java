package io.zerows.extension.skeleton.underway;

import io.vertx.core.Future;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.metadata.MDModuleManager;

import java.util.Set;

/**
 * 特殊 SPI 标记接口，用于实现启动的完成流程专用，只要调用了{@link OCacheConfiguration#valueSet()} 的地方都应该从此处来执行相关流程
 * 此流程的基础结构需求
 * <pre>
 *     1. 每个 Module 中会包含特殊的 {@link MDModuleManager} 引用，此引用针对每个 Module 必须是单件模式 Singleton 的简单说每个 Module 在任意场景下
 *        只能拥有一个 {@link MDModuleManager} 实例，因此它所包含的 Memory 中的数据结构才能唯一，此处的唯一性就是全局支持
 *        - 横向模块调用
 *        - 重写模块配置（CRUD引擎）
 *        上述结构才成立
 *     2. {@link Primed} 接口的实现类必须通过 SPI 进行加载，对应的 {@link PrimedActor} 会在所有模块启动之后完成后续启动
 *     3. 后期分布式环境下，此处的 SPI 实现还必须支持另外的模式，即：远程网状过程调用，后续在此接口中直接扩展
 *        如远程环境：A, B, C 三个微服务
 *        - 当前接口会在三个微服务内部（注不是微服务本身）的模块启动完成之后进行调用
 *        - 远程调用过程中，会将整套 {@link Primed} 单独作用于 A, B, C 内部
 *        - 此时的 {@link MDModuleManager} 实例内部不可能是单例，但在单独服务模式之下依旧是单例，所以要依赖远程管理（分布式对象存储）
 * </pre>
 *
 * @author lang : 2025-12-25
 */
public interface Primed {
    /**
     * 此处之所以不在外层迭代，而是直接将所有启动的模块传递进来，主要在于
     * <pre>
     *     1. 如果模块和模块之间有所关联，可直接通过 waitSet 来进行处理
     *     2. 避免在外层进行多次 Future 迭代，导致异步处理变得复杂
     *     3. 提供更大的灵活性，允许实现类根据需要对所有模块进行统一处理
     * </pre>
     *
     * @param waitSet 所有已经启动的模块信息
     *
     * @return 异步处理结果
     */
    Future<Boolean> afterAsync(Set<MDConfiguration> waitSet);
}

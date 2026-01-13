package io.zerows.cosmic.plugins.job;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.cosmic.plugins.job.management.ORepositoryJob;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.management.ORepository;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "job", sequence = -216, configured = false)
@Slf4j
public class JobActor extends AbstractHActor {
    public static JobInterval ofInterval() {
        return JobClientManager.of().refInterval();
    }

    public static JobStore ofStore() {
        return JobClientManager.of().refStore();
    }

    public static JobConfig ofConfig() {
        return JobClientManager.of().refConfiguration();
    }

    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final Boolean enabled = config.options("enabled", Boolean.TRUE);
        if (!enabled) {
            this.vLog("[ Job ] ❌ JobActor 任务组件临时被禁用，跳过启动！");
            return Future.succeededFuture(Boolean.FALSE);
        }


        this.vLog("[ Job ] JobActor 正在扫描任务类，vertx = {}", vertxRef.hashCode());
        final HSetting setting = NodeStore.ofSetting(vertxRef);
        ORepository.ofOr(ORepositoryJob.class).whenStart(setting);
        this.vLog("[ Job ] ✅ JobActor 已成功扫描完成！！");


        /*
         * 将 Vertx 引用写入到传入的 HConfig 中的 reference 字段，并用该配置初始化 JobClientManager。
         * 关键点说明：
         * 1. config.putRef(vertxRef)
         *    - 将传入的 Vertx 实例保存在 HConfig 的引用容器中（此操作会修改并返回同一个 HConfig），以便向下游组件穿透传递 Vertx 引用。
         *    - 这是一个有副作用的调用（会改变 config），调用者需知晓并谨慎使用。
         * 2. JobClientManager.of().configure(...)
         *    - 获取 JobClientManager 单例并使用传入的 HConfig 进行配置初始化。
         *    - 在 configure 内部会读取之前保存的 Vertx 引用来完成与 Vertx 相关的初始化工作（例如：注册 EventBus 处理器、创建定时器/调度器、初始化 JobStore、以及可能的上下文相关资源）。
         * 3. 调用时机与线程注意事项
         *    - 必须在 Vertx 已可用且已正确初始化后（例如在组件启动流程中），再执行此函数。
         *    - 如果 configure 会直接与 Vertx 事件循环或上下文交互，建议在合适的 Vertx 上下文/线程中调用，避免在事件循环中执行阻塞操作。
         * 4. 幂等性与重复调用
         *    - 根据 JobClientManager 的实现，重复调用 configure 可能会覆盖或重新初始化已有状态；建议仅在启动流程中调用一次，或确保实现对重复调用是幂等的。
         * 5. 错误与异常处理
         *    - configure 过程中可能抛出运行时异常（例如初始化资源失败），调用方应当根据需要捕获并记录，或让上层框架统一处理。
         *
         * 使用示例（语义说明）：
         * - 先把 Vertx 透传到配置对象中，然后再用该配置去配置 Job 客户端管理器，确保 JobClientManager 可以访问到运行时的 Vertx 实例。
         */
        JobClientManager.of().configure(config.putRef(vertxRef));
        this.vLog("[ Job ] JobActor 任务配置完成！config = {}", config);

        return Future.succeededFuture(Boolean.TRUE);
    }
}

package io.zerows.extension.skeleton.underway;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.skeleton.boot.ExAbstractHActor;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
import io.zerows.support.Fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 后期启动的专用 Actor，此 Actor 用于处理作用于所有模块的公共服务，典型如：
 * <pre>
 *     1. 工作流处理
 *     2. UI 处理
 *     3. CRUD 处理
 * </pre>
 * 因为这些流程必须是在所有模块启动完成后才能进行处理的，所以需要一个专用的 Actor 来进行统一管理，横向调用，如此才能保证所有的模块功能都可用，否则
 * 模块没有启动完成之后会导致部分数据不完善，举个例子
 * <pre>
 *     1. Module-01 -> CRUD -> Module-02
 *        上述启动流程中，CRUD Engine 无法识别 Module-02 的数据模型，因为 Module-02 还没有启动完成，这种场景下会导致 Memory 中数据绑定失败
 *     2. 启动优先级为 32767，即 Short.MAX_VALUE，确保此 Actor 最后启动，如果在定制过程中您的模块超过了这个值，那就自动被忽略，这种情况下
 *        你模块中定义的所有内容将会失效！
 * </pre>
 *
 * @author lang : 2025-12-25
 */
@Actor(value = "extension", sequence = Short.MAX_VALUE, configured = false)
public class PrimedActor extends ExAbstractHActor {

    private static final Cc<Class<?>, List<Primed>> CC_PRIMED = Cc.open();

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final List<Primed> executors = CC_PRIMED.pick(() -> HPI.findMany(Primed.class), this.getClass());
        this.vLog("特殊 Actor 启动，执行后期调度！Provider提供者：{}", executors.size());
        if (executors.isEmpty()) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        final List<Future<Boolean>> futures = new ArrayList<>();
        final Set<MDConfiguration> exmodule = OCacheConfiguration.of().valueSet();
        this.vLog("@ / {} 系统检测到 {} 个模块！", KeConstant.K_PREFIX_BOOT, exmodule.size());
        executors.forEach(executor -> {
            this.vLog("@ / 执行后期调度器：{}", executor.getClass().getName());
            futures.add(executor.afterAsync(exmodule, vertxRef));
        });
        return Fx.combineB(futures);
    }
}

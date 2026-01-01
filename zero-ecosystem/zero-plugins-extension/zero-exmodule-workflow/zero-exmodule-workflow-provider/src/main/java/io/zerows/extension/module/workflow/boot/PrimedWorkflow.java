package io.zerows.extension.module.workflow.boot;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDWorkflow;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.extension.module.workflow.component.deployment.DeployOn;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.underway.Primed;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-12-25
 */
@SPID(priority = 1024)
@Slf4j
public class PrimedWorkflow implements Primed {
    @Override
    public Future<Boolean> afterAsync(final Set<MDConfiguration> waitSet, final Vertx vertxRef) {
        if (this.isDisabled(vertxRef)) {
            log.info("{} `{}` 工作流引擎被配置为禁用，跳过启动流程！", KeConstant.K_PREFIX_BOOT, MID.BUNDLE_SYMBOLIC_NAME);
            return Future.succeededFuture(Boolean.TRUE);
        }
        log.info("{} `{}` 启动工作流引擎……", KeConstant.K_PREFIX_BOOT, MID.BUNDLE_SYMBOLIC_NAME);
        /* 提取所有 MDConfiguration 中的 Workflow 进行发布 */
        final Set<MDWorkflow> workflowSet = new HashSet<>();
        waitSet.stream()
            .filter(configuration -> Objects.nonNull(configuration.inWorkflow()))
            .forEach(configuration -> {
                final Set<MDWorkflow> workflowOfMod = configuration.inWorkflow();
                if (!workflowOfMod.isEmpty()) {
                    log.info("{} ---> 模块 `{}` 包含 {} 个工作流定义", KeConstant.K_PREFIX_BOOT, configuration.id().value(), workflowOfMod.size());
                    workflowSet.addAll(workflowOfMod);
                }
            });
        final List<Future<Boolean>> futures = new ArrayList<>();
        workflowSet.forEach(mdWorkflow -> futures.add(DeployOn.get(mdWorkflow).initialize()));
        return Fx.combineB(futures).compose(completed -> {
            log.info("{} `{}` 工作流引擎初始化完成！总发布：{}", KeConstant.K_PREFIX_BOOT, MID.BUNDLE_SYMBOLIC_NAME, workflowSet.size());
            return Future.succeededFuture(Boolean.TRUE);
        });
    }

    private boolean isDisabled(final Vertx vertxRef) {
        final HConfig workflow = NodeStore.findExtension(vertxRef, "workflow");
        return Objects.isNull(workflow);
    }
}

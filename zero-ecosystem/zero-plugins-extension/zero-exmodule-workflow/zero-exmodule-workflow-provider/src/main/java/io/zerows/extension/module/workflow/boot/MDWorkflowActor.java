package io.zerows.extension.module.workflow.boot;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDWorkflow;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.extension.module.workflow.component.deployment.DeployOn;
import io.zerows.extension.module.workflow.metadata.MetaWorkflow;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.specification.app.HAmbient;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "workflow", sequence = 1231, configured = false)
@Slf4j
public class MDWorkflowActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    protected Future<Boolean> startAsync(final HAmbient ambient, final Vertx vertxRef) {
        final MetaWorkflow workflow = this.manager().setting();
        if (Objects.isNull(workflow)) {
            log.warn("{} `{}` 模块正常启动，但未配置工作流元数据，跳过工作流引擎初始化。", KeConstant.K_PREFIX_BOOT, this.MID());
            return Future.succeededFuture(Boolean.TRUE);
        }

        final Database database = workflow.camundaDatabase();
        if (Objects.isNull(database)) {
            log.warn("{} `{}` 工作流数据库未初始化", KeConstant.K_PREFIX_BOOT, this.MID());
            return Future.succeededFuture(Boolean.TRUE);
        }
        return this.manager().compile(workflow, vertxRef).compose(initalized -> {
            log.info("{} `{}` 启动工作流引擎……，{}", KeConstant.K_PREFIX_BOOT, this.MID(), workflow.getName());

            /* 提取所有 MDConfiguration 中的 Workflow 进行发布 */
            final Set<MDConfiguration> exmodules = OCacheConfiguration.of().valueSet();
            final Set<MDWorkflow> workflowSet = new HashSet<>();
            exmodules.stream()
                .filter(configuration -> Objects.nonNull(configuration.inWorkflow()))
                .forEach(configuration -> {
                    final Set<MDWorkflow> workflowOfMod = configuration.inWorkflow();
                    log.info("{} ---> 模块 `{}` 包含 {} 个工作流定义", KeConstant.K_PREFIX_BOOT, configuration.id().value(), workflowOfMod.size());
                    workflowSet.addAll(workflowOfMod);
                });
            final List<Future<Boolean>> futures = new ArrayList<>();
            workflowSet.forEach(mdWorkflow -> futures.add(DeployOn.get(mdWorkflow).initialize()));
            return Fx.combineB(futures).compose(completed -> {
                log.info("{} `{}` 工作流引擎初始化完成！总发布：{}", KeConstant.K_PREFIX_BOOT, this.MID(), workflowSet.size());
                return Future.succeededFuture(Boolean.TRUE);
            });
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDWorkflowManager manager() {
        return MDWorkflowManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<MetaWorkflow> typeOfMDC() {
        return MetaWorkflow.class;
    }
}

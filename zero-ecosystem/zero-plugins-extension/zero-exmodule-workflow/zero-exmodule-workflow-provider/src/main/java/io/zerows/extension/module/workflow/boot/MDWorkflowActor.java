package io.zerows.extension.module.workflow.boot;

import io.r2mo.base.dbe.Database;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.module.workflow.metadata.MetaWorkflow;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.extension.skeleton.metadata.MDModuleActor;
import io.zerows.specification.app.HAmbient;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
        return this.manager().compile(workflow, vertxRef);
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

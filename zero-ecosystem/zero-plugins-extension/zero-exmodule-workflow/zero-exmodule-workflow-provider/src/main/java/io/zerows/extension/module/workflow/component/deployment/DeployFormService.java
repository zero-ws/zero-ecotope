package io.zerows.extension.module.workflow.component.deployment;

import io.vertx.core.Future;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.web.MDWorkflow;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.DeploymentBuilder;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class DeployFormService implements DeployOn {
    private final transient DeploymentBuilder builderRef;
    private final transient MDWorkflow workflow;

    DeployFormService(final MDWorkflow workflow, final DeploymentBuilder builder) {
        this.builderRef = builder;
        this.workflow = workflow;
    }

    @Override
    public Future<Boolean> initialize() {
        Objects.requireNonNull(this.builderRef);
        final Set<String> formFiles = this.workflow.bpmnForm();
        if (formFiles.isEmpty()) {
            return Ux.futureT();
        }
        final Set<String> deployedSet = new HashSet<>();
        formFiles.forEach(formFile -> {
            final InputStream istream = ZeroFs.of().inStream(formFile);
            if (Objects.nonNull(istream)) {
                final String filename = formFile.substring(formFile.lastIndexOf("/") + 1);
                this.builderRef.addInputStream(filename, istream);
                deployedSet.add(formFile);
            } else {
                log.warn("{} 忽略表单文件：`{}` 不存在！", KeConstant.K_PREFIX_BOOT, formFile);
            }
        });
        final String name = this.workflow.name();
        log.info("{}    `{}` 工作流表单数：{}", KeConstant.K_PREFIX_BOOT, name, deployedSet.size());
        return Ux.futureT();
    }
}

package io.zerows.extension.runtime.workflow.uca.deployment;

import io.vertx.core.Future;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.configuration.module.MDWorkflow;
import io.zerows.epoch.program.Ut;
import org.camunda.bpm.engine.repository.DeploymentBuilder;

import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
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
        formFiles.forEach(formFile -> {
            final InputStream istream = Ut.ioStream(formFile);
            if (Objects.nonNull(istream)) {
                final String filename = formFile.substring(formFile.lastIndexOf("/") + 1);
                this.builderRef.addInputStream(filename, istream);
            } else {
                LOG.Deploy.warn(this.getClass(), "Ignored: `{0}` does not exist.", formFile);
            }
        });
        return Ux.futureT();
    }
}

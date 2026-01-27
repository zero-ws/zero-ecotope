package io.zerows.extension.module.workflow.component.deployment;

import io.vertx.core.Future;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.MDWorkflow;
import io.zerows.extension.module.workflow.boot.Wf;
import io.zerows.extension.module.workflow.plugins.FlowSequenceListener;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;

import java.util.Collection;
import java.util.Objects;

;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class DeployBpmnService implements DeployOn {
    private final transient DeploymentBuilder builder;
    private final transient DeployOn formStub;
    private transient String tenantId;

    DeployBpmnService(final MDWorkflow workflow) {
        // DeploymentBuilder create
        final RepositoryService repository = Wf.camundaRepository();
        this.builder = repository.createDeployment();
        // Set Deployment Name
        this.builder.name(workflow.name());
        this.builder.source(KWeb.ARGS.V_AUDITOR);
        // Avoid duplicated deployment when container started.
        this.builder.enableDuplicateFiltering(Boolean.TRUE);

        final String bpmnFile = workflow.bpmnEntry();
        log.info("{} \uD83C\uDFC9 `{}` 加载 BPMN 文件：`{}`",
            KeConstant.K_PREFIX_BOOT, workflow.name(), bpmnFile);
        // BPMN Model Instance
        final BpmnModelInstance instance = Bpmn.readModelFromStream(Ut.ioStream(bpmnFile));
        Objects.requireNonNull(instance);

        // Flow Processing for activity info
        this.initializeListener(instance);
        this.builder.addModelInstance(bpmnFile, instance);

        // DeployStub ( Form Service )
        this.formStub = new DeployFormService(workflow, this.builder);
    }

    /*
     * Add Global Flow Sequence Listener
     <bpmn:extensionElements>
        <camunda:executionListener class="io.vertx.mod.plugin.workflow.FlowSequenceListener" event="zero-take"/>
     </bpmn:extensionElements>
     */
    private void initializeListener(final BpmnModelInstance instance) {
        final Collection<SequenceFlow> sequences = instance.getModelElementsByType(SequenceFlow.class);
        sequences.forEach(sequence -> {
            final ExtensionElements elements = instance.newInstance(ExtensionElements.class);
            final CamundaExecutionListener elementListener = instance.newInstance(CamundaExecutionListener.class);
            elementListener.setCamundaClass(FlowSequenceListener.class.getName());
            elements.addChildElement(elementListener);
            sequence.setExtensionElements(elements);
        });
    }

    @Override
    public Future<Boolean> initialize() {
        Objects.requireNonNull(this.builder);
        if (Ut.isNotNil(this.tenantId)) {
            this.builder.tenantId(this.tenantId);
        }
        return this.formStub.initialize().compose(nil -> {
            final Deployment deployment = this.builder.deployWithResult();
            log.info("{}    `{}(id={})` 工作流成功部署！SUCCESS！", KeConstant.K_PREFIX_BOOT,
                deployment.getName(), deployment.getId());
            return Ux.futureT();
        });
    }

    @Override
    public DeployOn tenant(final String tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}

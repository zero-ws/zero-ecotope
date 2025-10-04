package io.zerows.epoch.jigsaw;

import io.zerows.platform.constant.VPath;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.List;

/**
 * @author lang : 2024-06-17
 */
class EquipWorkflow implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {

        // workflow/RUNNING/
        final MDId id = configuration.id();
        final Bundle owner = id.owner();

        final String workflowDir = id.path() + "/workflow/RUNNING";
        final List<String> workflowList = Ut.ioDirectories(workflowDir);
        workflowList.stream().map(workflowEach -> {
                final String workflowPath;
                if (workflowEach.startsWith(workflowDir)) {
                    workflowPath = workflowEach;
                } else {
                    workflowPath = Ut.ioPath(workflowDir, workflowEach);
                }
                return this.buildWorkflow(workflowPath, id);
            })
            .forEach(configuration::addWorkflow);
    }

    private MDWorkflow buildWorkflow(final String workflowDir, final MDId id) {
        final MDWorkflow workflow = new MDWorkflow(id);
        final Bundle owner = id.owner();
        workflow.configure(workflowDir.trim());
        // *.form
        final List<String> formFiles = Ut.ioFiles(workflowDir, VPath.SUFFIX.BPMN_FORM);
        // *.json
        final List<String> formData = Ut.ioFiles(workflowDir, VPath.SUFFIX.JSON);
        return workflow.configure(formFiles, formData);
    }
}

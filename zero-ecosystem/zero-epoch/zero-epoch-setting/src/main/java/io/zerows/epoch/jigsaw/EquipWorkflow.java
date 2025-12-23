package io.zerows.epoch.jigsaw;

import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.basicore.MDWorkflow;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;

import java.util.List;

/**
 * @author lang : 2024-06-17
 */
class EquipWorkflow implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {

        // workflow/RUNNING/
        final MDId id = configuration.id();
        final ZeroFs io = ZeroFs.of(id);

        final String workflowDir = Ut.ioPath(id.path(), WORKFLOW_DIR);
        final List<String> workflowList = io.inDirectories(WORKFLOW_DIR);
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

    private static final String WORKFLOW_DIR = "workflow/RUNNING";

    private MDWorkflow buildWorkflow(final String workflowDir, final MDId id) {
        final MDWorkflow workflow = new MDWorkflow(id);
        final String workflowBase = workflowDir.trim();
        workflow.configure(workflowBase);
        final ZeroFs io = ZeroFs.of(id);
        // *.form
        final List<String> formFiles = io.inFiles(workflowBase, VValue.SUFFIX.BPMN_FORM)
            // 追加一层后缀过滤
            .stream().filter(file -> file.endsWith(VValue.SUFFIX.BPMN_FORM))
            .toList();
        // *.json
        final List<String> formData = io.inFiles(workflowBase, VValue.SUFFIX.JSON);
        return workflow.configure(formFiles, formData);
    }
}

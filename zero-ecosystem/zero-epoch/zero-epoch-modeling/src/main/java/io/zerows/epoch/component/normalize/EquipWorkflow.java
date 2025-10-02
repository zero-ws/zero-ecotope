package io.zerows.epoch.component.normalize;

import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.configuration.module.MDId;
import io.zerows.epoch.configuration.module.MDWorkflow;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Objects;

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
        final List<String> workflowList = this.scanDir(workflowDir, owner);
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
        final List<String> formFiles = this.scanForm(workflowDir, owner, VPath.SUFFIX.BPMN_FORM);
        // *.json
        final List<String> formData = this.scanForm(workflowDir, owner, VPath.SUFFIX.JSON);
        return workflow.configure(formFiles, formData);
    }

    private List<String> scanForm(final String workflowDir, final Bundle owner,
                                  final String extension) {
        if (Objects.isNull(owner)) {
            return Ut.ioFiles(workflowDir, extension);
        } else {
            return Ut.Bnd.ioFile(workflowDir, owner, extension);
        }
    }

    private List<String> scanDir(final String workflowDir, final Bundle owner) {
        if (Objects.isNull(owner)) {
            return Ut.ioDirectories(workflowDir);
        } else {
            return Ut.Bnd.ioDirectory(workflowDir, owner);
        }
    }
}

package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 工作流相关配置，发布工作流以及工作表单专用配置，提取工作流表单也会基于当前配置进行处理。
 * <pre><code>
 *     1. workflow.bpmn ( 文件 )
 *     2. *.form 配置
 *     3. node = *.json 配置（自定义表单加载）
 * </code></pre>
 *
 * @author lang : 2024-06-17
 */
public class MDWorkflow implements Serializable {

    private final MDId id;
    private final Set<String> formSet = new TreeSet<>();
    private final ConcurrentMap<String, JsonObject> formData = new ConcurrentHashMap<>();
    private String name;
    private String bpmn;
    private String directory;

    public MDWorkflow(final MDId id) {
        this.id = id;
    }

    public String name() {
        return this.name;
    }

    public JsonObject form(final String name) {
        return this.formData.getOrDefault(name, null);
    }

    public String bpmnEntry() {
        return this.bpmn;
    }

    public Set<String> bpmnForm() {
        return this.formSet;
    }

    public MDWorkflow configure(final String folder) {
        final String folderSeed;
        if (folder.endsWith("/")) {
            folderSeed = folder.substring(0, folder.length() - 1);
        } else {
            folderSeed = folder;
        }

        this.directory = folderSeed;
        this.bpmn = folderSeed + "/workflow.bpmn";
        this.name = folderSeed.substring(folderSeed.lastIndexOf("/") + 1);
        // 基础配置
        return this;
    }

    public MDWorkflow configure(final List<String> formSet,
                                final List<String> formData) {
        formSet.forEach(formEach -> {
            if (formEach.startsWith(this.directory)) {
                this.formSet.add(formEach);
            } else {
                final String formFile = Ut.ioPath(this.directory, formEach);
                this.formSet.add(formFile);
            }
        });
        formData.forEach(file -> {
            final String filename = file.replace(".json", "");
            this.formData.put(filename, this.ioData(file));
        });
        return this;
    }

    private JsonObject ioData(final String file) {
        final Bundle owner = this.id.owner();
        if (Objects.isNull(owner)) {
            final String filepath = Ut.ioPath(this.directory, file);
            return Ut.ioJObject(filepath);
        } else {
            return Ut.Bnd.ioJObject(file, owner);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MDWorkflow that = (MDWorkflow) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}

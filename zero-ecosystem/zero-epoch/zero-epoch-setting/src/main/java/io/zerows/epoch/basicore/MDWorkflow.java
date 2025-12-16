package io.zerows.epoch.basicore;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.support.Ut;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // ... 类定义 ...

    public MDWorkflow configure(final List<String> formSet,
                                final List<String> formData) {
        // 1. 处理 Form 文件路径
        formSet.forEach(formEach -> {
            if (this.isPathResolved(formEach)) {
                // 如果已经是完整路径（包含前缀或已存在），直接添加
                this.formSet.add(formEach);
            } else {
                // 否则拼接目录
                final String formFile = Ut.ioPath(this.directory, formEach);
                this.formSet.add(formFile);
            }
        });

        // 2. 处理 Data 数据读取
        formData.forEach(file -> {
            // 这里的 replace 是为了生成 Key，不影响读取路径
            final String filename = file.replace(".json", "");
            this.formData.put(filename, this.ioData(file));
        });
        return this;
    }

    private JsonObject ioData(final String file) {
        // 判断 file 是否已经是完整路径
        final String filepath;
        if (this.isPathResolved(file)) {
            filepath = file;
        } else {
            filepath = Ut.ioPath(this.directory, file);
        }
        return ZeroFs.of().inJObject(filepath);
    }

    /**
     * 辅助方法：判断路径是否已经“就绪”
     * 不需要再拼接目录的情况：
     * 1. 字符串以 directory 开头（防重复拼接）
     * 2. 是绝对路径 (/etc/...)
     * 3. 文件在当前路径下真实存在 (init/oob/...)
     */
    private boolean isPathResolved(final String pathStr) {
        if (pathStr == null) {
            return false;
        }

        // 1. 字符串检查：如果已经包含了 directory 前缀
        if (pathStr.startsWith(this.directory)) {
            return true;
        }

        try {
            final Path path = Paths.get(pathStr);
            // 2. 物理/逻辑检查：绝对路径 或 文件存在
            // (Files.exists 解决了你刚才遇到的 init/oob 返回完整相对路径的问题)
            return path.isAbsolute() || Files.exists(path);
        } catch (final Exception ignored) {
            // 路径字符串非法等情况，默认视为未解析，交由后续 Ut.ioPath 处理
            return false;
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

package io.zerows.epoch.configuration.module;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.metadata.MultiKeyMap;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 模块配置新规范，定义完整的结构化目录
 * <pre><code>
 *     id = zero-extension-running-ambient
 *     plugins/{id}/
 *     -- data：         数据目录
 *     -- database：     数据库初始化目录
 *     -- logging：      日志配置目录
 *     -- model：        静态模型目录
 *     -- modulat：      模块化目录
 *     -- security：     安全管理目录
 *     -- web：          前端页面配置
 *     -- workflow：     工作流配置
 * </code></pre>
 * 内置引用直接使用表名创建跨实体专用引用，这样实现实体的唯一化
 *
 * @author lang : 2024-05-07
 */
public class MDConfiguration {
    // configuration.json
    private final JsonObject configurationJ = new JsonObject();
    /**
     * model 目录之下三合一的配置信息，包含了所有实体信息，此处的键值得商榷，最终构造新的数据结构用来存储实体信息，其 Map 结构
     * <pre><code>
     *     identifier = MDEntity
     *     table = MDEntity
     * </code></pre>
     */
    private final MultiKeyMap<MDEntity> entityMap = new MultiKeyMap<>();
    private final MultiKeyMap<MDConnect> connectMap = new MultiKeyMap<>();
    /**
     * file set 专用目录
     * <pre><code>
     *     - data
     *     - modulat
     *     - security
     *     - workflow
     * </code></pre>
     */
    private final Set<String> fileSet = new TreeSet<>();

    /**
     * 工作流相关定义信息
     */
    private final ConcurrentMap<String, MDWorkflow> workflowMap = new ConcurrentHashMap<>();

    /**
     * Web 页面的定义
     */
    private final ConcurrentMap<String, MDPage> pageMap = new ConcurrentHashMap<>();

    private final MDId id;
    private String name;

    public MDConfiguration(final Bundle owner) {
        this.id = MDId.of(owner);
    }

    public MDConfiguration(final String id) {
        this.id = MDId.of(id);
    }

    // ---------- 读取数据专用方法
    public MDId id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    // ---------- 数据提取专用方法
    // ---- Boot 扩展专用
    public Set<MDConnect> inConnect() {
        return this.connectMap.values();
    }

    public Set<MDEntity> inEntity() {
        return this.entityMap.values();
    }

    public MDEntity inEntity(final String identifier) {
        return this.entityMap.getOr(identifier);
    }

    public Set<MDWorkflow> inWorkflow() {
        return new HashSet<>(this.workflowMap.values());
    }

    public Set<MDPage> inWeb() {
        return new HashSet<>(this.pageMap.values());
    }

    // ---- 其他位置调用
    public MDConnect inConnect(final String tableOr) {
        return this.connectMap.getOr(tableOr);
    }

    public JsonObject inConfiguration() {
        return this.configurationJ;
    }

    public Set<String> inFiles() {
        return this.fileSet;
    }

    // ------------ 设置配置信息
    public void addShape(final String name, final JsonObject configurationJ) {
        this.name = name;
        this.configurationJ.mergeIn(configurationJ, true);
    }

    public void addFile(final List<String> directories) {
        this.fileSet.addAll(directories);
    }

    public void addWorkflow(final MDWorkflow workflow) {
        Objects.requireNonNull(workflow);
        this.workflowMap.put(workflow.name(), workflow);
    }

    public void addPage(final MDPage page) {
        Objects.requireNonNull(page);
        this.pageMap.put(page.key(), page);
    }

    public void setEntity(final ConcurrentMap<String, MDEntity> entityMap) {
        entityMap.forEach((identifier, value) -> {
            final MDEntity entity = entityMap.get(identifier);
            final MDConnect connect = entity.refConnect();
            final String table = connect.getTable();
            final String daoCls = connect.getDao().getName();
            this.entityMap.put(identifier, entity,
                table, daoCls);
        });
    }

    public void setConnect(final ConcurrentMap<String, MDConnect> connectMap) {
        connectMap.forEach((table, value) -> {
            final MDConnect connect = connectMap.get(table);
            final String daoCls = connect.getDao().getName();
            this.connectMap.put(table, connect, daoCls);
        });
    }

    public void addConnect(final List<MDConnect> connects) {
        connects.forEach(connect -> {
            final String table = connect.getTable();
            final String daoCls = connect.getDao().getName();
            this.connectMap.put(table, connect, daoCls);
        });
    }

    // ---------- 重写的 equals / hashCode 方法
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MDConfiguration that = (MDConfiguration) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}

package io.zerows.epoch.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.zerows.epoch.constant.KName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ## 🧩 原子部件
 * 📦 Pojo 元数据容器
 * 📋 Java 对象元数据
 * 🔧 这里是 HAtom 升级的定义
 * 📝 Yaml 数据结构如下：
 * // <pre><code class="yaml">
 *     type:                                    # Jooq 生成的 POJO 类型
 *     mapping:                                 # 实体 -> 字段的映射
 *         pojoField: jsonField
 * // </code></pre>
 */
@Slf4j
@Data
public class MMPojo implements Serializable {

    @JsonIgnore
    private final ConcurrentMap<String, String> columns = new ConcurrentHashMap<>();
    @JsonIgnore
    private String pojoFile;
    @JsonProperty(KName.TYPE)
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> type;
    @JsonProperty(KName.MAPPING)
    private ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>();

    public MMPojo on(final String pojoFile) {
        this.pojoFile = pojoFile;
        return this;
    }

    /*
     * 🔄 字段 -> 输出字段
     * 1) 字段在 `Pojo` 类中声明
     * 2) 输出字段未声明，通常在输入 `InJson` 中提供
     *
     * 📌 示例:
     *
     * zSigma -> sigma
     */
    public ConcurrentMap<String, String> getOut() {
        // 🔧 修复空映射转换的问题
        if (Objects.isNull(this.mapping)) {
            this.mapping = new ConcurrentHashMap<>();
        }
        return this.mapping;
    }

    public String getOut(final String key) {
        return this.getOut().getOrDefault(key, null);
    }

    /*
     * 🔄 输出字段 -> 字段
     * 与 `getOut` 相反，不获取
     *
     * 📌 示例:
     *
     * sigma -> zSigma
     */
    @SuppressWarnings("all")
    public ConcurrentMap<String, String> getIn() {
        final ConcurrentMap<String, String> mapper =
            new ConcurrentHashMap<>();
        mapping.forEach((key, value) -> mapper.put(value, key));
        return mapper;
    }

    public String getIn(final String key) {
        return this.getIn().getOrDefault(key, null);
    }

    /*
     * 📥 输入用
     * 列 -> zSigma
     */
    public ConcurrentMap<String, String> getInColumn() {
        return this.columns;
    }

    /*
     * 📤 输出用
     * 列 -> sigma
     */
    public ConcurrentMap<String, String> getOutColumn() {
        final ConcurrentMap<String, String> revert = new ConcurrentHashMap<>();
        if (!this.columns.isEmpty()) {
            final ConcurrentMap<String, String> fieldMap = this.getIn();
            /*
             * 🔄 实际字段 -> 列
             */
            this.columns.forEach((key, value) -> {
                final String outField = fieldMap.get(key);
                if (Objects.nonNull(outField)) {
                    revert.put(value, outField);
                }
            });
        }
        return revert;
    }

    /*
     * 🔗 替换列映射，必须调用此方法
     * 或者
     * this.columns 无效
     */
    public MMPojo bindColumn(final ConcurrentMap<String, String> columns) {
        if (null != columns && !columns.isEmpty()) {
            this.columns.putAll(columns);
        }
        return this;
    }

    public MMPojo bind(final MMPojo mojo) {
        this.type = mojo.type;
        this.mapping.putAll(mojo.mapping);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final MMPojo mojo)) {
            return false;
        }
        return Objects.equals(this.type, mojo.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public String toString() {
        /*
         * 📊 报告构建
         */
        final StringBuilder report = new StringBuilder();
        report.append("==> 列: \n");
        this.columns.forEach((column, field) -> report
            .append(column).append('=').append(field).append('\n'));
        /*
         * 📊 Pojo 映射报告
         */
        report.append("==> Pojo: \n");
        this.mapping.forEach((actual, input) -> report
            .append(actual).append('=').append(input).append('\n'));
        return report.toString();
    }
}
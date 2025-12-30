package io.zerows.plugins.monitor.metadata;

import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 分类分组
 * <pre>
 *    - 1. category  类别
 *    - 2. group     组
 *    - 3. name      名称
 * </pre>
 * 记录
 * <pre>
 *    - 1. id        标识符
 *    - 2. vCount    计数
 *    - 3. vDisplay  显示内容
 *    - 4. vNumber   数值内容
 * </pre>
 * 地址
 * <pre>
 *    - 1. mmMeta       定义地址
 *    - 2. mmInstance   实例地址
 * </pre>
 *
 * @author lang : 2025-12-29
 */
@Data
@Accessors(fluent = true, chain = true)
public class MetricRow implements Serializable {
    // 分组信息
    private MetricType category;    // category 类别
    private String group;           // group 组别
    private String name;            // name 名称
    // 记录统计
    private String id;              // id 信息
    private Integer vCount;         // count 计数信息
    private String vDisplay;        // vDisplay 显示信息
    private String vNumber;         // vNumber 数值信息
    // 内存地址
    private String mmMeta;          // 定义地址 -> hashCode
    private String mmInstance;      // 实例地址 -> hashCode
    private String mmStandBy;       // 备用地址 -> hashCode
    @Setter(AccessLevel.NONE)
    private JsonObject vData = new JsonObject();
    @Setter(AccessLevel.NONE)
    private JsonObject vConfig = new JsonObject();

    @SuppressWarnings("all")
    public MetricRow addrMeta(final Integer hashCode) {
        return this.mmMeta(String.valueOf(hashCode));
    }

    @SuppressWarnings("all")
    public MetricRow addrInstance(final Integer hashCode) {
        return this.mmInstance(String.valueOf(hashCode));
    }

    @SuppressWarnings("all")
    public MetricRow addrStandBy(final Integer hashCode) {
        return this.mmStandBy(String.valueOf(hashCode));
    }

    public MetricRow config(final String key, final Object value) {
        this.vConfig.put(key, value);
        return this;
    }

    public MetricRow config(final JsonObject config) {
        this.vConfig.mergeIn(config, true);
        return this;
    }

    public MetricRow data(final String key, final Object value) {
        this.vData.put(key, value);
        return this;
    }

    public MetricRow data(final JsonObject data) {
        this.vData.mergeIn(data, true);
        return this;
    }

    public double vNumberAsDouble() {
        if (Objects.isNull(this.vNumber) || this.vNumber.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(this.vNumber);
        } catch (final NumberFormatException e) {
            // 如果填的是 "true"/"false" 或非数字，返回 0.0
            return 0.0;
        }
    }

    public MultiGauge.Row<?> vRow() {
        // 1. 创建一个可变的 List 来存放所有的 Tag
        final List<Tag> tagList = new ArrayList<>();

        // 2. 添加基础固定维度
        tagList.add(Tag.of("category", this.category().name()));
        tagList.add(Tag.of("group", Objects.toString(this.group, "NA")));
        tagList.add(Tag.of("name", Objects.toString(this.name, "-")));
        tagList.add(Tag.of("id", this.id));

        // 添加数值类展示值
        tagList.add(Tag.of("V_display", Objects.toString(this.vDisplay, "-")));
        tagList.add(Tag.of("V_count", Objects.toString(this.vCount, "0")));
        tagList.add(Tag.of("V_number", Objects.toString(this.vNumber, "0")));

        // 添加内存元数据
        tagList.add(Tag.of("MM_meta", Objects.toString(this.mmMeta, "-")));
        tagList.add(Tag.of("MM_obj", Objects.toString(this.mmInstance, "-")));
        tagList.add(Tag.of("MM_wait", Objects.toString(this.mmStandBy, "-")));

        // 3. 添加动态维度
        this.vConfig.forEach(entry -> {
            final String name = "C_" + entry.getKey();
            final String value = Objects.toString(entry.getValue(), "-");
            tagList.add(Tag.of(name, value));
        });
        this.vData.forEach(entry -> {
            final String name = "D_" + entry.getKey();
            final String value = Objects.toString(entry.getValue(), "-");
            tagList.add(Tag.of(name, value));
        });

        // 2. 构建维度数据
        final Tags tags = Tags.of(tagList);

        // 3. 计算数值
        double value = this.vNumberAsDouble();
        if (value == 0.0 && this.vNumber == null) {
            value = 1.0;
        }
        return MultiGauge.Row.of(tags, value);
    }
}

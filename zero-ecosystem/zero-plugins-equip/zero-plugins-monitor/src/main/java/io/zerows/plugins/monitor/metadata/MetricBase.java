package io.zerows.plugins.monitor.metadata;

import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-12-29
 */
@Data
@Accessors(fluent = true, chain = true)
public abstract class MetricBase implements Serializable {
    private String id;              // id 信息
    private String name;            // name 名称
    private String group;           // group 组别
    private String vDisplay;        // vDisplay 显示信息
    private String vNumber;         // vNumber 数值信息
    private JsonObject config = new JsonObject();

    protected abstract MetricType category();

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
        // 1. 先处理 Config 数据
        final String configStr = this.config.encodePrettily();


        // 2. 构建维度数据
        final Tags tags = Tags.of(
            "id", this.id,
            "category", this.category().name(),
            "name", Objects.toString(this.name, "-"),
            "group", Objects.toString(this.group, "NA"),
            "display", Objects.toString(this.vDisplay, "-"),
            "config", configStr
        );
        this.vExtension().forEach(tags::and);


        // 3. 计算数值
        double value = this.vNumberAsDouble();
        if (value == 0.0 && this.vNumber == null) {
            value = 1.0;
        }
        return MultiGauge.Row.of(tags, value);
    }

    protected Map<String, String> vExtension() {
        return Map.of();
    }
}

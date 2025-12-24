package io.zerows.extension.crud.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础CRUD配置，主要配置了几个关键信息
 * <pre>
 *     1. patterns：用于 CRUD 驱动的路径匹配相关信息（自动生成）
 *     2. columnKeyField：用于指定列的键字段，默认值为 "dataIndex"
 *     3. columnLabelField：用于指定列的标签字段，默认值为 "title"
 * </pre>
 *
 * @author lang : 2025-12-24
 */
@Data
public class IxConfig implements Serializable {
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray patterns;

    private String columnKeyField = "dataIndex";  // Render for column
    private String columnLabelField = "title";

    @Override
    public String toString() {
        return "IxConfig{" +
            "patterns=" + this.patterns +
            ", columnKeyField='" + this.columnKeyField + '\'' +
            ", columnLabelField='" + this.columnLabelField + '\'' +
            '}';
    }
}

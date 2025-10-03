package io.zerows.epoch.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;

import java.io.Serializable;
import java.util.Objects;

/**
 * Connect configuration data to
 * Dao / Pojo class
 */
public class MDConnect implements Serializable {

    @JsonIgnore // 新版不执行 dao 的序列化，构造过程中单独处理
    private Class<?> dao;
    private String pojoFile;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray unique;

    private String key;

    @JsonIgnore
    private MDMeta meta;


    /**
     * 简单计算
     * <pre><code>
     *     1. 关系模型 key 允许为空，不设置默认值
     *     2. 实体模型，未配置的情况下返回 "key"
     * </code></pre>
     *
     * @return key
     */
    public MDConnect build(final MDMeta meta) {
        Objects.requireNonNull(meta);
        this.meta = meta;


        // 设置默认主键
        if (meta.isEntity() && Objects.isNull(this.key)) {
            this.key = KName.KEY;
        }

        // 为空绑定时才执行此操作，不为空时可以直接获取
        if (Objects.isNull(this.dao)) {
            this.dao = meta.dao();
        }
        return this;
    }

    public MDMeta meta() {
        return this.meta;
    }

    public String getTable() {
        Objects.requireNonNull(this.meta);
        return this.meta.table();
    }

    public Class<?> getPojo() {
        Objects.requireNonNull(this.meta);
        return this.meta.pojo();
    }

    public Class<?> getDao() {
        return this.dao;
    }

    public void setDao(final Class<?> dao) {
        this.dao = dao;
    }

    public String getPojoFile() {
        return this.pojoFile;
    }

    public void setPojoFile(final String pojoFile) {
        this.pojoFile = pojoFile;
    }

    public JsonArray getUnique() {
        return this.unique;
    }

    public void setUnique(final JsonArray unique) {
        this.unique = unique;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ExConnect{" +
            ", dao=" + this.dao +
            ", pojoFile='" + this.pojoFile + '\'' +
            ", unique=" + this.unique +
            ", key='" + this.key + '\'' +
            '}';
    }
}

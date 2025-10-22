package io.zerows.epoch.basicore;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.program.R2Vector;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Connect configuration data to
 * Dao / Pojo class
 */
@Data
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
     * 使用 {@link MDConnect} 可直接构造 Join 所需的参数信息，注意一点就是 {@link MDConnect} 不能初始化字段，字段级别的
     * 初始化只能在底层处理过程中完成，不可以在创建的时候处理，所以到时候只能调用{@link DBNode#put(String, Class)} 来追加
     * 和字段、类型相关的一系列信息，只要拥有了如下信息就可以完整完成 JOIN
     * <pre>
     *     1. name = alias              / 提取数据时候专用
     *     2. name = {@link Class}      / 数据类型映射
     *     3. waitFor = ( key = value ) / JOIN 过程中的 ON 语句专用
     * </pre>
     *
     * @return 连接专用
     */
    public DBNode forJoin() {
        final DBNode node = new DBNode();
        node.entity(this.meta.pojo());      // 表实体信息
        node.table(this.meta.table());      // 表名信息
        node.key(this.key);                 // 主键信息
        // 根据是否带有 pojo 计算 R2Vector
        if (StrUtil.isNotEmpty(this.pojoFile)) {
            final R2Vector vector = MMAdapt.of(this.pojoFile).vector();
            node.vector(vector);
        }
        return node;
    }


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

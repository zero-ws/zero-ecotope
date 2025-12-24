package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.common.DBNode;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Connect configuration data to
 * Dao / Pojo class
 */
@Data
public class MDConnect implements Serializable {

    @JsonIgnore // 新版不执行 dao 的序列化，构造过程中单独处理
    private Class<?> dao;           // DaoClass 类名
    private String pojoFile;        // 绑定的 pojo 名

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray unique;       // 唯一键列表（二维矩阵）

    private String key;             // 主键字段名称

    @JsonIgnore
    private MDMeta meta;            // 元数据信息（此处要唯一）

    /**
     * 使用 {@link MDConnect} 可直接构造 Join 所需的参数信息，注意一点就是 {@link MDConnect} 不能初始化字段，字段级别的
     * 初始化只能在底层处理过程中完成，不可以在创建的时候处理，所以到时候只能调用{@link DBNode#types(String, Class)} 来追加
     * 和字段、类型相关的一系列信息，只要拥有了如下信息就可以完整完成 JOIN
     * <pre>
     *     1. name = findAlias              / 提取数据时候专用
     *     2. name = {@link Class}      / 数据类型映射
     *     3. waitFor = ( key = value ) / JOIN 过程中的 ON 语句专用
     * </pre>
     *
     * @return 连接专用
     */
    public DBNode forJoin() {
        /*
         * 赋值属性
         * - dao
         * - vector
         * 上述两个属性是构造 ADB 必须的属性
         */
        return DBNode.of(this.meta.dao(), Optional.ofNullable(this.pojoFile)
            .map(pojoFile -> MMAdapt.of(pojoFile).vector())
            /*
             * FIX-DBE: 表名在 JOIN 流程中要用来做缓存键，所以此处不可以返回 null，否则会引发 NPE 问题
             */
            .orElse(null)).table(this.meta.table());
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

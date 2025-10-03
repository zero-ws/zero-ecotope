package io.zerows.epoch.corpus.metadata.specification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 专用字段定义，对模型进行字段级的约束描述，基本配置的数据格式如下：
 * <pre><code class="json">
 *     {
 *         "key": "",
 *         "numbers": {
 *             "field1": "number code 1",
 *             "field2": "number code 2"
 *         },
 *         "unique": [
 *             [
 *                 "field1",
 *                 "field2"
 *             ]
 *         ],
 *         "created": {
 *             "by": "",
 *             "at": ""
 *         },
 *         "updated": {
 *             "by": "",
 *             "at": ""
 *         },
 *         "object": [
 *         ],
 *         "array": [
 *         ],
 *         "attachment": [
 *         ]
 *     }
 * </code></pre>
 * 上述配置定义了一个模型中的所有键相关信息，这些键信息是基于模型中的字段描述定义的：
 * <pre><code>
 *     key: 类型 - {@link String}
 *          主键字段名，现阶段模型只支持单主键，不支持多主键，在Zero Extension部分通常使用 "key" 做字段单主键，使用此键名的主要
 *          考虑点在 React 中使用，React 默认在迭代时使用了 "key" 键名。
 *
 *     numbers：类型 - {@link JsonObject}
 *          序号字段详细定义，定义了需要依赖 {X_NUMBER} 表定义生成的序号字段，一个模型中可以拥有多个，所以此处定义是典型的哈希表
 *          结构，定义了 `field1 = number code1`，`field2 = number code2` 的结构定义，最终模型记录中的需要依赖此处的 number
 *          code 来生成。
 *
 *     unique：类型 - {@link JsonArray}
 *          此处的 unique 是一个二维数组：[[]]，这个结构中把业务标识规则进行了分组
 *          - 第一维度：分组，若您的模型存在多组业务标识规则，那么第一维度表示标识规则的组
 *          - 第二维度：属性集，每一个属性集表示一个业务标识规则，这个规则中的属性集合是 `and` 关系，构成多字段唯一规则集
 *
 *     created / updated：类型 - {@link JsonObject}
 *          - at：表示创建/更新时间的属性信息，不配置时为 createdAt / updatedAt
 *          - by：表示创建人/更新人的属性信息，不配置时为 createdBy / updatedBy
 *
 *     object / array / attachment：类型 - {@link JsonArray}
 *          - object 表示属性数据结构是 {@link JsonObject}
 *          - array 则表示属性数据结构是 {@link JsonArray}
 *          - attachment 表示此属性会关联 X_ATTACHMENT 中的附件信息
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KField implements Serializable {
    /** 主键属性值，默认 `key` */
    private String key;


    /** 业务标识规则，可自定义，二维数组结构 `[[]]` */
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray unique;


    /** Auditor字段，创建人、创建时间 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject created;


    /** Auditor字段，更新人、更新时间 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject updated;


    /** 序号字段定义，定义不同的字段集 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject numbers;


    /** {@link JsonObject} 类型的字段定义 */
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray object;


    /** {@link JsonArray} 类型的字段定义 */
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray array;


    /** 附件字段，系统会根据字段信息计算带有文件的属性信息 */
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray attachment;

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public JsonArray getUnique() {
        return this.unique;
    }

    public void setUnique(final JsonArray unique) {
        this.unique = unique;
    }

    public JsonObject getCreated() {
        return this.created;
    }

    public void setCreated(final JsonObject created) {
        this.created = created;
    }

    public JsonObject getUpdated() {
        return this.updated;
    }

    public void setUpdated(final JsonObject updated) {
        this.updated = updated;
    }

    public JsonObject getNumbers() {
        return this.numbers;
    }

    public void setNumbers(final JsonObject numbers) {
        this.numbers = numbers;
    }

    public JsonArray getObject() {
        return this.object;
    }

    public void setObject(final JsonArray object) {
        this.object = object;
    }

    public JsonArray getArray() {
        return this.array;
    }

    public void setArray(final JsonArray array) {
        this.array = array;
    }

    public JsonArray getAttachment() {
        return this.attachment;
    }

    public void setAttachment(final JsonArray attachment) {
        this.attachment = attachment;
    }


    // -------------------------------

    /**
     * 以集合方式返回 {@link JsonArray} 字段定义
     *
     * @return {@link Set}
     */
    public Set<String> fieldArray() {
        return Ut.toSet(this.array);
    }

    /**
     * 以集合方式返回 {@link JsonObject} 字段定义
     *
     * @return {@link Set}
     */
    public Set<String> fieldObject() {
        return Ut.toSet(this.object);
    }

    /**
     * 以集合方式返回 Auditor 字段属性集
     *
     * @return {@link Set}
     */
    public Set<String> fieldAudit() {
        final Set<String> set = new HashSet<>();
        final JsonObject created = Ut.valueJObject(this.created);
        if (Objects.nonNull(created.getValue(KName.BY))) {
            set.add(created.getString(KName.BY));
        }
        final JsonObject updated = Ut.valueJObject(this.updated);
        if (Objects.nonNull(updated.getValue(KName.BY))) {
            set.add(updated.getString(KName.BY));
        }
        return set;
    }

    /**
     * 计算附件字段以及附件配置等相关信息，附件字段定义数据结构如下
     * <pre><code>
     *     {
     *         "attachment": [
     *             {
     *                 "field": "字段属性名",
     *                 "condition": "附件查询条件"
     *             }
     *         ]
     *     }
     * </code></pre>
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<String, JsonObject> fieldFile() {
        final JsonArray attachments = Ut.valueJArray(this.attachment);
        final ConcurrentMap<String, JsonObject> fieldMap = new ConcurrentHashMap<>();
        Ut.itJArray(attachments).forEach(attachment -> {
            final String field = attachment.getString(KName.FIELD);
            final Object value = attachment.getValue("condition");
            if (Ut.isNotNil(field) && value instanceof JsonObject) {
                fieldMap.put(field, (JsonObject) value);
            }
        });
        return fieldMap;
    }

    @Override
    public String toString() {
        return "KField{" +
            "key='" + this.key + '\'' +
            ", unique=" + this.unique +
            ", created=" + this.created +
            ", updated=" + this.updated +
            ", numbers=" + this.numbers +
            ", object=" + this.object +
            ", array=" + this.array +
            ", attachment=" + this.attachment +
            '}';
    }
}

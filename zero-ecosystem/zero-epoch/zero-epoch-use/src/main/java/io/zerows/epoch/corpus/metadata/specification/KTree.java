package io.zerows.epoch.corpus.metadata.specification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;

import java.io.Serializable;

/**
 * 树型配置专用对象，此内容主用于字典配置，最早字典配置名称为 `KDict`，表示字典，字典的核心信息在于翻译，假设：界面上存在一个下拉，这个下拉有值（key）和显示值（display），这两个值从底层存储到上层显示的过程中，需要进行翻译，这个翻译的过程就是字典。
 * 字典现在最新的名字就是 {@link KTree}，主要原因在于字典本身支持 父/子 模型的继承关系，不同继承引起的字典结构变化有所区别，所以使用了 `KTree` 的名字代替原始的 `KDict` 的名字。
 * 字典的基本命名方向如：
 * <pre><code>
 *     in            out
 *     显示端         存储端
 *     display       key
 *     如员工中的部门信息的配置如下
 *     {
 *         "in": "name",
 *         "out": "key",
 *     }
 * </code></pre>
 * 这个概念对初学者可能觉得模糊，因为底层存储的值实际在 out 端，而不是 in，它表示的含义是
 * <pre><code>
 *     1. 如上述示例中，显示端定义的是 name，表示界面上显示的是 name 字段的值，而不是 key 字段的值
 *     2. 而存储端定义的是 key，表示存储的是 key 字段的值，而不是 name 字段的值
 * </code></pre>
 * 整个 Zero Extension 模块中的定义维持统一规则，对外呈现的都是 in，而内部存储全程使用 out，此处还有一个特殊的属性为 region，它用来计算字典的区域，通常是以 X_CATEGORY / X_TABULAR 为基础，计算它的 type 属性相关的值来划分区域，最终的 region 会作为查询条件传入到数据库中。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KTree implements Serializable {
    /** 管理端属性名 */
    private String in;
    /** 存储端属性名 **/
    private String out = KName.KEY;
    /** 当前属性中生效的字典属性 */
    private String field = "parentId";
    /** 当前配置中支持查询条件对字典进行分区的区域配置信息 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject region = new JsonObject();

    public String getIn() {
        return this.in;
    }

    public void setIn(final String in) {
        this.in = in;
    }

    public String getOut() {
        return this.out;
    }

    public void setOut(final String out) {
        this.out = out;
    }

    public String getField() {
        return this.field;
    }

    public void setField(final String field) {
        this.field = field;
    }

    public JsonObject getRegion() {
        return this.region;
    }

    public void setRegion(final JsonObject region) {
        this.region = region;
    }

    /**
     * 专用查询条件的生成方法，如它的定义
     * <pre><code>
     *     {
     *         "tree": {
     *             "in": "code",
     *             "region": {
     *                  "type": "`${type}`"
     *             }
     *         }
     *     }
     * </code></pre>
     * 此时系统中输入数据会包含 type 属性，并解析程基础条件，最终代码中使用如：
     * <pre><code class="java">
     *     final JsonObject criteria = tree.region(in.parameters());
     *     final String keyField = keyValue.key();
     *     criteria.put(keyField + ",i", values);
     *     criteria.put(VString.EMPTY, Boolean.TRUE);
     * </code></pre>
     * 上述代码中构造了当前字典的区域条件，最终会生成如：
     * <pre><code class="SQL">
     *     -- type 限定了字典的数据区域
     *     -- key 限定了字典的记录主键集
     *     WHERE TYPE = `XXX` AND KEY IN [?,?,?]
     * </code></pre>
     *
     * @param parameters 传入的数据信息
     *
     * @return 返回最终的查询条件
     */
    public JsonObject region(final JsonObject parameters) {
        final JsonObject regionData = new JsonObject();
        Ut.<String>itJObject(this.region, (expr, field) -> {
            final String parsed = Ut.fromExpression(expr, parameters);
            if (Ut.isNotNil(parsed)) {
                regionData.put(field, parsed);
            }
        });
        return regionData;
    }
}

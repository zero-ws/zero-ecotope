package io.zerows.epoch.corpus.metadata.specification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.metadata.datamation.KDictConfig;
import io.zerows.metadata.datamation.KDictUse;
import io.zerows.integrated.jackson.databind.JsonObjectDeserializer;
import io.zerows.integrated.jackson.databind.JsonObjectSerializer;
import io.zerows.epoch.program.Ut;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

/**
 * 转换节点专用配置对象，此种转换主要针对特殊的 导入/导出 场景，核心配置场景如下：
 * <pre><code>
 *     1. 默认值场景 initial，导入时可批量提供导入数据某些属性的默认值，支持 JEXL 表达式
 *     2. 树型转换场景 tree，主要针对带有 parentId 的树型结构的导入 / 导出，支持 JEXL 表达式
 *     3. 字典转换场景 fabric，字典转换专用流程
 *     4. 属性映射场景 mapping，当属性有别名时会使用
 * </code></pre>
 * 以上四种场景都是可选的，如果不配置则不会执行对应的转换流程，如果配置了则会执行对应的转换流程。由于白皮书中已经包含了这四种场景的详细说明，此处就不做更加详细的说明了。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KTransform implements Serializable {
    /** 字典转换 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject fabric;

    /** 树型转换 */
    private KTree tree;

    /** 默认值转换 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject initial;

    /** 字段别名 */
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject mapping;

    public JsonObject getFabric() {
        return this.fabric;
    }

    public void setFabric(final JsonObject fabric) {
        this.fabric = fabric;
    }

    public KTree getTree() {
        return this.tree;
    }

    public void setTree(final KTree tree) {
        this.tree = tree;
    }

    public JsonObject getInitial() {
        return Ut.valueJObject(this.initial);
    }

    public void setInitial(final JsonObject initial) {
        this.initial = initial;
    }

    public JsonObject getMapping() {
        return Ut.valueJObject(this.mapping);
    }

    public void setMapping(final JsonObject mapping) {
        this.mapping = mapping;
    }

    public ConcurrentMap<String, KDictUse> epsilon() {
        final JsonObject dictionary = Ut.valueJObject(this.fabric);
        return KDictUse.epsilon(Ut.valueJObject(dictionary.getJsonObject(KName.EPSILON)));
    }

    public KDictConfig source() {
        final JsonObject dictionary = Ut.valueJObject(this.fabric);
        return new KDictConfig(Ut.valueJArray(dictionary.getJsonArray(KName.SOURCE)));
    }
}

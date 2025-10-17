package io.zerows.platform.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.atomic.HCopier;
import io.zerows.specification.atomic.HJson;
import io.zerows.support.base.UtBase;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「字典消费者」字典消费者可以消费字典提供者，关键点：
 * <pre><code>
 *     1. 每个属性只能有一个唯一的概念定义，若它关联字典就只能是一个。
 *     2. 但字典本身是可共享的，两个不同的属性可共享一个字典呈现不同属性。
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class KDictUse implements Serializable, HJson, HCopier<KDictUse> {

    /** 消费的字典名，{@link KDictConfig.Source} 中的 key 定义 */
    private String source;

    /** 显示端属性名 */
    private String in;

    /** 存储端属性名 */
    private String out;

    /** 如果出现继承专用的字典结构 */
    private boolean parent;

    public static ConcurrentMap<String, KDictUse> epsilon(final JsonObject epsilonJson) {
        final ConcurrentMap<String, KDictUse> epsilonMap = new ConcurrentHashMap<>();
        if (UtBase.isNotNil(epsilonJson)) {
            epsilonJson.fieldNames().stream()
                .filter(field -> epsilonJson.getValue(field) instanceof JsonObject)
                .forEach(field -> {
                    final JsonObject fieldData = epsilonJson.getJsonObject(field);
                    final KDictUse epsilon = new KDictUse();
                    epsilon.fromJson(fieldData);
                    epsilonMap.put(field, epsilon);
                });
        }
        return epsilonMap;
    }

    @Override
    public JsonObject toJson() {
        return UtBase.serializeJson(this);
    }

    @Override
    public String toString() {
        return "DictEpsilon{" +
            "source='" + this.source + '\'' +
            ", in='" + this.in + '\'' +
            ", out='" + this.out + '\'' +
            ", parent=" + this.parent +
            '}';
    }

    @Override
    public void fromJson(final JsonObject json) {
        if (UtBase.isNotNil(json)) {
            this.source = json.getString("source");
            this.in = json.getString("in");
            this.out = json.getString("out");
            if (json.containsKey("parent")) {
                this.parent = json.getBoolean("parent");
            } else {
                /*
                 * Not configured, it means current dict should be not Self stored
                 */
                this.parent = false;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KDictUse> CHILD copy() {
        final KDictUse consumer = new KDictUse();
        final JsonObject data = this.toJson().copy();
        consumer.fromJson(data);
        return (CHILD) consumer;
    }

    public boolean isValid() {
        return UtBase.isNotNil(this.in) && UtBase.isNotNil(this.out) && UtBase.isNotNil(this.source);
    }
}

package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.zerows.platform.enums.EmDict;
import io.zerows.support.UtBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「字典定义」（批量）完整字典配置专用对象，此对象存储了 Zero Extension 中常用的几种字典的基础配置信息 {@link EmDict.Type}
 * <pre><code>
 *     1. CATEGORY：对应系统中 X_CATEGORY 表中的基础数据信息（树型字典）
 *     2. TABULAR：对应系统中 X_TABULAR 表中的基础信息（列表字典）
 *     3. ASSIST：可关联任意第三方字段信息，这些字典信息回搭配字典数据读取器来实现数据加载
 *                - 组件 {@see DictionaryPlugin}
 *     4. DAO：直连字典，此处直接绑定了 DAO 类
 *     5. NONE：默认值（禁用字典时可使用此选项）
 * </code></pre>
 * 本类主要用于定义全字典配置，解析当前通道、接口专用的所有字典配置（包含多条规则）
 * <pre><code class="json">
 *     字典提供者（List结构），关联类型 {@link KDictSource}
 *     [
 *         {
 *             "source":"CATEGORY / TABULAR / ASSIST，指定字典类型",
 *             "types": [
 *                 "CATEGORY / TABULAR字典专用，可提取不同的 type = ? 的字典数据"
 *             ],
 *             "key": "字典名称，如 resource.companys",
 *             "component": "（扩展）当您想要扩展整个字典模块时，可采用第三方扩展组件",
 *             "componentConfig": {
 *                 "...": "组件配置"
 *             }
 *         }
 *     ]
 *
 *     字典消费者（Map结构），关联类型 {@link KDictUse}
 *     {
 *         "field1": {
 *             "source": "消费的字典，对应上半段的 key 定义，如 resource.companys",
 *             "in": "显示端属性名，如 name",
 *             "out": "存储端属性名，如 key",
 *         }
 *     }
 *
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KDictConfig implements Serializable {

    private final List<KDictSource> source = new ArrayList<>();
    private final ConcurrentMap<String, KDictUse> consumer = new ConcurrentHashMap<>();
    private Class<?> component;

    public KDictConfig(final String literal) {
        if (UtBase.isJArray(literal)) {
            final JsonArray parameters = new JsonArray(literal);
            UtBase.itJArray(parameters)
                .map(KDictSource::new)
                .forEach(this.source::add);
        }
    }

    public KDictConfig(final JsonArray input) {
        if (Objects.nonNull(input)) {
            UtBase.itJArray(input)
                .map(KDictSource::new)
                .forEach(this.source::add);
        }
    }

    public KDictConfig bind(final Class<?> component) {
        if (Objects.isNull(component)) {
            /*
             * When component not found,
             * clear source data cache to empty list.
             * It's force action here to clear source instead of others
             * 1) If you don't bind Class<?> component, the source will be cleared
             * 2) If you want to bind Class<?> component, it means that all the inited dict
             * will be impact
             */
            this.source.clear();
            this.consumer.clear();
        } else {
            this.component = component;
        }
        return this;
    }

    public KDictConfig bind(final ConcurrentMap<String, KDictUse> epsilon) {
        if (Objects.nonNull(epsilon)) {
            this.consumer.putAll(epsilon);
        }
        return this;
    }

    public boolean validSource() {
        return !this.source.isEmpty();
    }

    public boolean valid() {
        /*
         * When current source is not empty,
         * The `dictComponent` is required here.
         */
        return this.validSource() && Objects.nonNull(this.component);
    }

    public Class<?> configComponent() {
        return this.component;
    }

    public ConcurrentMap<String, KDictUse> configUse() {
        return this.consumer;
    }

    public List<KDictSource> configSource() {
        return this.source;
    }
}

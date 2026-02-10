package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.platform.enums.EmDS;
import io.zerows.specification.atomic.HCopier;
import io.zerows.support.base.UtBase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「字典定义」（批量）完整字典配置专用对象，此对象存储了 Zero Extension 中常用的几种字典的基础配置信息 {@link EmDS.Dictionary}
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
 *     字典提供者（List结构），关联类型 {@link Source}
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

    private final List<Source> source = new ArrayList<>();
    private final ConcurrentMap<String, KDictUse> consumer = new ConcurrentHashMap<>();
    private Class<?> component;

    public KDictConfig(final JsonArray input) {
        if (Objects.nonNull(input)) {
            UtBase.itJArray(input)
                .map(Source::new)
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

    public List<Source> configSource() {
        return this.source;
    }

    /**
     * 「字典提供者」字典提供者用于定义字典源信息，目前支持的常用三大类
     * <pre><code>
     *     1. ASSIST：   辅助字典（复杂模式）
     *                   配合组件从系统重读取字典数据，组件 {@see io.vertx.up.operation.dict.DictionaryPlugin}
     *     2. TABULAR：  列表字典
     *                   type划定区域，从系统中读取 X_TABULAR 表中的数据
     *     3. CATEGORY： 树型字典
     *                   type划定区域，从系统中读取 X_CATEGORY 表中的数据
     * </code></pre>
     * 当前配置结构如下：
     * <pre><code class="json">
     *     {
     *         "source":"CATEGORY / TABULAR / ASSIST，指定字典类型",
     *         "types": [
     *             "CATEGORY / TABULAR字典专用，可提取不同的 type = ? 的字典数据"
     *         ],
     *         "key": "字典名称，如 resource.companys",
     *         "component": "（扩展）当您想要扩展整个字典模块时，可采用第三方扩展组件",
     *         "componentConfig": {
     *             "...": "组件配置"
     *         }
     *     }
     * </code></pre>
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    @Data
    @Slf4j
    public static class Source implements Serializable, HCopier<Source> {

        /**
         * 类型，一般用于 CATEGORY | TABULAR 两类字典从系统中企图数据专用，可同时读取多种类型
         */
        private final Set<String> types = new HashSet<>();


        /**
         * 组件配置，和 component 搭配使用的组件配置节点
         */
        private final JsonObject componentConfig = new JsonObject();


        /**
         * 字典类型，指明当前字典的类型信息
         */
        private EmDS.Dictionary source;


        /**
         * 当前字典名字，消费者专用 {@link KDictUse} 此名字执行消费
         **/
        private String key;


        /**
         * 字典数据提取依托的组件信息
         **/
        private Class<?> component;

        public Source(final JsonObject definition) {
            /*
             * Source normalize for `source type`
             */
            final String source = definition.getString(VName.SOURCE);
            this.source = UtBase.toEnum(() -> source, EmDS.Dictionary.class, EmDS.Dictionary.NONE);
            if (EmDS.Dictionary.CATEGORY == this.source || EmDS.Dictionary.TABULAR == this.source) {
                /*
                 * Different definition for
                 * 1) CATEGORY / TABULAR
                 */
                final JsonArray typeJson = definition.getJsonArray("types");
                if (Objects.nonNull(typeJson)) {
                    typeJson.stream().filter(Objects::nonNull)
                        .map(item -> (String) item)
                        .forEach(this.types::add);
                }
            } else if (EmDS.Dictionary.ASSIST == this.source) {
                /*
                 * Different definition for
                 * ASSIST
                 */
                this.key = definition.getString(VName.KEY);
                final String className = definition.getString(VName.COMPONENT);
                if (UtBase.isNotNil(className)) {
                    this.component = UtBase.clazz(className);
                    if (Objects.isNull(this.component)) {
                        log.warn("[ ZERO ] 字典组件 `{}` 无法初始化！", className);
                    }
                }
                final JsonObject componentConfig = definition.getJsonObject("componentConfig");
                if (UtBase.isNotNil(componentConfig)) {
                    this.componentConfig.mergeIn(componentConfig);
                }
            }
        }

        private Source() {
        }

        public EmDS.Dictionary getSourceType() {
            return this.source;
        }

        public <T> T getPlugin() {
            if (Objects.isNull(this.component)) {
                return null;
            } else {
                return UtBase.singleton(this.component);
            }
        }

        public JsonObject getPluginConfig() {
            return UtBase.valueJObject(this.componentConfig);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <CHILD extends Source> CHILD copy() {
            final Source source = new Source();
            source.component = this.component;
            source.componentConfig.clear();
            source.componentConfig.mergeIn(this.componentConfig.copy());
            source.key = this.key;
            source.source = this.source;
            source.types.addAll(this.types);
            return (CHILD) source;
        }
    }
}

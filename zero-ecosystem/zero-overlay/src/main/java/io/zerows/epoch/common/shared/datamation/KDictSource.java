package io.zerows.epoch.common.shared.datamation;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.constant.VName;
import io.zerows.epoch.enums.EmDict;
import io.zerows.epoch.support.UtBase;
import io.zerows.specification.atomic.HCopier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
public class KDictSource implements Serializable, HCopier<KDictSource> {
    private static final Annal LOGGER = Annal.get(KDictSource.class);


    /** 类型，一般用于 CATEGORY | TABULAR 两类字典从系统中企图数据专用，可同时读取多种类型 */
    private final Set<String> types = new HashSet<>();


    /** 组件配置，和 component 搭配使用的组件配置节点 */
    private final JsonObject componentConfig = new JsonObject();


    /** 字典类型，指明当前字典的类型信息 */
    private EmDict.Type source;


    /** 当前字典名字，消费者专用 {@link KDictUse} 此名字执行消费 **/
    private String key;


    /** 字典数据提取依托的组件信息 **/
    private Class<?> component;

    public KDictSource(final JsonObject definition) {
        /*
         * Source normalize for `source type`
         */
        final String source = definition.getString(VName.SOURCE);
        this.source = UtBase.toEnum(() -> source, EmDict.Type.class, EmDict.Type.NONE);
        if (EmDict.Type.CATEGORY == this.source || EmDict.Type.TABULAR == this.source) {
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
        } else if (EmDict.Type.ASSIST == this.source) {
            /*
             * Different definition for
             * ASSIST
             */
            this.key = definition.getString(VName.KEY);
            final String className = definition.getString(VName.COMPONENT);
            if (UtBase.isNotNil(className)) {
                this.component = UtBase.clazz(className);
                if (Objects.isNull(this.component)) {
                    LOGGER.warn("The component `{0}` could not be initialized", className);
                }
            }
            final JsonObject componentConfig = definition.getJsonObject("componentConfig");
            if (UtBase.isNotNil(componentConfig)) {
                this.componentConfig.mergeIn(componentConfig);
            }
        }
    }

    private KDictSource() {
    }

    public EmDict.Type getSourceType() {
        return this.source;
    }

    public Set<String> getTypes() {
        return this.types;
    }

    public String getKey() {
        return this.key;
    }

    public <T> T getPlugin() {
        if (Objects.isNull(this.component)) {
            return null;
        } else {
            return UtBase.singleton(this.component);
        }
    }

    public Class<?> getComponent() {
        return this.component;
    }

    public JsonObject getPluginConfig() {
        return UtBase.valueJObject(this.componentConfig);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KDictSource> CHILD copy() {
        final KDictSource source = new KDictSource();
        source.component = this.component;
        source.componentConfig.clear();
        source.componentConfig.mergeIn(this.componentConfig.copy());
        source.key = this.key;
        source.source = this.source;
        source.types.addAll(this.types);
        return (CHILD) source;
    }
}

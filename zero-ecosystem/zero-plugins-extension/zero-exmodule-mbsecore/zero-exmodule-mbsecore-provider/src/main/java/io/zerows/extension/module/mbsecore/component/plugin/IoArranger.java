package io.zerows.extension.module.mbsecore.component.plugin;

import io.r2mo.typed.common.Kv;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.MMComponent;
import io.zerows.extension.module.mbsecore.domain.tables.pojos.MAttribute;
import io.zerows.extension.module.mbsecore.metadata.Model;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.extension.module.mbsecore.metadata.element.DataTpl;
import io.zerows.specification.modeling.HAttribute;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.property.*;
import io.zerows.support.TiConsumer;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * ## Manager of ...
 * <p>
 * This class will parse DataTpl for standard workflow
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class IoArranger {

    /**
     * Extract `IComponent` here.
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginIn(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getInComponent, IoArranger::notAop, IComponent.class);
    }

    /**
     * Extract `IComponent` here. ( Before )
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginInBefore(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getInComponent, IoArranger::isAopBefore, IComponent.class);
    }

    /**
     * Extract `IComponent` here. ( After )
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginInAfter(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getInComponent, IoArranger::isAopAfter, IComponent.class);
    }

    /**
     * Extract `OComponent` here.
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginOut(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getOutComponent, IoArranger::notAop, OComponent.class);
    }

    /**
     * Extract `OComponent` here.( Before )
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginOutBefore(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getOutComponent, IoArranger::isAopBefore, OComponent.class);
    }

    /**
     * Extract `OComponent` here.( After )
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginOutAfter(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getOutComponent, IoArranger::isAopAfter, OComponent.class);
    }

    /**
     * Extract `INormalizer` here.
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginNormalize(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getNormalize, IoArranger::notAop, INormalizer.class);
    }

    /**
     * Extract `OExpression` here.
     *
     * @param tpl {@link DataTpl}
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    static ConcurrentMap<String, MMComponent> pluginExpression(final DataTpl tpl) {
        return extractPlugin(tpl, MAttribute::getExpression, IoArranger::notAop, OExpression.class);
    }

    /**
     * Not AOP configuration ( sourceField != BEFORE && AFTER )
     *
     * @param attribute {@link MAttribute} Input attribute object that will be checked.
     * @return {@link java.lang.Boolean}
     */
    private static boolean notAop(final MAttribute attribute) {
        return !isAop(attribute);
    }

    /**
     * AOP configuration ( sourceField == BEFORE || AFTER )
     *
     * @param attribute {@link MAttribute} Input attribute object that will be checked.
     * @return {@link java.lang.Boolean}
     */
    private static boolean isAop(final MAttribute attribute) {
        return isAopAfter(attribute) || isAopBefore(attribute);
    }

    /**
     * AOP configuration ( sourceField == BEFORE  )
     *
     * @param attribute {@link MAttribute} Input attribute object that will be checked.
     * @return {@link java.lang.Boolean}
     */
    private static boolean isAopBefore(final MAttribute attribute) {
        final String sourceField = attribute.getSourceField();
        return KName.Modeling.VALUE_BEFORE.equals(sourceField);
    }

    /**
     * AOP configuration ( sourceField == BEFORE  )
     *
     * @param attribute {@link MAttribute} Input attribute object that will be checked.
     * @return {@link java.lang.Boolean}
     */
    private static boolean isAopAfter(final MAttribute attribute) {
        final String sourceField = attribute.getSourceField();
        return KName.Modeling.VALUE_AFTER.equals(sourceField);
    }


    /**
     * @param tpl          {@link DataTpl} The model definition template in data event
     * @param fnComponent  {@link java.util.function.Function} The component name extract from.
     * @param fnFilter     {@link java.util.function.Function} The filter for attribute processing.
     * @param interfaceCls {@link java.lang.Class} required interface class
     * @return {@link java.util.concurrent.ConcurrentMap} The JComponent map for each field.
     */
    private static ConcurrentMap<String, MMComponent> extractPlugin(
        final DataTpl tpl, final Function<MAttribute, String> fnComponent,
        final Function<MAttribute, Boolean> fnFilter, final Class<?> interfaceCls) {
        /*
         * 1. Iterate tpl attributes.
         */
        final Model model = tpl.atom().model();
        final ConcurrentMap<String, MMComponent> pluginMap = new ConcurrentHashMap<>();
        final Function<MAttribute, Boolean> fnSelect = Objects.isNull(fnFilter) ? attribute -> Boolean.TRUE : fnFilter;
        model.dbAttributes().stream().filter(fnSelect::apply).forEach(attribute -> {
            /*
             * 2. Attribute
             */
            final String componentName = fnComponent.apply(attribute);
            final Class<?> componentCls = Ut.clazz(componentName, null);
            if (Objects.nonNull(componentCls)) {
                /*
                 * 3. SourceConfig
                 */
                final MMComponent component = MMComponent.create(attribute.getName(), componentCls);
                if (component.isImplement(interfaceCls)) {
                    final JsonObject config = componentConfig(attribute, tpl.atom(), componentCls);
                    pluginMap.put(attribute.getName(), component.bind(config));
                }
            }
        });
        return pluginMap;
    }

    /**
     * Extract component configuration from attribute `sourceConfig` instead of other place.
     * <p>
     * 1. sourceConfig stored component configuration.
     * 2. The json configuration came from `field = componentCls`.
     * <p>
     * ```json
     * // <pre><code class="json">
     * {
     *     "attribute": {
     *          "name": "xxx",
     *          "alias": "Text",
     *          "format": "JsonArray | JsonObject | Elementary"
     *     },
     *     "source": "来源模型identifier",
     *     "sourceField": "来源属性信息",
     *     "plugin.io": {
     *
     *     }
     * }
     * // </code></pre>
     * ```
     *
     * @param attribute    {@link MAttribute} Processed attribute that are related to `M_ATTRIBUTE`
     * @param componentCls {@link java.lang.Class} The component class of type
     * @return {@link JsonObject} The extracted configuration of current component.
     */
    private static JsonObject componentConfig(final MAttribute attribute, final DataAtom atom, final Class<?> componentCls) {
        final JsonObject sourceConfig = Ut.toJObject(attribute.getSourceConfig());
        final JsonObject combine;
        if (sourceConfig.containsKey(KName.PLUGIN_IO)) {
            final JsonObject pluginConfig = Ut.toJObject(sourceConfig.getValue(KName.PLUGIN_IO));
            if (pluginConfig.containsKey(componentCls.getName())) {
                combine = Ut.toJObject(pluginConfig.getValue(componentCls.getName()));
            } else {
                combine = new JsonObject();
            }
        } else {
            combine = new JsonObject();
        }
        final HAttribute aoAttr = atom.attribute(attribute.getName());
        final JsonObject attrJson = new JsonObject();
        attrJson.put(KName.NAME, attribute.getName());
        attrJson.put(KName.ALIAS, attribute.getAlias());
        attrJson.put(KName.FORMAT, aoAttr.format());

        combine.put(KName.ATTRIBUTE, attrJson);
        combine.put(KName.SOURCE, attribute.getSource());
        combine.put(KName.SOURCE_FIELD, attribute.getSourceField());
        /*
         *  sourceParams
         */
        final JsonObject params = new JsonObject();
        params.put(KName.SIGMA, attribute.getSigma());
        combine.put(KName.SOURCE_PARAMS, params);
        return combine;
    }

    // --------------------------- Execute The Workflow ------------------------
    private static <T extends IoSource> JsonObject sourceData(final ConcurrentMap<String, MMComponent> inMap,
                                                              final Class<?> interfaceCls) {
        /*
         * Source Data Convert
         */
        final ConcurrentMap<String, MMComponent> componentMap = new ConcurrentHashMap<>();
        inMap.values().forEach(component -> componentMap.put(component.keyUnique(), component));
        /*
         * Source Data Process
         */
        final JsonObject sourceData = new JsonObject();
        if (Objects.nonNull(interfaceCls)) {
            componentMap.values().forEach(component -> {
                final T componentInstance = component.instance(interfaceCls);
                if (Objects.nonNull(componentInstance)) {
                    /*
                     * Source Data Extract ( Code Logical )
                     */
                    componentInstance.source(component.getConfig()).forEach(sourceData::put);
                }
            });
        }
        return sourceData;
    }

    static void runNorm(final HRecord[] records, final ConcurrentMap<String, MMComponent> inMap) {
        run(records, inMap, null, (processed, component, config) -> {
            final INormalizer reference = component.instance(INormalizer.class);
            Arrays.stream(records).forEach(record -> run(record, component, reference::before));
        });
    }

    static void runNorm(final HRecord record, final ConcurrentMap<String, MMComponent> normalizeMap) {
        run(record, normalizeMap, null, (processed, component, config) -> {
            final INormalizer reference = component.instance(INormalizer.class);
            run(record, component, reference::before);
        });
    }

    static void runExpr(final HRecord[] records, final ConcurrentMap<String, MMComponent> inMap) {
        run(records, inMap, null, (processed, component, config) -> {
            final OExpression reference = component.instance(OExpression.class);
            Arrays.stream(records).forEach(record -> run(record, component, reference::after));
        });
    }

    static void runExpr(final HRecord record, final ConcurrentMap<String, MMComponent> normalizeMap) {
        run(record, normalizeMap, null, (processed, component, config) -> {
            final OExpression reference = component.instance(OExpression.class);
            run(record, component, reference::after);
        });
    }

    static void runIn(final HRecord record, final ConcurrentMap<String, MMComponent> inMap) {
        run(record, inMap, IComponent.class, (processed, component, config) -> {
            final IComponent reference = component.instance(IComponent.class);
            run(record, component, kv -> reference.before(kv, record, config));
        });
    }

    static void runIn(final HRecord[] records, final ConcurrentMap<String, MMComponent> inMap) {
        run(records, inMap, IComponent.class, (processed, component, config) -> {
            final IComponent reference = component.instance(IComponent.class);
            Arrays.stream(records).forEach(record -> run(record, component, kv -> reference.before(kv, record, config)));
        });
    }

    static void runOut(final HRecord record, final ConcurrentMap<String, MMComponent> inMap) {
        run(record, inMap, OComponent.class, (processed, component, config) -> {
            final OComponent reference = component.instance(OComponent.class);
            run(record, component, kv -> reference.after(kv, record, config));
        });
    }

    static void runOut(final HRecord[] records, final ConcurrentMap<String, MMComponent> inMap) {
        run(records, inMap, OComponent.class, (processed, component, config) -> {
            final OComponent reference = component.instance(OComponent.class);
            Arrays.stream(records).forEach(record -> run(record, component, kv -> reference.after(kv, record, config)));
        });
    }

    /* Post Run */
    private static void run(final HRecord record, final MMComponent component,
                            final Function<Kv<String, Object>, Object> executor) {
        final String field = component.key();
        final Kv<String, Object> kv = Kv.create(field, record.get(field));
        final Object processedValue = executor.apply(kv);
        if (Objects.nonNull(processedValue)) {
            /*
             * Replace original data
             * debugStory: change record.set to record.attach, because processedValue could be
             *  in a different class with kv.findRunning
             */
            record.attach(field, processedValue);
        }
    }

    /* Top Run */
    private static <T> void run(final T input, final ConcurrentMap<String, MMComponent> inMap,
                                final Class<?> interfaceCls,
                                final TiConsumer<T, MMComponent, JsonObject> consumer) {
        if (!inMap.isEmpty()) {
            final JsonObject dataMap = sourceData(inMap, interfaceCls);
            inMap.forEach((field, component) -> {
                final JsonObject config = component.getConfig();
                config.put(KName.SOURCE_DATA, dataMap);
                consumer.accept(input, component, config);
            });
        }
    }
}

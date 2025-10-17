package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.platform.enums.modeling.EmAttribute;
import io.zerows.platform.enums.modeling.EmValue;
import io.zerows.specification.modeling.HAttribute;
import io.zerows.specification.modeling.metadata.HMetaField;
import io.zerows.support.base.UtBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KAttribute implements HAttribute, Serializable {
    private final EmValue.Format format;

    private final List<HMetaField> shapes = new ArrayList<>();

    private final HMetaField type;
    private final KMarkAtom.Attribute tag;
    private RRule rule;

    /*
     * Data Structure of Matrix
     * {
     *     "name",
     *     "alias",
     *     "type",
     *     "formatFail": "JsonArray, JsonObject, Elementary",
     *     "fields": [
     *         {
     *              "field": "",
     *              "alias": "",
     *              "type": "null -> String.class | ???"
     *         }
     *     ],
     *     "rule": {
     *     }
     * }
     */
    public KAttribute(final JsonObject config, final KMarkAtom.Attribute tag) {
        this.tag = tag;
        /*
         * Extract DataFormat from `formatFail` field in config，
         * Here are formatFail adjustment:
         * 1. Priority 1: isArray = true, the formatFail is `JsonArray`.
         * 2. Priority 2: isArray = false, set the default get instead ( Elementary )
         */
        EmValue.Format format = UtBase.toEnum(() -> config.getString(VName.FORMAT), EmValue.Format.class, EmValue.Format.Elementary);
        if (tag.value(EmAttribute.Marker.array)) {
            format = EmValue.Format.JsonArray;
        }
        this.format = format;

        /*
         * Here the type must be fixed or null
         */
        final Class<?> type = UtBase.clazz(config.getString(VName.TYPE), String.class);
        final String name = config.getString(VName.NAME);
        final String alias = config.getString(VName.ALIAS);
        this.type = HMetaField.of(name, alias, type);

        /*
         * Format is not elementary, expand the `fields` lookup range
         * instead of simple, then add children into HTField for complex
         */
        if (EmValue.Format.Elementary != format) {
            final JsonArray fields = UtBase.valueJArray(config.getJsonArray(VName.FIELDS));
            UtBase.itJArray(fields).forEach(item -> {
                final String field = item.getString(VName.FIELD);
                if (UtBase.isNotNil(field)) {
                    final String fieldAlias = item.getString(VName.ALIAS, null);
                    final Class<?> subType = UtBase.clazz(item.getString(VName.TYPE), String.class);
                    this.shapes.add(HMetaField.of(field, fieldAlias, subType));
                }
            });
            this.type.add(this.shapes);
        }

        /*
         * Bind `rule` processing, the `rule` should be configured in config instead of
         */
        if (config.containsKey(VName.RULE)) {
            final JsonObject ruleJ = UtBase.valueJObject(config, VName.RULE);
            this.rule = UtBase.deserialize(ruleJ, RRule.class);
            /* Bind type into rule */
            this.rule.type(this.type.type());
            /* Unique rule for diffSet */
            this.type.key(this.rule.getUnique());
            // this.type.?uleUnique(this.rule.getUnique());
        }
    }

    @Override
    public RRule referenceRule() {
        return this.rule;
    }

    @Override
    public KMarkAtom.Attribute marker() {
        return this.tag;
    }

    @Override
    public EmValue.Format format() {
        return this.format;
    }

    @Override
    public HMetaField field() {
        return this.type;
    }

    @Override
    public List<HMetaField> fieldCompiled() {
        return this.shapes;
    }
}

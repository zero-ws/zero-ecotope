package io.zerows.extension.runtime.crud.util;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.epoch.database.jooq.util.JqAnalyzer;
import io.zerows.epoch.corpus.mbse.atom.specification.KModule;
import io.zerows.epoch.metadata.specification.KField;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.specification.modeling.metadata.HMetaAtom;
import io.zerows.specification.modeling.metadata.HMetaField;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class IxData {

    static Kv<String, HttpMethod> impact(final IxMod in) {
        final KModule module = in.module();
        final String pattern = "/api/{0}/search";
        final String actor = module.getName();
        return Kv.create(MessageFormat.format(pattern, actor), HttpMethod.POST);
    }

    static Kv<String, String> column(final Object value) {
        if (KWeb.ARGS.V_HOLDER.equals(value)) {
            return null;
        }
        final String field;
        final String fieldValue;
        if (value instanceof String) {
            // metadata
            field = value.toString().split(",")[0];
            fieldValue = value.toString().split(",")[1];
        } else {
            final JsonObject column = (JsonObject) value;
            if (column.containsKey(KName.METADATA)) {
                // metadata
                final String metadata = column.getString(KName.METADATA);
                if (Ut.isNotNil(metadata)) {
                    field = metadata.split(",")[0];
                    fieldValue = value.toString().split(",")[1];
                } else {
                    field = null;
                    fieldValue = null;
                }
            } else {
                // dataIndex
                field = column.getString(IxPin.getColumnKey());
                fieldValue = column.getString(IxPin.getColumnLabel());
            }
        }
        if (Objects.nonNull(field) && Objects.nonNull(fieldValue)) {
            return Kv.create(field, fieldValue);
        } else {
            return null;
        }
    }

    static JsonArray matrix(final KField field) {
        final JsonArray priority = new JsonArray();
        final String keyField = field.getKey();
        /*
         * Add key into group as the high est priority
         */
        priority.add(new JsonArray().add(keyField));
        final JsonArray matrix = Ut.valueJArray(field.getUnique());
        priority.addAll(matrix);
        return priority;
    }

    static JsonObject parameters(final IxMod in) {
        /*
         * module seeking
         * 1. Checking connect module to see whether it's defined in crud configuration
         * 2. When it's null, ( Half Processing )
         *      -- Check the `module` parameters first
         * 3. The last part is current `module` identifier ( such as `tabular` )
         */
        final JsonObject parameters = in.parameters();
        if (!parameters.containsKey(KName.MODULE)) {
            final KModule module = in.module();
            final KModule connect = in.connected();
            if (Objects.isNull(connect)) {
                parameters.put(KName.MODULE, module.identifier());
            } else {
                parameters.put(KName.MODULE, connect.identifier());
            }
        }
        return parameters;
    }

    static HMetaAtom atom(final IxMod active, final JsonArray columns) {
        final ConcurrentMap<String, String> headers = new ConcurrentHashMap<>();
        columns.stream().map(Ix::onColumn).filter(Objects::nonNull).forEach(kv -> {
            /* Calculated */
            headers.put(kv.key(), kv.value());
        });
        /*
         * First module for calculation
         */
        final HMetaAtom atom = HMetaAtom.of();
        final KModule module = active.module();
        final List<HMetaField> fieldList = new ArrayList<>();

        final KModule connect = active.connected();
        if (Objects.nonNull(connect)) {
            fieldList.addAll(column(connect, active.envelop(), headers));
        }
        fieldList.addAll(column(module, active.envelop(), headers));

        fieldList.forEach(atom::add);
        return atom;
    }

    private static List<HMetaField> column(final KModule module, final Envelop envelop,
                                           final ConcurrentMap<String, String> headerMap) {
        final UxJooq jooq = IxPin.jooq(module, envelop);
        final JqAnalyzer analyzer = jooq.analyzer();
        final ConcurrentMap<String, Class<?>> typeMap = analyzer.types();
        /*
         * Processing for TypeField list building
         */
        final List<HMetaField> fieldList = new ArrayList<>();
        headerMap.forEach((field, alias) -> {
            final Class<?> type = typeMap.getOrDefault(field, String.class);
            fieldList.add(HMetaField.of(field, alias, type));
        });
        return fieldList;
    }
}

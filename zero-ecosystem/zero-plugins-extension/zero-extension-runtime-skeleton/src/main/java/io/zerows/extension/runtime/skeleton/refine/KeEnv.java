package io.zerows.extension.runtime.skeleton.refine;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.epoch.metadata.MMAdapt;
import io.zerows.epoch.metadata.UArray;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.platform.constant.VName;
import io.zerows.program.Ux;
import io.zerows.spi.modeler.Indent;
import io.zerows.support.Ut;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class KeEnv {

    private static final Set<String> FIELDS = new HashSet<>() {
        {
            this.add(KName.SIGMA);
            this.add(KName.LANGUAGE);
            this.add(KName.ACTIVE);
            this.add(KName.CREATED_AT);
            this.add(KName.CREATED_BY);
            this.add(KName.UPDATED_AT);
            this.add(KName.UPDATED_BY);
        }
    };

    static void auditJ(final JsonObject body, final User user) {
        if (Ut.isNotNil(body) && Objects.nonNull(user)) {
            final String key = Ux.keyUser(user);
            if (Ut.isNotNil(key)) {
                body.put(KName.CREATED_BY, key);
                body.put(KName.CREATED_AT, Instant.now());

                body.put(KName.UPDATED_BY, key);
                body.put(KName.UPDATED_AT, Instant.now());
            }
        }
    }

    static <T, I> void audit(final I output, final String outPojo, final T input, final String inPojo, final boolean isUpdated) {
        // If contains pojo, must be deserialized for auditor information
        // OutMap Calculation
        final ConcurrentMap<String, String> outMap = buildMap(outPojo, isUpdated);
        final ConcurrentMap<String, String> inMap = buildMap(inPojo, isUpdated);
        /* Mapping */
        final LocalDateTime now = LocalDateTime.now();
        outMap.forEach((key, out) -> {
            final String in = inMap.get(key);
            if (KName.CREATED_AT.equals(in) || KName.UPDATED_AT.equals(in)) {
                if (output instanceof JsonObject) {
                    // Instant          ( For JsonObject Only )
                    ((JsonObject) output).put(out, Instant.now());
                } else {
                    // LocalDataTime
                    Ut.field(output, out, now);
                }
            } else {
                // Extract input data
                final Object value;
                if (input instanceof JsonObject) {
                    value = ((JsonObject) input).getValue(in);
                } else {
                    value = Ut.field(input, in);
                }
                if (output instanceof JsonObject) {
                    // ( For JsonObject Only )
                    ((JsonObject) output).put(out, value);
                } else {
                    Ut.field(output, out, value);
                }
            }
        });
    }

    private static ConcurrentMap<String, String> buildMap(final String filename, final boolean isUpdated) {
        final ConcurrentMap<String, String> vector = new ConcurrentHashMap<>();
        if (Ut.isNil(filename)) {
            FIELDS.forEach(each -> vector.put(each, each));
        } else {
            // app -> zApp 反序（zApp 是实体类字段）
            MMAdapt.of(filename).mapBy(FIELDS::contains, vector::put);
        }
        if (isUpdated) {
            vector.remove(KName.CREATED_BY);
            vector.remove(KName.CREATED_AT);
        }
        return vector;
    }

    static <T> Future<List<T>> indent(final List<T> input, final String sigma,
                                      final String code,
                                      final BiConsumer<T, String> fnConsumer) {
        if (Objects.isNull(input) || input.isEmpty()) {
            return Ux.future(new ArrayList<>());
        } else {
            return Ux.channel(Indent.class, () -> input, stub ->
                stub.indent(code, sigma, input.size()).compose(queue -> {
                    input.forEach(entity -> fnConsumer.accept(entity, queue.poll()));
                    return Ux.future(input);
                }));
        }
    }

    static <T> Future<T> indent(final T input, final String sigma,
                                final String code,
                                final BiConsumer<T, String> fnConsumer) {
        return Ux.channel(Indent.class, () -> input, stub -> {
            if (Ut.isNil(sigma)) {
                return Ux.future(input);
            } else {
                return stub.indent(code, sigma)
                    .compose(indent -> {
                        fnConsumer.accept(input, indent);
                        return Ux.future(input);
                    });
            }
        });
    }

    /*
     * Indent Single
     */
    static Future<JsonObject> indent(final JsonObject data, final String code) {
        return Ux.channel(Indent.class, () -> data, stub -> {
            final String sigma = data.getString(KName.SIGMA);
            if (Ut.isNil(sigma) || Ut.isNil(code)) {
                return Ux.future(data);
            } else {
                return stub.indent(code, sigma)
                    .compose(indent -> Ux.future(data.put(KName.INDENT, indent)));
            }
        });
    }

    static Future<JsonArray> indent(final JsonArray data, final String code) {
        return Ux.channel(Indent.class, () -> data, stub -> {
            final String sigma = Ut.valueString(data, KName.SIGMA);
            if (Ut.isNil(sigma)) {
                return Ux.future(data);
            } else {
                return stub.indent(code, sigma, data.size())
                    .compose(indentQ -> {
                        Ut.itJArray(data).forEach(json -> json.put(KName.INDENT, indentQ.poll()));
                        return Ux.future(data);
                    });
            }
        });
    }

    static <T> Future<JsonArray> daoR(final String field, final String key, final Class<?> daoCls) {
        return DB.on(daoCls).<T>fetchAsync(field, key)
            .compose(Ux::futureA)
            .compose(relation -> UArray.create(relation)
                .remove(field).toFuture());
    }

    static <T> Future<List<T>> daoR(final String field, final String key, final Class<?> daoCls, final Function<T, Integer> priorityFn) {
        return DB.on(daoCls).<T>fetchAsync(field, key)
            .compose(result -> {
                result.sort(Comparator.comparing(priorityFn));
                return Ux.future(result);
            });
    }

    /*
     * config data structure
     * {
     *     "dao": "className",
     *     "pojo": "pojoFile",
     *     "criteria": {
     *     }
     * }
     */
    static Future<JsonObject> daoJ(final JsonObject config, final JsonObject params) {
        return daoT(config, JsonObject::new, jq -> {
            final JsonObject exprTpl = Ut.valueJObject(config, VName.KEY_CRITERIA);
            final JsonObject condition = Ut.fromExpression(exprTpl, params);
            return jq.fetchJOneAsync(condition);
        });
    }

    static Future<JsonArray> daoA(final JsonObject config, final JsonObject params) {
        return daoT(config, JsonArray::new, jq -> {
            final JsonObject exprTpl = Ut.valueJObject(config, VName.KEY_CRITERIA);
            final JsonObject condition = Ut.fromExpression(exprTpl, params);
            return jq.fetchJAsync(condition);
        });
    }

    private static <T> Future<T> daoT(final JsonObject config,
                                      final Supplier<T> supplier,
                                      final Function<DBJooq, Future<T>> executor) {
        final JsonObject safeJ = Ut.valueJObject(config);
        final String clazz = safeJ.getString(KName.DAO, null);
        if (Ut.isNil(clazz)) {
            // clazz = null, default workflow
            return Ux.future(supplier.get());
        }
        final Class<?> daoCls = Ut.clazz(clazz, null);
        if (Objects.isNull(daoCls)) {
            // clazz could not be found, default workflow
            return Ux.future(supplier.get());
        }
        final DBJooq jq = DB.on(daoCls);
        final String pojo = safeJ.getString("pojo", null);
        if (Ut.isNotNil(pojo)) {
            jq.on(pojo);
        }
        return executor.apply(jq);
    }
}

package io.zerows.support;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.OLog;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.support.fn.Fx;
import io.zerows.specification.modeling.HRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * For comparing
 */
@SuppressWarnings("all")
final class Compare {
    private static final OLog LOGGER = _Log.Log.ux(Compare.class);

    private Compare() {
    }

    static int compareTo(final int left, final int right) {
        return left - right;
    }

    static int compareTo(final String left, final String right) {
        return compareTo(left, right,
            (leftVal, rightVal) -> leftVal.compareTo(rightVal));
    }

    static <T> int compareTo(
        final T left, final T right,
        final BiFunction<T, T, Integer> compare) {
        if (null == left && null == right) {
            return 0;
        } else if (null == left && null != right) {
            return -1;
        } else if (null != left && null == right) {
            return 1;
        } else {
            return compare.apply(left, right);
        }
    }

    /**
     * @author lang : 2024-04-19
     */
    static class J {
        private static final OLog LOG = _Log.Log.ux(J.class);

        // ------------------------- Compare InJson ------------------------
        static JsonArray ruleJReduce(final JsonArray records, final JsonArray matrix) {
            final JsonArray normalized = new JsonArray();
            Ut.itJArray(records).filter(json -> ruleJOk(json, matrix)).forEach(normalized::add);
            logReduce(records, normalized);
            return normalized;
        }

        static JsonArray ruleJReduce(final JsonArray records, final Set<String> fields) {
            final JsonArray normalized = new JsonArray();
            Ut.itJArray(records).filter(json -> ruleJOk(json, fields)).forEach(normalized::add);
            logReduce(records, normalized);
            return normalized;
        }

        private static void logReduce(final JsonArray records, final JsonArray normalized) {
            final int input = records.size();
            final int output = normalized.size();
            if (input != output) {
                LOGGER.info("[ RD ] Compress: {0}/{1}",
                    String.valueOf(output), String.valueOf(input));
            }
        }

        static boolean ruleJOk(final JsonObject record, final Set<String> fields) {
            /*
             * All the uniqueFields must contain value
             */
            return fields.stream().allMatch(field ->
                Objects.nonNull(record.getValue(field)));
        }

        static boolean ruleJOk(final JsonObject record, final JsonArray matrix) {
            /*
             * Matrix may be multi groups
             */
            final int size = matrix.size();
            for (int idx = 0; idx < size; idx++) {
                final Object value = matrix.getValue(idx);
                final Set<String> fields = fieldSet(value);
                if (fields.isEmpty()) {
                    /*
                     * Not unique defined, monitor the reduce processing:
                     */
                    LOGGER.warn("[ RD ] Reduce Process: Fields is empty !!! matrix = {0}",
                        matrix.encode());
                    return false;
                }


                /*
                 * Compare each group for matrix, monitor the reduce processing:
                 */
                final boolean match = ruleJOk(record, fields);
                if (!match) {
                    LOGGER.warn("[ RD ] Reduce Process: Fields is `{0}` matrix = {1}",
                        Ut.fromJoin(fields), matrix.encode());
                    return false;
                }
            }
            return true;
        }

        /*
         * Here should be some comments for group unique rules
         * 1. When you check whether the data is OK, here should be:
         *    ----> Unique 1  --> Ok
         *    ----> Unique 2  --> Ok   ----- All the rule should be Ok
         *    ----> Unique 2  --> Ok
         * 2. When you want to match whether the two record are equal, here should be:
         *    ----> Unique 1  --> Match
         *    ----> Unique 2  --> Not    ----- Any rule should be matched
         *    ----> Unique 3  --> Match
         *    Here are the priority of each Unique Rule,
         *    The situation is often
         *    1)  Primary Key
         *    2)  Unique Key
         */
        static boolean ruleJEqual(final JsonObject record, final JsonObject latest,
                                  final JsonArray matrix) {
            /*
             * Matrix may be multi groups
             */
            final int size = matrix.size();
            for (int idx = 0; idx < size; idx++) {
                final Object value = matrix.getValue(idx);
                final Set<String> fields = fieldSet(value);
                if (fields.isEmpty()) {
                    /*
                     * Not unique defined, check the next
                     * rule here.
                     */
                    continue;
                }
                /*
                 * Compare each group for matrix
                 * Find any one rule should be OK here for equal
                 * 1) Primary Key    - 0
                 * 2) Unique Key     - 1
                 */
                final boolean equal = ruleJEqual(record, latest, fields);
                if (equal) {
                    return true;
                }
            }
            return false;
        }

        static boolean ruleJEqual(final JsonObject record, final JsonObject latest,
                                  final Set<String> fields) {
            final JsonObject subR = Ut.elementSubset(record, fields);
            final JsonObject subL = Ut.elementSubset(latest, fields);
            return subR.equals(subL);
        }

        static JsonObject ruleJFind(final JsonArray source, final JsonObject expected,
                                    final Set<String> fields) {
            return Ut.itJArray(source).filter(json -> ruleJEqual(json, expected, fields))
                .findAny().orElse(new JsonObject());
        }

        static JsonObject ruleJFind(final JsonArray source, final JsonObject expected,
                                    final JsonArray matrix) {
            return Ut.itJArray(source).filter(json -> ruleJEqual(json, expected, matrix))
                .findAny().orElse(new JsonObject());
        }

        private static Set<String> fieldSet(final Object value) {
            final Set<String> fields;
            if (value instanceof JsonArray) {
                fields = Ut.toSet((JsonArray) value);
            } else if (value instanceof String) {
                fields = new HashSet<>();
                fields.add((String) value);
            } else {
                fields = new HashSet<>();
            }
            return fields;
        }

        static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
            final JsonArray original, final JsonArray current, final Set<String> fields) {
            return compareJ(original, current,
                (source, record) -> ruleJFind(source, record, fields));
        }

        static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
            final JsonArray original, final JsonArray current, final String field) {
            return J.compareJ(original, current, new HashSet<>() {
                {
                    this.add(field);
                }
            });
        }

        static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
            final JsonArray original, final JsonArray current, final JsonArray matrix) {
            return compareJ(original, current,
                (source, record) -> ruleJFind(source, record, matrix));
        }

        private static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
            final JsonArray original, final JsonArray current,
            // 2 Function
            final BiFunction<JsonArray, JsonObject, JsonObject> findFn
        ) {
            final ConcurrentMap<ChangeFlag, JsonArray> result = new ConcurrentHashMap<>();
            result.put(ChangeFlag.UPDATE, new JsonArray());
            result.put(ChangeFlag.ADD, new JsonArray());
            result.put(ChangeFlag.DELETE, new JsonArray());
            Ut.itJArray(original).forEach(recordO -> {
                final JsonObject recordN = findFn.apply(current, recordO);
                if (Ut.isNil(recordN)) {
                    // New: x, Old: o
                    result.get(ChangeFlag.DELETE).add(recordO);
                } else {
                    // New: o, Old: o
                    // Do not overwrite `key` field because is primary key
                    final JsonObject recordNC = recordN.copy();
                    if (recordNC.containsKey(KName.KEY)) {
                        recordNC.remove(KName.KEY);
                    }
                    final JsonObject combine = recordO.copy().mergeIn(recordNC, true);
                    result.get(ChangeFlag.UPDATE).add(combine);
                }
            });
            Ut.itJArray(current).forEach(recordN -> {
                final JsonObject recordO = findFn.apply(original, recordN);
                if (Ut.isNil(recordO)) {
                    // New: o, Old: x
                    result.get(ChangeFlag.ADD).add(recordN);
                }
            });
            return result;
        }
    }

    /*
     * Compare two entity collection for
     * 1) Get `ADD` operation
     * 2) Get `UPDATE` operation
     * 3) Get `DELETE` operation
     */
    static class T {
        /*
         * The uniqueSet contains all unique fields
         */
        static <T> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current,
                                                              final Set<String> uniqueSet,
                                                              final String pojoFile) {
            return compare(original, current, (entity) -> {
                /*
                 * The fnValue should calculate unique value subset
                 */
                final JsonObject uniqueValue = new JsonObject();
                uniqueSet.forEach(field -> {
                    final Object fieldValue = Ut.field(entity, field);
                    uniqueValue.put(field, fieldValue);
                });
                return uniqueValue;
            }, pojoFile);
        }

        static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current,
                                                                 final Function<T, R> fnValue,
                                                                 final String pojoFile) {
            final ConcurrentMap<ChangeFlag, List<T>> comparedMap = new ConcurrentHashMap<ChangeFlag, List<T>>() {
                {
                    this.put(ChangeFlag.DELETE, new ArrayList<>());
                    this.put(ChangeFlag.UPDATE, new ArrayList<>());
                    this.put(ChangeFlag.ADD, new ArrayList<>());
                }
            };
            if (Objects.isNull(original) || original.isEmpty()) {
                /*
                 * No `DELETE`, No `UPDATE`
                 * In this situation, all the entity should be `ADD`
                 * Not null for double checking
                 */
                if (Objects.nonNull(current)) {
                    comparedMap.get(ChangeFlag.ADD).addAll(current);
                }
            } else {
                /*
                 * Calculation for `DELETE`
                 */
                original.forEach(originalItem -> {
                    final T latestItem = find(current, originalItem, fnValue);
                    if (Objects.isNull(latestItem)) {
                        /*
                         * Delete
                         */
                        comparedMap.get(ChangeFlag.DELETE).add(originalItem);
                    }
                });
                /*
                 * Calculation for `ADD / UPDATE`
                 */
                current.forEach(latestItem -> {
                    final T previous = find(original, latestItem, fnValue);
                    if (Objects.isNull(previous)) {
                        /*
                         * ADD
                         */
                        comparedMap.get(ChangeFlag.ADD).add(latestItem);
                    } else {
                        /*
                         * original / current contains
                         * UPDATE
                         */
                        final T updated = combine(previous, latestItem, pojoFile);
                        comparedMap.get(ChangeFlag.UPDATE).add(updated);
                    }
                });
            }
            return comparedMap;
        }

        @SuppressWarnings("all")
        private static <T> T combine(final T old, final T latest, final String pojo) {
            if (Objects.isNull(old) && Objects.isNull(latest)) {
                return null;
            } else {
                if (Objects.isNull(old)) {
                    return latest;
                } else if (Objects.isNull(latest)) {
                    return old;
                } else {
                    /*
                     * 此处做一个比较大的比对变更，主要用于如何合并属性的考虑，此处的 Tool 包含了所有属性集，为一级数据
                     * 在比对过程中，如果遇到了JSON属性，那么二层JSON属性不应该执行 Merge 操作，而是直接替换，只有
                     * 一级属性会做相关比对
                     * 1. combineJson - 旧数据
                     * 2. latestJson  - 新数据
                     * 所以此处在调用 mergeIn 方法时第二参数应该为 false
                     */
                    final JsonObject combineJson = Ut.valueJObject(Ut.toJson(old, pojo));
                    final JsonObject latestJson = Ut.valueJObject(Ut.toJson(latest, pojo));
                    if (latestJson.containsKey("key")) {
                        /*
                         * Because here it will combine previous/current json object
                         * The system should remove latest `key` field ( Primary Key Removed )
                         */
                        latestJson.remove("key");
                    }
                    combineJson.mergeIn(latestJson, false);
                    final Class<?> clazz = latest.getClass();
                    return (T) Ut.fromJson(combineJson, clazz, pojo);
                }
            }
        }

        private static <T, R> T find(final List<T> list, final T current, final Function<T, R> fnValue) {
            if (Objects.isNull(list) || list.isEmpty() || Objects.isNull(current)) {
                /*
                 * Could not be found here
                 * 1) list is null          ( Source List )
                 * 2) list is empty         ( Source List )
                 * 3) current is null       ( Target Entity )
                 */
                return null;
            } else {
                final R comparedValue = fnValue.apply(current);
                if (Objects.isNull(comparedValue)) {
                    /*
                     * Compared value is null, return null instead of deeply lookup
                     */
                    return null;
                } else {
                    return list.stream().filter(Objects::nonNull)
                        .filter(each -> comparedValue.equals(fnValue.apply(each)))
                        .findAny().orElse(null);
                }
            }
        }

        @SuppressWarnings("all")
        static <T> T updateT(final T query, final JsonObject params) {
            Objects.requireNonNull(query);
            final Class<?> entityCls = query.getClass();
            final JsonObject original = Ut.toJson(query, "");
            original.mergeIn(params, true);
            return (T) Ut.fromJson(original, entityCls, "");
        }

        @SuppressWarnings("all")
        static <T> T cloneT(final T input) {
            if (Objects.isNull(input)) {
                return null;
            }
            final Class<?> clazz = input.getClass();
            final JsonObject original = Ut.toJson(input, "");
            return (T) Ut.fromJson(original, clazz, "");
        }

        static <ID> HRecord updateR(final HRecord record, final JsonObject data,
                                    final Supplier<ID> supplier) {
            Objects.requireNonNull(record);
            record.set(data);
            final ID key = record.key();
            if (Objects.isNull(key)) {
                record.key(supplier.get());
            }
            return record;
        }

        static List<HRecord> updateR(final List<HRecord> recordList, final JsonArray array, final String field) {
            final ConcurrentMap<String, JsonObject> dataMap = Ut.elementMap(array, field);
            recordList.forEach(record -> {
                final String key = record.key();
                if (Objects.nonNull(key)) {
                    final JsonObject dataJ = dataMap.getOrDefault(key, new JsonObject());
                    if (Ut.isNotNil(dataJ)) {
                        dataJ.remove(field);
                        record.set(dataJ);
                    }
                }
            });
            return recordList;
        }

        static <T> List<T> updateT(final List<T> query, final JsonArray params, final String field) {
            Objects.requireNonNull(query);
            if (query.isEmpty()) {
                return new ArrayList<>();
            } else {
                final ConcurrentMap<String, JsonObject> dataMap = Ut.elementMap(params, field);
                final List<T> result = new ArrayList<>();
                query.forEach(item -> {
                    final Object key = Ut.field(item, field);
                    if (Objects.nonNull(key)) {
                        final JsonObject merge = dataMap.get(key.toString());
                        final T entity = updateT(item, merge);
                        result.add(entity);
                    }
                });
                return result;
            }
        }

        static JsonArray updateJ(final JsonArray query, final JsonArray params, final String field) {
            Objects.requireNonNull(query);
            if (Ut.isNil(query)) {
                return new JsonArray();
            } else {
                final ConcurrentMap<String, JsonObject> dataMap = Ut.elementMap(params, field);
                final JsonArray normalized = query.copy();
                Ut.itJArray(normalized).forEach(json -> {
                    final String value = json.getString(field);
                    if (Objects.nonNull(value)) {
                        final JsonObject merge = dataMap.get(value);
                        if (Objects.nonNull(merge)) {
                            json.mergeIn(merge, true);
                        }
                    }
                });
                return normalized;
            }
        }


        static <T> Future<JsonArray> run(final ConcurrentMap<ChangeFlag, List<T>> compared,
                                         final Function<List<T>, Future<List<T>>> insertAsyncFn,
                                         final Function<List<T>, Future<List<T>>> updateAsyncFn) {
            final List<Future<JsonArray>> futures = new ArrayList<>();
            final List<T> qAdd = compared.getOrDefault(ChangeFlag.ADD, new ArrayList<>());
            if (!qAdd.isEmpty()) {
                futures.add(insertAsyncFn.apply(qAdd).compose(Ut::futureA));
            }
            final List<T> qUpdate = compared.getOrDefault(ChangeFlag.UPDATE, new ArrayList<>());
            if (!qUpdate.isEmpty()) {
                futures.add(updateAsyncFn.apply(qUpdate).compose(Ut::futureA));
            }
            return Fx.compressA(futures);
        }
    }
}

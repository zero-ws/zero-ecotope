package io.zerows.extension.module.integration.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.integration.component.Fs;
import io.zerows.extension.module.integration.component.FsDefault;
import io.zerows.extension.module.integration.domain.tables.daos.IDirectoryDao;
import io.zerows.extension.module.integration.domain.tables.pojos.IDirectory;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class IsFs {

    static Future<JsonObject> run(final JsonObject data, final Function<Fs, Future<JsonObject>> fsRunner) {
        final String componentCls = data.getString(KName.Component.RUN_COMPONENT);
        if (Ut.isNil(componentCls)) {
            return Ux.future(data);
        }
        final Class<?> clazz = Ut.clazz(componentCls, null);
        if (Objects.nonNull(clazz) && Ut.isImplement(clazz, Fs.class)) {
            final Fs fs = Ut.singleton(clazz);
            return fsRunner.apply(fs);
        } else {
            return Ux.future(data);
        }
    }


    static Future<JsonArray> run(final JsonArray data, final BiFunction<Fs, JsonArray, Future<JsonArray>> fsRunner) {
        final ConcurrentMap<Fs, JsonArray> componentMap = fsGroup(data);
        final List<Future<JsonArray>> futures = new ArrayList<>();
        componentMap.forEach((fs, dataEach) -> futures.add(fsRunner.apply(fs, dataEach.copy())));
        return Fx.compressA(futures);
    }

    /*
     * The input map
     * -- storePath = directoryId
     *
     * Here `storePath` is file or directory storePath
     */
    static Future<ConcurrentMap<Fs, Set<String>>> group(final ConcurrentMap<String, String> fileMap) {
        // directoryId = Set<String> ( storePath )
        final ConcurrentMap<String, Set<String>> directoryMap = Ut.inverseSet(fileMap);

        // Fetch directories by Set<String> ( keys )
        final JsonObject criteria = new JsonObject();
        criteria.put(KName.KEY + ",i", Ut.toJArray(directoryMap.keySet()));
        return IsDir.query(criteria).compose(directories -> {

            // Grouped List<IDirectory> by `runComponent`, transfer to runComponent = Set<String> ( keys )
            final ConcurrentMap<String, List<String>> grouped =
                Ut.elementGroup(directories, IDirectory::getRunComponent, IDirectory::getKey);

            /*
             * Connect to Map
             * 1. runComponent = Set<String> ( keys )
             * 2. key = Set<String> ( storePath )
             */
            final ConcurrentMap<Fs, List<String>> fsGroup = group(grouped, List::isEmpty);
            final ConcurrentMap<Fs, Set<String>> resultMap = new ConcurrentHashMap<>();
            fsGroup.forEach((fs, keyList) -> {
                final Set<String> storeSet = new HashSet<>();
                keyList.forEach(key -> {
                    final Set<String> subSet = directoryMap.get(key);
                    if (Objects.nonNull(subSet) && !subSet.isEmpty()) {
                        storeSet.addAll(subSet);
                    }
                });
                if (!storeSet.isEmpty()) {
                    resultMap.put(fs, storeSet);
                }
            });
            return Ux.future(resultMap);
        });
    }

    /*
     * data structure of each json
     * {
     *      "storeRoot": "xxxx",
     *      "storePath": "Actual Path",
     *      "integrationId": "If integrated by directory"
     * }
     */
    private static ConcurrentMap<Fs, JsonArray> fsGroup(final JsonArray data) {
        /*
         * Group data
         * 1. All the integrationId = null, extract `storeRoot` from data.
         * 2. The left records contains integrationId, grouped by `integrationId`.
         */
        final JsonArray queueDft = new JsonArray();
        final JsonArray queueIntegrated = new JsonArray();
        Ut.itJArray(data).forEach(json -> {
            if (json.containsKey(KName.Component.RUN_COMPONENT)) {
                queueIntegrated.add(json);
            } else {
                queueDft.add(json);
            }
        });

        final ConcurrentMap<String, JsonArray> groupIntegrated = Ut.elementGroup(queueIntegrated, KName.Component.RUN_COMPONENT);
        final ConcurrentMap<Fs, JsonArray> groupComponent = group(groupIntegrated, JsonArray::isEmpty);

        /*
         * Default component Compact
         */
        if (!queueDft.isEmpty()) {
            final Fs fs = Ut.singleton(FsDefault.class.getName());
            final JsonArray dataRef = groupComponent.getOrDefault(fs, new JsonArray());
            dataRef.addAll(queueDft);
            groupComponent.put(fs, dataRef);
        }
        return groupComponent;
    }

    static Future<Fs> component(final String directoryId) {
        final Fs fsDft = Ut.singleton(FsDefault.class.getName());
        if (Objects.isNull(directoryId)) {
            return Ux.future(fsDft);
        }
        return DB.on(IDirectoryDao.class).<IDirectory>fetchByIdAsync(directoryId).compose(directory -> {
            if (Objects.isNull(directory)) {
                return Ux.future(fsDft);
            }
            final String componentCls = directory.getRunComponent();
            if (Ut.isNil(componentCls)) {
                return Ux.future(fsDft);
            }
            final Class<?> clazz = Ut.clazz(componentCls, null);
            if (Objects.nonNull(clazz) && Ut.isImplement(clazz, Fs.class)) {
                return Ux.future(Ut.singleton(clazz));
            } else {
                return Ux.future(fsDft);
            }
        });
    }

    static ConcurrentMap<Fs, Set<String>> combine(final ConcurrentMap<Fs, Set<String>> directoryMap,
                                                  final ConcurrentMap<Fs, Set<String>> fileMap) {
        /*
         * Combine two map
         */
        final ConcurrentMap<Fs, Set<String>> combine = new ConcurrentHashMap<>(directoryMap);
        fileMap.forEach((fs, set) -> {
            final Set<String> valueSet;
            if (combine.containsKey(fs)) {
                valueSet = combine.getOrDefault(fs, new HashSet<>());
            } else {
                valueSet = new HashSet<>();
            }
            valueSet.addAll(set);
            combine.put(fs, valueSet);
        });
        return combine;
    }

    static <V> ConcurrentMap<Fs, V> group(final ConcurrentMap<String, V> map, final Predicate<V> fnKo) {
        final ConcurrentMap<Fs, V> groupComponent = new ConcurrentHashMap<>();
        map.forEach((componentCls, value) -> {
            if (!fnKo.test(value)) {
                final Class<?> clazz = Ut.clazz(componentCls, null);
                if (Objects.nonNull(clazz) && Ut.isImplement(clazz, Fs.class)) {
                    final Fs fs = Ut.singleton(clazz);
                    groupComponent.put(fs, value);
                }
            }
        });
        return groupComponent;
    }
}

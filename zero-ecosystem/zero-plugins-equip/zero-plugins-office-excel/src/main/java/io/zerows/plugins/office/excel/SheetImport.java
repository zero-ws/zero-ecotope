package io.zerows.plugins.office.excel;

import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.support.FnBase;
import io.zerows.core.database.jooq.operation.UxJooq;
import io.zerows.epoch.common.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.uca.normalize.Oneness;
import io.zerows.epoch.enums.typed.ChangeFlag;
import io.zerows.module.metadata.atom.configuration.modeling.MDConnect;
import io.zerows.plugins.office.excel.atom.ExTable;
import io.zerows.plugins.office.excel.exception._60039Exception500ExportingError;
import io.zerows.unity.Ux;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("unchecked")
class SheetImport {
    private static final Annal LOGGER = Annal.get(ExcelClientImpl.class);
    private transient final ExcelHelper helper;

    private SheetImport(final ExcelHelper helper) {
        this.helper = helper;
    }

    static SheetImport create(final ExcelHelper helper) {
        return new SheetImport(helper);
    }

    <T> Set<T> saveEntity(final JsonArray data, final ExTable table) {
        final Set<T> resultSet = new HashSet<>();


        final MDConnect connect = table.getConnect();
        Objects.requireNonNull(connect);
        final Class<T> classPojo = (Class<T>) connect.getPojo();
        final Class<?> classDao = connect.getDao();


        if (Objects.nonNull(classPojo) && Objects.nonNull(classDao)) {
            try {
                final JsonObject filters = table.whereAncient(data);
                LOGGER.debug("[ Έξοδος ]  Table: {1}, Filters: {0}", filters.encode(), table.getName());
                final List<T> entities = Ux.fromJson(data, classPojo, connect.getPojoFile());
                final UxJooq jooq = this.jooq(table);
                assert null != jooq;
                final List<T> queried = jooq.fetch(filters);


                /*
                 * Compare by unique
                 */
                final Oneness<MDConnect> oneness = Oneness.ofConnect();
                final Set<String> keyUnique = oneness.keyUnique(connect);
                final String keyPrimary = oneness.keyPrimary(connect);


                ConcurrentMap<ChangeFlag, List<T>> compared =
                    Ux.compare(queried, entities, keyUnique, connect.getPojoFile());
                final List<T> qUpdate = compared.getOrDefault(ChangeFlag.UPDATE, new ArrayList<>());
                final List<T> qInsert = compared.getOrDefault(ChangeFlag.ADD, new ArrayList<>());
                if (!qInsert.isEmpty()) {
                    /*
                     * Compare by keys
                     */
                    if (Objects.nonNull(keyPrimary)) {
                        final Set<String> keys = new HashSet<>();
                        qInsert.forEach(item -> {
                            final Object value = Ut.field(item, keyPrimary);
                            if (Objects.nonNull(value)) {
                                keys.add(value.toString());
                            }
                        });
                        final List<T> qKeys = jooq.fetchIn(keyPrimary, keys);
                        if (!qKeys.isEmpty()) {
                            compared = Ux.compare(qKeys, qInsert, keyUnique, connect.getPojoFile());
                            qUpdate.addAll(compared.getOrDefault(ChangeFlag.UPDATE, new ArrayList<>()));
                            // qInsert reset
                            qInsert.clear();
                            qInsert.addAll(compared.getOrDefault(ChangeFlag.ADD, new ArrayList<>()));
                        }
                    }
                }
                /*
                 * Batch operation
                 */
                final List<T> batchInsert = jooq.insert(this.helper.compress(qInsert, table));
                resultSet.addAll(batchInsert);
                final List<T> batchUpdate = jooq.update(qUpdate);
                resultSet.addAll(batchUpdate);
                final int total = batchUpdate.size() + batchInsert.size();
                LOGGER.info("[ Έξοδος ] `{0}` -- ( {1} ), Inserted: {2}, Updated: {3}",
                    table.getName(), String.valueOf(total), String.valueOf(batchInsert.size()), String.valueOf(batchUpdate.size()));
            } catch (final Throwable ex) {
                ex.printStackTrace();
                LOGGER.fatal(ex);
            }
        }
        return resultSet;
    }

    <T> T saveEntity(final JsonObject data, final ExTable table) {
        T reference = null;


        final MDConnect connect = table.getConnect();
        Objects.requireNonNull(connect);
        final Class<T> classPojo = (Class<T>) connect.getPojo();
        final Class<?> classDao = connect.getDao();


        if (Objects.nonNull(classPojo) && Objects.nonNull(classDao)) {
            /*
             * First, find the record by unique filters that defined in income here.
             */
            final JsonObject filters = table.whereUnique(data);
            LOGGER.debug("[ Έξοδος ]  Table: {1}, Filters: {0}", filters.encode(), table.getName());
            final T entity = Ux.fromJson(data, classPojo, connect.getPojoFile());
            final UxJooq jooq = this.jooq(table);
            assert null != jooq;
            /*
             * Unique filter to fetch single record database here.
             * Such as code + sigma
             */
            final T queried = jooq.fetchOne(filters);
            if (null == queried) {
                /*
                 * Here are two situations that we could be careful
                 * 1. Unique Condition in source does not change, do insert here.
                 * 2. Key Condition existing in database, do update here.
                 */
                final String key = table.whereKey(data);
                if (Ut.isNil(key)) {
                    /*
                     * No definition of key here, insert directly.
                     */
                    reference = jooq.insert(entity);
                } else {
                    /*
                     * Double check to avoid issue:
                     * java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'xxx' for key 'PRIMARY'
                     */
                    final T fetched = jooq.fetchById(key);
                    if (null == fetched) {
                        /*
                         * In this situation, it common workflow to do data loading.
                         */
                        reference = jooq.insert(entity);
                    } else {
                        /*
                         * In this situation, it means the old unique filters have been changed.
                         * Such as:
                         * From
                         * id,      code,      sigma
                         * 001,     AB.CODE,   5sLyA90qSo7
                         *
                         * To
                         * id,      code,      sigma
                         * 001,     AB.CODE1,  5sLyA90qSo7
                         *
                         * Above example could show that primary key has not been modified
                         */
                        reference = jooq.update(entity);
                    }
                }
            } else {
                /*
                 * code, sigma did not change and we could identify this record
                 * do update directly to modify old information.
                 */
                reference = jooq.update(entity);
            }
        }
        return reference;
    }

    <T> Future<Set<T>> importAsync(final Set<ExTable> tables) {
        /*
         * Loading data into system
         */
        final List<Future<Set<T>>> futures = new ArrayList<>();
        tables.forEach(table ->
            futures.add(this.helper.extract(table)
                .compose(data -> Ux.future(this.saveEntity(data, table)))
            ));
        /* Set<Tool> handler */
        return FnBase.combineT(futures).compose(result -> {
            final Set<T> entitySet = new HashSet<>();
            result.forEach(entitySet::addAll);
            return Ux.future(entitySet);
        });
    }

    <T> Future<Set<T>> importAsync(final AsyncResult<Set<ExTable>> async) {
        if (async.succeeded()) {
            final Set<ExTable> tables = async.result();
            return this.importAsync(tables);
        }


        final Throwable error = async.cause();
        if (Objects.nonNull(error)) {
            return FnVertx.failOut(_60039Exception500ExportingError.class, error.getMessage());
        } else {
            return FnVertx.failOut(_500ServerInternalException.class, "[ R2MO ] 未知错误，导入失败！");
        }
    }

    private UxJooq jooq(final ExTable table) {
        final MDConnect connect = table.getConnect();
        final UxJooq jooq = Ux.Jooq.on(connect.getDao());
        if (null != jooq) {
            final String pojoFile = connect.getPojoFile();
            if (Ut.isNotNil(pojoFile)) {
                jooq.on(pojoFile);
            }
        }
        return jooq;
    }
}

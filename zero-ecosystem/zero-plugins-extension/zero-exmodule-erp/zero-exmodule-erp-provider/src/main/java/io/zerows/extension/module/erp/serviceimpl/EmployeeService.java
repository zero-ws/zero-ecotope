package io.zerows.extension.module.erp.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.erp.domain.tables.daos.EEmployeeDao;
import io.zerows.extension.module.erp.domain.tables.pojos.EEmployee;
import io.zerows.extension.module.erp.servicespec.EmployeeStub;
import io.zerows.extension.skeleton.common.KeBiz;
import io.zerows.extension.skeleton.spi.ExTrash;
import io.zerows.extension.skeleton.spi.ExUser;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.spi.modeler.Indent;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Set;
import java.util.function.BiFunction;

public class EmployeeService implements EmployeeStub {
    @Override
    public Future<JsonObject> createAsync(final JsonObject data) {
        final EEmployee employee = Ut.deserialize(data, EEmployee.class);
        if (Ut.isNil(employee.getWorkNumber())) {
            return this.insertAsyncPre(employee, data);
        } else {
            return this.insertAsync(employee, data);
        }
    }

    private Future<JsonObject> insertAsyncPre(final EEmployee employee, final JsonObject data) {
        final HPI<Indent> service = HPI.of(Indent.class);
        return service.waitOr(
            indent -> indent.indent("NUM.EMPLOYEE", data.getString(KName.SIGMA)).compose(workNum -> {
                employee.setWorkNumber(workNum);
                return this.insertAsync(employee, data);
            }),
            () -> this.insertAsync(employee, data)
        );
    }

    private Future<JsonObject> insertAsync(final EEmployee employee, final JsonObject data) {
        return DB.on(EEmployeeDao.class).insertAsync(employee)
            .compose(Ux::futureJ)
            .compose(inserted -> {
                /*
                 * If data contains `userId`, it means that current employee will relate to
                 * an account
                 */
                if (data.containsKey(KName.USER_ID)) {
                    /*
                     * Create new relation here.
                     */
                    final String key = data.getString(KName.USER_ID);
                    return this.updateReference(key, inserted);
                } else {
                    return Ux.future(data);
                }
            });
    }

    @Override
    public Future<JsonObject> fetchAsync(final String key) {
        return DB.on(EEmployeeDao.class).fetchByIdAsync(key)
            .compose(Ux::futureJ)
            .compose(this::fetchRef);
    }

    @Override
    public Future<JsonArray> fetchAsync(final Set<String> keys) {
        return DB.on(EEmployeeDao.class).fetchInAsync(KName.KEY, Ut.toJArray(keys))
            .compose(Ux::futureA)
            .compose(this::fetchRef);
    }

    @Override
    public Future<JsonArray> fetchAsync(final JsonObject condition) {
        return DB.on(EEmployeeDao.class).fetchAsync(condition)
            .compose(Ux::futureA)
            .compose(this::fetchRef);
    }

    @Override
    public Future<JsonObject> updateAsync(final String key, final JsonObject data) {
        return this.fetchAsync(key)
            .compose(Fx.ofJObject(original -> {
                final String userId = original.getString(KName.USER_ID);
                final String current = data.getString(KName.USER_ID);
                if (Ut.isNil(userId) && Ut.isNil(current)) {
                    /*
                     * Old null, new null
                     * Relation keep
                     */
                    return this.updateEmployee(key, data);
                } else if (Ut.isNil(userId) && Ut.isNotNil(current)) {
                    /*
                     * Old null, new <findRunning>
                     * Create relation with new
                     */
                    return this.updateEmployee(key, data)
                        .compose(response -> this.updateReference(current, response));
                } else if (Ut.isNotNil(userId) && Ut.isNil(current)) {
                    /*
                     * Old <findRunning>, new <null>
                     * Clear relation with old
                     */
                    return this.updateEmployee(key, data)
                        .compose(response -> this.updateReference(userId, new JsonObject())
                            .compose(nil -> Ux.future(response))
                        );
                } else {
                    /*
                     * Old <findRunning>, new <findRunning>
                     */
                    if (userId.equals(current)) {
                        /*
                         * Old = New
                         * Relation keep
                         */
                        return this.updateEmployee(key, data);
                    } else {
                        return this.updateEmployee(key, data)
                            /*
                             * Clear first
                             */
                            .compose(response -> this.updateReference(userId, new JsonObject())
                                /*
                                 * Then update
                                 */
                                .compose(nil -> this.updateReference(current, response)));
                    }
                }
            }));
    }

    private Future<JsonObject> updateEmployee(final String key, final JsonObject data) {
        final JsonObject uniques = new JsonObject();
        uniques.put(KName.KEY, key);
        final EEmployee employee = Ut.deserialize(data, EEmployee.class);
        return DB.on(EEmployeeDao.class)
            .upsertAsync(uniques, employee)
            .compose(Ux::futureJ);
    }

    @Override
    public Future<Boolean> deleteAsync(final String key) {
        return this.fetchAsync(key).compose(Fx.ifNil(() -> Boolean.TRUE, item ->
            // SPI: ExTrash
            HPI.of(ExTrash.class).waitOr(
                trash -> trash.backupAsync("res.employee", item)
                    .compose(backup -> this.deleteAsync(key, item)),
                () -> this.deleteAsync(key, item)
            )
        ));
    }

    private Future<Boolean> deleteAsync(final String key, final JsonObject item) {
        final String userId = item.getString(KName.USER_ID);
        return this.updateReference(userId, new JsonObject())
            .compose(nil -> DB.on(EEmployeeDao.class)
                .deleteByIdAsync(key));
    }

    private Future<JsonObject> updateReference(final String key, final JsonObject data) {
        return this.switchJ(data, (user, filters) -> user.rapport(key, filters)
            .compose(Fx.ofJObject(response ->
                Ux.future(data.put(KName.USER_ID, response.getString(KName.KEY))))));
    }

    private Future<JsonObject> fetchRef(final JsonObject input) {
        return this.switchJ(input, ExUser::rapport).compose(userJ -> {
            if (Ut.isNotNil(userJ)) {
                final String userId = Ut.valueString(userJ, KName.KEY);
                if (Ut.isNotNil(userId)) {
                    input.put(KName.USER_ID, userId);
                }
            }
            return Ux.future(input);
        });
    }

    private Future<JsonArray> fetchRef(final JsonArray input) {
        return Ux.channel(ExUser.class, JsonArray::new, user -> {
            final Set<String> keys = Ut.valueSetString(input, KName.KEY);
            return user.rapport(keys);
        }).compose(employee -> {
            final JsonArray merged = Ut.elementJoin(input, employee, KName.KEY, KName.MODEL_KEY);
            return Ux.future(merged);
        });
    }

    private Future<JsonObject> switchJ(final JsonObject input,
                                       final BiFunction<ExUser, JsonObject, Future<JsonObject>> executor) {
        // SPI: ExUser
        return HPI.of(ExUser.class).waitAsync(
            user -> {
                if (Ut.isNil(input)) {
                    // fix issue: https://gitee.com/silentbalanceyh/vertx-zero-scaffold/issues/I6W2L9
                    final JsonObject filters = new JsonObject();
                    filters.put(KName.IDENTIFIER, KeBiz.TypeUser.employee.name());
                    return executor.apply(user, filters);
                    //return Ux.future(new JsonObject());
                } else {
                    final JsonObject filters = new JsonObject();
                    filters.put(KName.IDENTIFIER, KeBiz.TypeUser.employee.name());
                    filters.put(KName.SIGMA, input.getString(KName.SIGMA));
                    filters.put(KName.KEY, input.getString(KName.KEY));
                    return executor.apply(user, filters);
                }
            },
            JsonObject::new
        );
    }
}

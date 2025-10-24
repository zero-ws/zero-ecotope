package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.r2mo.base.dbe.Join;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UObject;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.rbac.atom.ScConfig;
import io.zerows.extension.commerce.rbac.bootstrap.ScPin;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.skeleton.spi.ExOwner;
import io.zerows.extension.skeleton.spi.ScTwine;
import io.zerows.mbse.metadata.KQr;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/*
 * 关联关系：用户扩展组件
 * 1）当用户和额外的表执行链接时，会启用用户扩展组件，根据类型执行相连
 * 2）目前的分组和规划
 *    用户 + 员工 = 员工账号
 *    用户 + 客户 = 三方账号
 *    用户 + 会员 = 会员账号
 * 3）其配置扩展区间的核心配置如下（以员工为例）
 * src/plugin/rbac/configuration.json 文件
 * {
 *     "....",
 *     "category": {
 *          "employee": {
 *               "classDao": "io.zerows.extension.commerce.erp.domain.tables.daos.EEmployeeDao",
 *               "condition": {
 *                   "workNumber,!n": ""
 *               },
 *               "mapping": {
 *                   "modelKey": "employeeId"
 *               }
 *          }
 *     },
 *     "initializePassword": "xxxx",
 *     "initialize": {
 *         "scope": "vie.app.xx",
 *         "grantType": "authorization_code"
 *     }
 * }
 *   3.1）此处 employee 代表类型，即 S_USER 中 MODEL_ID 存储的值
 *   3.2）modelKey -> employeeId 为前端提供语义级消费
 *   3.3）initialize 为导入时的模板数据
 */
class TwineExtension implements ScTwine<SUser> {

    private static final ScConfig CONFIG = ScPin.getConfig();

    @Override
    public Future<JsonObject> searchAsync(final String identifier, final JsonObject query) {
        // KQr 为空，不执行关联查询
        final KQr qr = CONFIG.category(identifier);
        if (Objects.isNull(qr) || !qr.valid()) {
            return DB.on(SUserDao.class).fetchJOneAsync(query);
        }
        return TwineQr.normalize(qr, query)
            .compose(queryJ ->
                DB.on(Join.of(
                    SUserDao.class, KName.MODEL_KEY,
                    qr.getClassDao()
                )).searchAsync(queryJ)
            )
            // Connect to `groups`
            .compose(this::connect);
        //            .compose(queryJ -> {
        //
        //
        //                final ADJ searcher = DB.join();
        //                /*
        //                 * S_USER ( modelKey )
        //                 *    JOIN
        //                 * XXX ( key )
        //                 * 额外步骤
        //                 * */
        //                searcher.add(SUserDao.class, KName.MODEL_KEY);
        //                final Class<?> clazz = qr.getClassDao();
        //                searcher.join(clazz);
        //                return searcher.searchAsync(queryJ)
        //                    // Connect to `groups`
        //                    .compose(this::connect);
        //            });
    }

    /**
     * 扩展用户信息提取，主要扩展两层
     * <pre><code>
     *     1. 根据配置文件中的 category 执行关联查询
     *        S_USER -> modelId -> category -> classDao
     *        提取子表信息
     *     2. 根据 sigma 提取租户信息，调用 {@link ExOwner} 通道信息
     * </code></pre>
     *
     * @param user 读取的用户对象
     *
     * @return 返回追加到响应数据中的扩展信息
     */
    @Override
    public Future<JsonObject> identAsync(final SUser user) {
        final KRef ref = new KRef();
        return this.runSingle(user, qr -> {
                final ADB jq = DB.on(qr.getClassDao());
                Objects.requireNonNull(jq);
                return jq.fetchJByIdAsync(user.getModelKey());
            })
            .compose(ref::future)
            /*
             * 追加两个属性到根数据结构中
             * 1. tenantId -> 租户ID
             * 2. tenant -> 租户基本信息
             */
            .compose(nil -> Ux.channel(ExOwner.class, JsonObject::new, stub -> stub.fetchTenant(user.getSigma())))
            .compose(tenant -> {
                final JsonObject response = ref.get();
                if (Ut.isNotNil(tenant)) {
                    final String tenantId = Ut.valueString(tenant, KName.KEY);
                    response.put(KName.TENANT_ID, tenantId);
                    response.put(KName.Tenant.TENANT, tenant);
                }
                return Ux.future(response);
            });
    }

    @Override
    public Future<JsonObject> identAsync(final SUser key, final JsonObject updatedData) {
        /* User model key */
        return this.runSingle(key, qr -> {
            /* Read Extension information */
            final ADB jq = DB.on(qr.getClassDao());
            Objects.requireNonNull(jq);
            return jq.updateJAsync(key.getModelKey(), updatedData);
        });
    }

    @Override
    public Future<JsonArray> identAsync(final Collection<SUser> users) {
        return this.runBatch(this.compress(users)).compose(map -> {
            final JsonArray combineA = new JsonArray();
            final JsonArray extensionA = new JsonArray();
            map.values().forEach(extensionA::addAll);
            final ConcurrentMap<String, JsonObject> mapped = Ut.elementMap(extensionA, KName.KEY);
            // User Iterator / Extension ( key -> JsonObject )
            users.stream().filter(Objects::nonNull).forEach(user -> {
                final JsonObject userJ = Ux.toJson(user);
                final String modelKey = user.getModelKey();
                if (mapped.containsKey(modelKey)) {
                    final JsonObject extensionJ = mapped.getOrDefault(modelKey, new JsonObject());
                    final KQr qr = CONFIG.category(user.getModelId());
                    combineA.add(this.combine(userJ, extensionJ, qr));
                } else {
                    combineA.add(userJ);
                }
            });
            return Ux.future(combineA);
        });
    }

    // ------------------ Private Method Processing --------------------
    private Future<ConcurrentMap<String, JsonArray>> runBatch(final List<SUser> users) {
        final ConcurrentMap<String, List<SUser>> grouped = Ut.elementGroup(users, SUser::getModelId, item -> item);
        final ConcurrentMap<String, Future<JsonArray>> futureMap = new ConcurrentHashMap<>();
        grouped.forEach((modelId, groupList) -> {
            final KQr qr = CONFIG.category(modelId);
            futureMap.put(modelId, this.runBatch(groupList, qr));
        });
        return Fx.combineM(futureMap);
    }

    private Future<JsonArray> runBatch(final List<SUser> users, final KQr qr) {
        final Set<String> keys = users.stream()
            .map(SUser::getModelKey)
            .collect(Collectors.toSet());
        if (keys.isEmpty()) {
            return Ux.futureA();
        } else {
            final ADB jq = DB.on(qr.getClassDao());
            Objects.requireNonNull(jq);
            final JsonObject condition = new JsonObject();
            condition.put(KName.KEY + ",i", Ut.toJArray(keys));
            return jq.fetchJAsync(condition);
        }
    }

    /*
     * Fetch Extension Table Record, joined by
     * S_USER
     * - MODEL_ID
     * - MODEL_KEY
     *
     * Here are the specification of extension model that will be combine to S_USER
     * The default usage of `modelId` is as:
     * - res.employee: The account of E_EMPLOYEE of OA system.
     * - ht.member:    The member of the website here for usage in future.
     *
     * The joined part should be came from HAtom in the layer system of EMF framework instead.
     */
    private Future<JsonObject> runSingle(final SUser user, final Function<KQr, Future<JsonObject>> executor) {
        if (Objects.isNull(user)) {
            /* Input SUser object is null, could not findRunning S_USER record in your database */
            LOG.Web.info(this.getClass(), AuthMsg.EXTENSION_EMPTY + " Null SUser");
            return Ux.futureJ();
        }

        if (Objects.isNull(user.getModelKey()) || Objects.isNull(user.getModelId())) {
            /*
             * There are two fields in S_USER table: MODEL_ID & MODEL_KEY
             * This branch means that MODEL_KEY is null, you could not do any Extension part.
             * Returned SUser json data formatFail only.
             */
            LOG.Web.info(this.getClass(), AuthMsg.EXTENSION_EMPTY + " Null modelKey");
            return Ux.futureJ(user);
        }

        /*
         * Extract the extension part for default running here.
         * 1) The KQr must exist in ScConfig and you can search the KQr by `modelId`
         * 2) The KQr must be valid:  classDao mustn't be null here
         */
        final KQr qr = CONFIG.category(user.getModelId());
        if (Objects.isNull(qr) || !qr.valid()) {
            LOG.Web.info(this.getClass(), AuthMsg.EXTENSION_EMPTY + " Extension {0} Null", user.getModelId());
            return Ux.futureJ(user);
        }


        /*
         * Simple situation for extension information processing
         * 1. User Extension `executor` bind to UxJooq running
         * 2. Zero extension provide the configuration part and do executor
         * 3. Returned data formatFail is InJson of Extension
         */
        LOG.Web.info(this.getClass(), AuthMsg.EXTENSION_BY_USER, user.getModelKey());
        return executor.apply(qr).compose(extensionJ -> {
            final JsonObject userJ = Ux.toJson(user);
            if (Ut.isNil(extensionJ)) {
                return Ux.future(userJ);
            } else {
                return Ux.future(this.combine(userJ, extensionJ, qr));
            }
        });
    }

    private Future<JsonObject> connect(final JsonObject pagination) {
        final JsonArray users = Ut.valueJArray(pagination, KName.LIST);
        final Set<String> userKeys = Ut.valueSetString(users, KName.KEY);
        return Junc.refRights().identAsync(userKeys).compose(relations -> {
            // 分组
            final ConcurrentMap<String, JsonArray> grouped =
                Ut.elementGroup(relations, AuthKey.F_USER_ID);
            final JsonArray replaced = Ut.elementZip(users, KName.KEY, KName.GROUPS, grouped, AuthKey.F_GROUP_ID);
            pagination.put(KName.LIST, replaced);
            return Ux.future(pagination);
        });
    }

    private List<SUser> compress(final Collection<SUser> users) {
        /*
         * Filtered:
         * 1. SUser is not null
         * 2. modelId / modelKey is Ok
         * 3. KQr is valid configured in ScConfig
         */
        return users.stream()
            .filter(Objects::nonNull)
            .filter(user -> Objects.nonNull(user.getModelId()))
            .filter(user -> Objects.nonNull(user.getModelKey()))
            .filter(user -> {
                final KQr qr = CONFIG.category(user.getModelId());
                return Objects.nonNull(qr) && qr.valid();
            })
            .collect(Collectors.toList());
    }

    private JsonObject combine(final JsonObject userJ, final JsonObject extensionJ, final KQr qr) {
        final UObject combine = UObject.create(userJ.copy()).append(extensionJ);
        /*
         * mapping
         * modelKey -> targetField
         */
        final JsonObject mapping = qr.getMapping();
        Ut.<String>itJObject(mapping, (to, from) -> combine.convert(from, to));
        return combine.to();
    }
}

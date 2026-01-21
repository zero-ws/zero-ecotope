package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.Apt;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
class IdcService extends AbstractIdc {

    IdcService(final String sigma) {
        super(sigma);
    }

    @Override
    public Future<JsonArray> saveAsync(final JsonArray user, final String by) {
        /*
         * 绑定一个用户输入，输入中可能包含
         * - roles      = R1, R2, R3
         * - groups     = G1, G2, G3
         */
        final KRef inputArray = new KRef();
        return this.runPre(user)
            // 按 username 压缩
            .compose(this::compress)
            // 压缩后和引用同步
            .compose(inputArray::future)
            // 读取原来的数据
            .compose(this::capture)
            .compose(original -> {
                /*
                 * Unique `username` ensure in database
                 */
                final Apt apt = Apt.create(original, inputArray.get());
                final Apt created = Ke.compmared(apt, KName.USERNAME, by);
                /*
                 * Split doing
                 */
                return Ke.atomyFn(this.getClass(), created).apply(
                    // Insert
                    inserted -> this.createAsync(inserted).compose(users -> this.connect(users, inputArray.get())),
                    // Update
                    updated -> this.updateAsync(updated).compose(users -> this.connect(users, inputArray.get()))
                );
            });
    }

    @SuppressWarnings("all")
    private Future<JsonArray> connect(final List<SUser> users, final JsonArray inputData) {
        return Fx.passion(inputData,
            // roles = R1, R2, R3
            inputA -> IdcBinder.role(this.sigma).bindAsync(users, inputA),
            // groups = G1, G2, G3
            inputA -> IdcBinder.group(this.sigma).bindAsync(users, inputA)
        );
    }

    private Future<JsonArray> capture(final JsonArray compress) {
        final JsonObject condition = Ux.whereAnd();
        condition.put(KName.USERNAME + ",i", Ut.toJArray(Ut.valueSetString(compress, KName.USERNAME)));
        condition.put(KName.SIGMA, this.sigma);
        log.info("{} 查询条件：{}", ScConstant.K_PREFIX, condition.encode());
        return DB.on(SUserDao.class).fetchJAsync(condition);
    }

    private Future<JsonArray> compress(final JsonArray inputData) {
        final JsonArray compressed = new JsonArray();
        final Set<String> nameSet = new HashSet<>();
        Ut.itJArray(inputData).forEach(each -> {
            if (!nameSet.contains(each.getString(KName.USERNAME))) {
                compressed.add(each);
                nameSet.add(KName.USERNAME);
            } else {
                log.info("{} 用户 ( username = {} ) 重复，将自动忽略：{}",
                    ScConstant.K_PREFIX, each.getString(KName.USERNAME), each.encode());
            }
        });
        return Ux.future(compressed);
    }

    private Future<List<SUser>> createAsync(final JsonArray userJson) {
        final KRef refer = new KRef();
        return this.model(userJson)
            .compose(processed -> Sc.valueAuth(processed, this.sigma))
            .compose(DB.on(SUserDao.class)::insertAsync);
        // .compose(refer::future)
        // .compose(Sc::valueAuth)
        // .compose(DB.on(OUserDao.class)::insertAsync)
        // .compose(ou -> Ux.future(refer.get()));
    }

    private Future<List<SUser>> updateAsync(final JsonArray userJson) {
        final List<SUser> users = Ux.fromJson(userJson, SUser.class);
        users.forEach(user -> user.setActive(Boolean.TRUE));
        return DB.on(SUserDao.class).updateAsync(users);
    }
}

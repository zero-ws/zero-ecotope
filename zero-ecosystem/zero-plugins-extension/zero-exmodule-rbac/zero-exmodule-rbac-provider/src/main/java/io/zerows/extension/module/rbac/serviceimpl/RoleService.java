package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.rbac.boot.MDRBACManager;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SView;
import io.zerows.extension.module.rbac.servicespec.RoleStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoleService implements RoleStub {

    @Override
    public Future<JsonObject> roleSave(final JsonObject data, final User user) {
        // 1. 加载配置信息
        final MDConfiguration config = MDRBACManager.of().configuration();
        final JsonObject configData = config.inConfiguration();
        final JsonObject initializeRole = configData.getJsonObject(ScAuthKey.INITIALIZE_ROLE);
        final JsonObject initializePermissions = configData.getJsonObject(ScAuthKey.INITIALIZE_PERMISSIONS);

        // 2. 提取权限码
        final JsonArray permCodes = initializeRole.getJsonArray(ScAuthKey.PERMISSIONS);
        final JsonObject queryCondition = new JsonObject().put(ScAuthKey.AUTH_CODE, permCodes);

        // 3. 转换数据模型
        final SRole sRole = Ut.fromJson(data, SRole.class);

        return DB.on(SRoleDao.class).insertAsync(sRole)
            .compose(role -> this.savePermissions(role.getKey(), queryCondition))
            .compose(nil -> this.saveDefaultView(sRole, user, initializePermissions))
            .map(role -> (Ux.toJson(sRole)));
    }

    /**
     * 保存角色与权限的关联关系
     */
    private Future<JsonObject> savePermissions(final String roleId, final JsonObject queryCondition) {
        return DB.on(SPermissionDao.class).<SPermission>fetchAsync(queryCondition)
            .compose(permissions -> {
                final JsonArray relations = new JsonArray();
                for (final SPermission permission : permissions) {
                    relations.add(new JsonObject()
                        .put(ScAuthKey.F_ROLE_ID, roleId)
                        .put(ScAuthKey.F_PERM_ID, permission.getKey()));
                }
                return DB.on(RRolePermDao.class).insertAsync(relations);
            }).compose(niv -> Ux.future());
    }

    /**
     * 构造并保存默认视图
     */
    private Future<Void> saveDefaultView(final SRole role, final User user, final JsonObject initPermissions) {
        final SView view = new SView();
        view.setKey(UUID.randomUUID().toString());
        view.setName(ScAuthKey.DEFAULT);
        view.setOwner(role.getKey());
        view.setOwnerType(ScAuthKey.OWNER_TYPE_ROLE);
        view.setResourceId(ScAuthKey.DEFAULT_RESOURCE_ID);
        view.setProjection("{}");
        view.setCriteria("{}");
        view.setRows(initPermissions.toString());
        view.setPosition(ScAuthKey.DEFAULT);
        view.setVisitant(false);
        view.setSigma(role.getSigma());
        view.setLanguage(role.getLanguage());
        view.setActive(true);
        view.setCreatedAt(LocalDateTime.now());
        view.setCreatedBy(Account.userId(user));

        return DB.on(SViewDao.class).insertAsync(view).mapEmpty();
    }
}
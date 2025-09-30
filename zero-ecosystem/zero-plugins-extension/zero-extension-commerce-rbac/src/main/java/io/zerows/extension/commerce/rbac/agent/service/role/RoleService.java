package io.zerows.extension.commerce.rbac.agent.service.role;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.extension.HExtension;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SViewDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SView;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.ScConstant;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.unity.Ux;

import java.time.LocalDateTime;
import java.util.UUID;

public class RoleService implements RoleStub {

    @Override
    public Future<JsonObject> roleSave(final JsonObject data, final User user) {
        // 1. 加载配置信息
        final MDConfiguration config = HExtension.getOrCreate(ScConstant.BUNDLE_SYMBOLIC_NAME);
        final JsonObject configData = config.inConfiguration();
        final JsonObject initializeRole = configData.getJsonObject(AuthKey.INITIALIZE_ROLE);
        final JsonObject initializePermissions = configData.getJsonObject(AuthKey.INITIALIZE_PERMISSIONS);

        // 2. 提取权限码
        final JsonArray permCodes = initializeRole.getJsonArray(AuthKey.PERMISSIONS);
        final JsonObject queryCondition = new JsonObject().put(AuthKey.AUTH_CODE, permCodes);

        // 3. 转换数据模型
        final SRole sRole = Ut.fromJson(data, SRole.class);

        return Ux.Jooq.on(SRoleDao.class).insertAsync(sRole)
            .compose(role -> this.savePermissions(role.getKey(), queryCondition))
            .compose(nil -> this.saveDefaultView(sRole, user, initializePermissions))
            .map(role -> (Ux.toJson(sRole)));
    }

    /**
     * 保存角色与权限的关联关系
     */
    private Future<JsonObject> savePermissions(final String roleId, final JsonObject queryCondition) {
        return Ux.Jooq.on(SPermissionDao.class).<SPermission>fetchAsync(queryCondition)
            .compose(permissions -> {
                final JsonArray relations = new JsonArray();
                for (final SPermission permission : permissions) {
                    relations.add(new JsonObject()
                        .put(AuthKey.F_ROLE_ID, roleId)
                        .put(AuthKey.F_PERM_ID, permission.getKey()));
                }
                return Ux.Jooq.on(RRolePermDao.class).insertAsync(relations);
            }).compose(niv -> Ux.future());
    }

    /**
     * 构造并保存默认视图
     */
    private Future<Void> saveDefaultView(final SRole role, final User user, final JsonObject initPermissions) {
        final SView view = new SView();
        view.setKey(UUID.randomUUID().toString());
        view.setName(AuthKey.DEFAULT);
        view.setOwner(role.getKey());
        view.setOwnerType(AuthKey.OWNER_TYPE_ROLE);
        view.setResourceId(AuthKey.DEFAULT_RESOURCE_ID);
        view.setProjection("{}");
        view.setCriteria("{}");
        view.setRows(initPermissions.toString());
        view.setPosition(AuthKey.DEFAULT);
        view.setVisitant(false);
        view.setSigma(role.getSigma());
        view.setLanguage(role.getLanguage());
        view.setActive(true);
        view.setCreatedAt(LocalDateTime.now());
        view.setCreatedBy(Ux.keyUser(user));

        return Ux.Jooq.on(SViewDao.class).insertAsync(view).mapEmpty();
    }
}
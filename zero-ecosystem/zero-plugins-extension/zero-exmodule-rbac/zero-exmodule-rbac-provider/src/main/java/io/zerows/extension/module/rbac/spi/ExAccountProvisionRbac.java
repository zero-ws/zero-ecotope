package io.zerows.extension.module.rbac.spi;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ExAccountProvision;
import io.zerows.extension.skeleton.spi.ExTenantProvision;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * EMAIL / SMS 注册补齐：
 * 1) 创建 S_USER
 * 2) 关联默认角色 R_USER_ROLE
 * 3) 通过 ExTenantProvision 创建 / 获取租户
 */
public class ExAccountProvisionRbac implements ExAccountProvision {

    private static final String DEFAULT_ROLE_CODE = "ROLE.USER";

    @Override
    public Future<JsonObject> provision(final JsonObject input) {
        final String identifier = input.getString("identifier");
        final String type = input.getString("type", "UNKNOWN").toUpperCase(Locale.ROOT);
        final String field = this.loginField(type);
        if (StrUtil.isBlank(identifier) || StrUtil.isBlank(field)) {
            return Future.succeededFuture(new JsonObject());
        }
        return DB.on(SUserDao.class).<SUser>fetchOneAsync(field, identifier)
            .compose(found -> {
                if (Objects.nonNull(found)) {
                    found.setPassword(null);
                    return Ux.futureJ(found);
                }
                return this.provisionTenant(input)
                    .compose(tenant -> this.insertUser(input, tenant, field, identifier))
                    .compose(user -> this.ensureRole(user, input, type))
                    .compose(user -> Ux.futureJ(user.setPassword(null)));
            });
    }

    private Future<JsonObject> provisionTenant(final JsonObject input) {
        return HPI.of(ExTenantProvision.class).waitOr(
            provision -> provision.provision(input),
            () -> Future.succeededFuture(new JsonObject())
        );
    }

    private Future<SUser> insertUser(final JsonObject input, final JsonObject tenant,
                                     final String field, final String identifier) {
        final String sigma = this.readSigma(tenant, identifier);
        final String tenantId = this.readTenantId(tenant);
        final String appId = tenant.getString(KName.APP_ID);
        final String username = this.readUsername(input, field, identifier);

        final SUser user = new SUser();
        user.setId(UUID.randomUUID().toString());
        user.setCode(this.userCode(identifier));
        user.setUsername(username);
        user.setPassword(this.readPassword(input));
        user.setType("USER");
        user.setCategory("REGISTER");
        user.setActive(Boolean.TRUE);
        user.setLanguage("zh-CN");
        user.setSigma(sigma);
        user.setTenantId(tenantId);
        user.setAppId(appId);
        user.setCreatedAt(LocalDateTime.now());
        user.setRealname(input.getString("realname"));
        user.setAlias(input.getString("alias"));
        user.setDescription(input.getString("description"));
        user.setMetadata(new JsonObject()
            .put("registerType", input.getString("type"))
            .put("registerIdentifier", identifier));
        if ("email".equals(field)) {
            user.setEmail(identifier);
        } else if ("mobile".equals(field)) {
            user.setMobile(identifier);
        }
        return DB.on(SUserDao.class).insertAsync(user);
    }

    private Future<SUser> ensureRole(final SUser user, final JsonObject input, final String type) {
        return this.fetchOrCreateRole(user, input, type)
            .compose(role -> this.bindRole(user.getId(), role.getId()).map(nil -> user));
    }

    private Future<SRole> fetchOrCreateRole(final SUser user, final JsonObject input, final String type) {
        final JsonObject condition = new JsonObject().put(KName.CODE, DEFAULT_ROLE_CODE);
        if (Ut.isNotNil(user.getSigma())) {
            condition.put(KName.SIGMA, user.getSigma());
        }
        return DB.on(SRoleDao.class).fetchJOneAsync(condition)
            .compose(found -> {
                if (Ut.isNotNil(found)) {
                    return Ux.future(Ut.deserialize(found, SRole.class));
                }
                final SRole role = new SRole();
                role.setId(UUID.randomUUID().toString());
                role.setCode(DEFAULT_ROLE_CODE);
                role.setName("默认角色");
                role.setComment("Auto provisioned for " + type);
                role.setPower(Boolean.FALSE);
                role.setCategory("DEFAULT");
                role.setSigma(user.getSigma());
                role.setTenantId(user.getTenantId());
                role.setAppId(user.getAppId());
                role.setActive(Boolean.TRUE);
                role.setLanguage("zh-CN");
                role.setCreatedAt(LocalDateTime.now());
                role.setMetadata(new JsonObject()
                    .put("autoProvision", true)
                    .put("source", input.getString("type")));
                return DB.on(SRoleDao.class).insertAsync(role);
            });
    }

    private Future<Boolean> bindRole(final String userId, final String roleId) {
        final JsonObject query = new JsonObject()
            .put("userId", userId)
            .put("roleId", roleId);
        return DB.on(RUserRoleDao.class).fetchJOneAsync(query)
            .compose(found -> {
                if (Ut.isNotNil(found)) {
                    return Future.succeededFuture(Boolean.TRUE);
                }
                final RUserRole relation = new RUserRole()
                    .setUserId(userId)
                    .setRoleId(roleId)
                    .setPriority(0);
                return DB.on(RUserRoleDao.class).insertAsync(relation).map(nil -> Boolean.TRUE);
            });
    }

    private String loginField(final String type) {
        if ("EMAIL".equals(type)) {
            return "email";
        }
        if ("SMS".equals(type)) {
            return "mobile";
        }
        return null;
    }

    private String readSigma(final JsonObject tenant, final String identifier) {
        final String sigma = tenant.getString(KName.SIGMA);
        if (StrUtil.isNotBlank(sigma)) {
            return sigma;
        }
        return "AUTO_" + this.normalize(identifier);
    }

    private String readTenantId(final JsonObject tenant) {
        final String tenantId = tenant.getString(KName.TENANT_ID);
        if (StrUtil.isNotBlank(tenantId)) {
            return tenantId;
        }
        return tenant.getString(KName.ID);
    }

    private String readUsername(final JsonObject input, final String field, final String identifier) {
        final String explicit = input.getString("username");
        if (StrUtil.isNotBlank(explicit)) {
            return explicit;
        }
        if ("email".equals(field)) {
            final String[] segments = identifier.split("@", 2);
            if (segments.length > 0 && StrUtil.isNotBlank(segments[0])) {
                return segments[0];
            }
        }
        return identifier;
    }

    private String readPassword(final JsonObject input) {
        final String password = input.getString("password");
        if (StrUtil.isBlank(password)) {
            return Sc.valuePassword();
        }
        return Sc.valuePassword(password);
    }

    private String userCode(final String identifier) {
        return "USER_" + this.normalize(identifier);
    }

    private String normalize(final String value) {
        if (StrUtil.isBlank(value)) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return value.toUpperCase(Locale.ROOT)
            .replaceAll("[^0-9A-Z]+", "_")
            .replaceAll("^_+|_+$", "");
    }
}

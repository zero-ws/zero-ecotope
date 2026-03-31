package io.zerows.extension.module.rbac.spi;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ExAccountProvision;
import io.zerows.extension.skeleton.spi.ExTenantProvision;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

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
                    return this.ensureExisting(found, input, type, identifier)
                        .compose(user -> Ux.futureJ(user.setPassword(null)));
                }
                return this.provisionTenant(input)
                    .compose(tenant -> this.insertUser(input, tenant, field, identifier))
                    .compose(user -> this.ensureRole(user, input, type))
                    .compose(user -> Ux.futureJ(user.setPassword(null)));
            });
    }

    private Future<SUser> ensureExisting(final SUser found, final JsonObject input,
                                         final String type, final String identifier) {
        return this.ensureTenant(found, input, identifier)
            .compose(user -> this.ensureRole(user, input, type));
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
        final String appId = this.readAppId(tenant, input);
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
            .compose(role -> this.alignUserContextByRole(user, role)
                .compose(aligned -> this.ensureRolePermissions(role)
                .compose(nil -> this.bindRole(user.getId(), role.getId()))
                .map(nil -> aligned)));
    }

    private Future<SRole> fetchOrCreateRole(final SUser user, final JsonObject input, final String type) {
        final JsonObject condition = new JsonObject().put(KName.CODE, DEFAULT_ROLE_CODE);
        final String appIdInput = this.readAppId(new JsonObject(), input);
        if (StrUtil.isNotBlank(appIdInput)) {
            condition.put(KName.APP_ID, appIdInput);
        }
        return DB.on(SRoleDao.class).<SRole>fetchAsync(condition)
            .compose(foundRoles -> {
                if (Objects.nonNull(foundRoles) && !foundRoles.isEmpty()) {
                    return this.pickBestRole(foundRoles).map(role -> Objects.isNull(role) ? foundRoles.get(0) : role);
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

    private Future<SRole> pickBestRole(final List<SRole> candidates) {
        final Set<String> roleIds = candidates.stream()
            .filter(Objects::nonNull)
            .map(SRole::getId)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return Future.succeededFuture(null);
        }
        return DB.on(RRolePermDao.class).fetchJAsync(new JsonObject().put("roleId,i", Ut.toJArray(roleIds)))
            .map(relations -> {
                final Map<String, Long> permCount = relations.stream()
                    .filter(Objects::nonNull)
                    .map(item -> (JsonObject) item)
                    .map(item -> item.getString("roleId"))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
                return candidates.stream()
                    .filter(Objects::nonNull)
                    .max(Comparator.comparingLong(role ->
                        permCount.getOrDefault(role.getId(), 0L)))
                    .orElse(null);
            });
    }

    private Future<SUser> alignUserContextByRole(final SUser user, final SRole role) {
        if (Objects.isNull(role)) {
            return Ux.future(user);
        }
        boolean changed = false;
        if (StrUtil.isNotBlank(role.getSigma()) && !role.getSigma().equals(user.getSigma())) {
            user.setSigma(role.getSigma());
            changed = true;
        }
        if (StrUtil.isNotBlank(role.getAppId()) && !role.getAppId().equals(user.getAppId())) {
            user.setAppId(role.getAppId());
            changed = true;
        }
        if (!changed) {
            return Ux.future(user);
        }
        return DB.on(SUserDao.class).updateAsync(user).map(user);
    }

    private Future<SUser> ensureTenant(final SUser user, final JsonObject input, final String identifier) {
        return this.provisionTenant(input).compose(tenant -> {
            if (Ut.isNil(tenant)) {
                return Ux.future(user);
            }
            final String tenantId = this.readTenantId(tenant);
            final String sigma = this.readSigma(tenant, identifier);
            final String appId = this.readAppId(tenant, input);
            final boolean registerUser = "REGISTER".equalsIgnoreCase(user.getCategory());
            boolean changed = false;
            if (StrUtil.isBlank(user.getTenantId())
                || (registerUser && StrUtil.isNotBlank(tenantId) && !tenantId.equals(user.getTenantId()))) {
                user.setTenantId(tenantId);
                changed = true;
            }
            if (StrUtil.isBlank(user.getSigma())
                || (registerUser && StrUtil.isNotBlank(sigma) && !sigma.equals(user.getSigma()))) {
                user.setSigma(sigma);
                changed = true;
            }
            if (StrUtil.isNotBlank(appId)
                && (StrUtil.isBlank(user.getAppId()) || (registerUser && !appId.equals(user.getAppId())))) {
                user.setAppId(appId);
                changed = true;
            }
            if (!changed) {
                return Ux.future(user);
            }
            return DB.on(SUserDao.class).updateAsync(user).map(user);
        });
    }

    private Future<Boolean> ensureRolePermissions(final SRole role) {
        return this.resolveDefaultPermissionIds(role).compose(targetPermissionIds -> {
            if (targetPermissionIds.isEmpty()) {
                return Future.succeededFuture(Boolean.TRUE);
            }
            return DB.on(RRolePermDao.class).fetchJAsync(new JsonObject().put("roleId", role.getId()))
                .compose(existing -> {
                    final Set<String> existingIds = new HashSet<>();
                    final Set<String> loaded = Ut.valueSetString(existing, "permId");
                    if (Objects.nonNull(loaded)) {
                        existingIds.addAll(loaded);
                    }
                    final JsonArray inserts = new JsonArray();
                    targetPermissionIds.stream()
                        .filter(permissionId -> !existingIds.contains(permissionId))
                        .forEach(permissionId -> inserts.add(new JsonObject()
                            .put("roleId", role.getId())
                            .put("permId", permissionId)));
                    if (inserts.isEmpty()) {
                        return Future.succeededFuture(Boolean.TRUE);
                    }
                    return DB.on(RRolePermDao.class).insertAsync(inserts).map(Boolean.TRUE);
                });
        });
    }

    private Future<Set<String>> resolveDefaultPermissionIds(final SRole role) {
        final JsonArray permissionCodes = Sc.valuePermissions();
        if (Ut.isNotNil(permissionCodes)) {
            final JsonObject query = new JsonObject().put("code,i", permissionCodes);
            return DB.on(SPermissionDao.class).<SPermission>fetchAsync(query)
                .compose(permissions -> {
                    final Set<String> ids = this.toPermissionIds(permissions);
                    if (!ids.isEmpty()) {
                        return Future.succeededFuture(ids);
                    }
                    return this.resolveFromTemplateRole(role);
                });
        }
        return this.resolveFromTemplateRole(role);
    }

    private Future<Set<String>> resolveFromTemplateRole(final SRole role) {
        final JsonObject condition = new JsonObject().put(KName.CODE, DEFAULT_ROLE_CODE);
        if (StrUtil.isNotBlank(role.getAppId())) {
            condition.put(KName.APP_ID, role.getAppId());
        }
        return DB.on(SRoleDao.class).<SRole>fetchAsync(condition)
            .compose(roles -> {
                if (Objects.isNull(roles) || roles.isEmpty()) {
                    return Future.succeededFuture(new HashSet<>());
                }
                final Set<String> candidateRoleIds = new HashSet<>();
                roles.stream()
                    .filter(Objects::nonNull)
                    .map(SRole::getId)
                    .filter(StrUtil::isNotBlank)
                    .filter(roleId -> !roleId.equals(role.getId()))
                    .forEach(candidateRoleIds::add);
                if (candidateRoleIds.isEmpty()) {
                    return Future.succeededFuture(new HashSet<>());
                }
                final JsonObject relationCondition = new JsonObject()
                    .put("roleId,i", Ut.toJArray(candidateRoleIds));
                return DB.on(RRolePermDao.class).fetchJAsync(relationCondition)
                    .map(relations -> {
                        final Set<String> ids = new HashSet<>();
                        if (Ut.isNotNil(relations)) {
                            relations.stream()
                                .filter(Objects::nonNull)
                                .map(item -> (JsonObject) item)
                                .map(item -> item.getString("permId"))
                                .filter(StrUtil::isNotBlank)
                                .forEach(ids::add);
                        }
                        return ids;
                    });
            });
    }

    private Set<String> toPermissionIds(final List<SPermission> permissions) {
        final Set<String> ids = new HashSet<>();
        if (Objects.isNull(permissions) || permissions.isEmpty()) {
            return ids;
        }
        permissions.stream()
            .filter(Objects::nonNull)
            .map(SPermission::getId)
            .filter(StrUtil::isNotBlank)
            .forEach(ids::add);
        return ids;
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

    private String readAppId(final JsonObject tenant, final JsonObject input) {
        final String appId = tenant.getString(KName.APP_ID);
        if (StrUtil.isNotBlank(appId)) {
            return appId;
        }
        return input.getString(KName.APP_ID);
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

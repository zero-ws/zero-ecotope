package io.zerows.boot.extension.appcontainer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SResourceDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 权限数据持久化器
 * 负责将 SPermission、SAction、SResource、RRolePerm 数据保存到数据库
 * 支持幂等导入（upsert）
 */
@Slf4j
@SuppressWarnings("all")
class BuildPermPersister {

    private final Vertx vertx;
    private final JsonObject globalConfig;

    private BuildPermPersister(final Vertx vertx, final JsonObject globalConfig) {
        this.vertx = vertx;
        this.globalConfig = globalConfig;
    }

    static BuildPermPersister create(final Vertx vertx, final JsonObject globalConfig) {
        return new BuildPermPersister(vertx, globalConfig);
    }

    /**
     * 保存 SPermission 数据到数据库
     * 唯一键：code + appId
     * 返回 [新增数, 更新数]
     */
    Future<int[]> savePermissions(final Map<String, SPermission> permissions) {

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final List<Future<String>> futures = new ArrayList<>();

        for (final SPermission perm : permissions.values()) {
            final Future<String> future = this.upsertPermission(perm)
                .onSuccess(action -> {
                    if ("insert".equals(action)) {
                        insertCount.incrementAndGet();
                    } else if ("update".equals(action)) {
                        updateCount.incrementAndGet();
                    }
                });
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.info("[ INST ] 权限保存完成: 新增 {} / 更新 {} / 总数 {}", insertCount.get(), updateCount.get(), permissions.size());
            return new int[]{insertCount.get(), updateCount.get()};
        });
    }

    /**
     * 保存 SAction 数据到数据库
     * 唯一键：code + appId
     * 返回 [新增数, 更新数]
     */
    Future<int[]> saveActions(final Map<String, SAction> actions) {

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final List<Future<String>> futures = new ArrayList<>();

        for (final SAction action : actions.values()) {
            final Future<String> future = this.upsertAction(action)
                .onSuccess(result -> {
                    if ("insert".equals(result)) {
                        insertCount.incrementAndGet();
                    } else if ("update".equals(result)) {
                        updateCount.incrementAndGet();
                    }
                });
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.info("[ INST ] 操作保存完成: 新增 {} / 更新 {} / 总数 {}", insertCount.get(), updateCount.get(), actions.size());
            return new int[]{insertCount.get(), updateCount.get()};
        });
    }

    /**
     * 保存 SResource 数据到数据库
     * 唯一键：code + appId
     * 返回 [新增数, 更新数]
     */
    Future<int[]> saveResources(final Map<String, SResource> resources) {

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final List<Future<String>> futures = new ArrayList<>();

        for (final SResource resource : resources.values()) {
            final Future<String> future = this.upsertResource(resource)
                .onSuccess(result -> {
                    if ("insert".equals(result)) {
                        insertCount.incrementAndGet();
                    } else if ("update".equals(result)) {
                        updateCount.incrementAndGet();
                    }
                });
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.info("[ INST ] 资源保存完成: 新增 {} / 更新 {} / 总数 {}", insertCount.get(), updateCount.get(), resources.size());
            return new int[]{insertCount.get(), updateCount.get()};
        });
    }

    /**
     * 保存 RRolePerm 数据到数据库
     * 需要先根据 roleCode 查询 roleId
     * 返回 [新增数, 更新数]
     */
    Future<int[]> saveRolePerms(final List<RRolePerm> rolePerms, final Map<String, String> roleCodeToIdMap) {

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final List<Future<String>> futures = new ArrayList<>();

        for (final RRolePerm rolePerm : rolePerms) {
            // 更新 roleId
            // 注意：rolePerm 中需要设置正确的 roleId

            final Future<String> future = this.upsertRolePerm(rolePerm)
                .onSuccess(result -> {
                    if ("insert".equals(result)) {
                        insertCount.incrementAndGet();
                    } else if ("update".equals(result)) {
                        updateCount.incrementAndGet();
                    }
                });
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.info("[ INST ] 角色权限关联保存完成: 新增 {} / 更新 {} / 总数 {}", insertCount.get(), updateCount.get(), rolePerms.size());
            return new int[]{insertCount.get(), updateCount.get()};
        });
    }

    // ==================== Upsert 方法 ====================

    /**
     * Upsert SPermission
     * 唯一键：code + appId
     */
    private Future<String> upsertPermission(final SPermission perm) {
        final String appId = perm.getAppId();
        final String code = perm.getCode();

        return DB.on(SPermissionDao.class)
            .<SPermission>fetchAsync("APP_ID", appId)
            .compose(list -> {
                // 查找匹配的记录
                SPermission existing = null;
                for (final SPermission p : list) {
                    if (code.equals(p.getCode())) {
                        existing = p;
                        break;
                    }
                }

                if (existing == null) {
                    // 不存在，插入（保持 perm 的 ID 不变，使用加载时生成的 UUID）
                    return DB.on(SPermissionDao.class)
                        .insertAsync(perm)
                        .map(v -> {
                            log.debug("[ INST ] 插入权限: {}", code);
                            return "insert";
                        });
                } else {
                    // 存在，使用数据库中的 ID 并更新记录
                    perm.setId(existing.getId());
                    return DB.on(SPermissionDao.class)
                        .updateAsync(perm)
                        .map(v -> {
                            log.debug("[ INST ] 更新权限: {}", code);
                            return "update";
                        });
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存权限失败: {}", code, err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * Upsert SAction
     * 唯一键：code + appId
     */
    private Future<String> upsertAction(final SAction action) {
        final String appId = action.getAppId();
        final String code = action.getCode();

        return DB.on(SActionDao.class)
            .<SAction>fetchAsync("APP_ID", appId)
            .compose(list -> {
                // 查找匹配的记录
                SAction existing = null;
                for (final SAction a : list) {
                    if (code.equals(a.getCode())) {
                        existing = a;
                        break;
                    }
                }

                if (existing == null) {
                    // 不存在，插入
                    return DB.on(SActionDao.class)
                        .insertAsync(action)
                        .map(v -> {
                            log.debug("[ INST ] 插入操作: {}", code);
                            return "insert";
                        });
                } else {
                    // 存在，更新 ID 和关联字段
                    action.setId(existing.getId());
                    // 保持原有的 permissionId 和 resourceId 关联
                    if (action.getPermissionId() == null) {
                        action.setPermissionId(existing.getPermissionId());
                    }
                    if (action.getResourceId() == null) {
                        action.setResourceId(existing.getResourceId());
                    }
                    return DB.on(SActionDao.class)
                        .updateAsync(action)
                        .map(v -> {
                            log.debug("[ INST ] 更新操作: {}", code);
                            return "update";
                        });
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存操作失败: {}", code, err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * Upsert SResource
     * 唯一键：code + appId
     */
    private Future<String> upsertResource(final SResource resource) {
        final String appId = resource.getAppId();
        final String code = resource.getCode();

        return DB.on(SResourceDao.class)
            .<SResource>fetchAsync("APP_ID", appId)
            .compose(list -> {
                // 查找匹配的记录
                SResource existing = null;
                for (final SResource r : list) {
                    if (code.equals(r.getCode())) {
                        existing = r;
                        break;
                    }
                }

                if (existing == null) {
                    // 不存在，插入（保持 resource 的 ID 不变，使用加载时生成的 UUID）
                    return DB.on(SResourceDao.class)
                        .insertAsync(resource)
                        .map(v -> {
                            log.debug("[ INST ] 插入资源: {}", code);
                            return "insert";
                        });
                } else {
                    // 存在，使用数据库中的 ID 并更新记录
                    resource.setId(existing.getId());
                    return DB.on(SResourceDao.class)
                        .updateAsync(resource)
                        .map(v -> {
                            log.debug("[ INST ] 更新资源: {}", code);
                            return "update";
                        });
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存资源失败: {}", code, err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * Upsert RRolePerm
     * 唯一键：roleId + permId
     */
    private Future<String> upsertRolePerm(final RRolePerm rolePerm) {
        if (rolePerm.getRoleId() == null || rolePerm.getPermId() == null) {
            return Future.succeededFuture("skip");
        }

        return DB.on(RRolePermDao.class)
            .<RRolePerm>fetchAsync("ROLE_ID", rolePerm.getRoleId())
            .compose(list -> {
                // 查找匹配的记录
                boolean exists = false;
                for (final RRolePerm rp : list) {
                    if (rolePerm.getPermId().equals(rp.getPermId())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // 不存在，插入
                    return DB.on(RRolePermDao.class)
                        .insertAsync(rolePerm)
                        .map(v -> {
                            log.debug("[ INST ] 插入角色权限关联: roleId={}, permId={}",
                                rolePerm.getRoleId(), rolePerm.getPermId());
                            return "insert";
                        });
                } else {
                    // 已存在，跳过
                    return Future.succeededFuture("skip");
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存角色权限关联失败", err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * 加载角色 ID 映射
     * 返回 Map&lt;roleCode, roleId&gt;
     */
    Future<Map<String, String>> loadRoleIdMap() {
        final Map<String, String> roleCodeToIdMap = new java.util.HashMap<>();
        final String appId = this.globalConfig.getString("appId");

        return DB.on(SRoleDao.class)
            .<SRole>fetchAsync("APP_ID", appId)
            .map(roles -> {
                for (final SRole role : roles) {
                    if (role.getCode() != null) {
                        roleCodeToIdMap.put(role.getCode(), role.getId());
                    }
                }
                log.info("[ INST ] 加载角色映射: {} 个", roleCodeToIdMap.size());
                return roleCodeToIdMap;
            })
            .recover(err -> {
                log.error("[ INST ] 加载角色映射失败", err);
                return Future.succeededFuture(roleCodeToIdMap);
            });
    }
}
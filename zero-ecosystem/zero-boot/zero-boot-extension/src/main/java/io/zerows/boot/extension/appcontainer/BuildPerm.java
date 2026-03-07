package io.zerows.boot.extension.appcontainer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 权限导入程序
 * 负责加载和持久化权限数据（SPermission、SAction、SResource、RRolePerm）
 * <p>
 * 输入目录：
 * - plugins/{MID}/security/RBAC_RESOURCE/ - 资源定义
 * - plugins/{MID}/security/RBAC_ROLE/ - 角色权限关联
 * <p>
 * 核心实体：
 * - SPermission - 权限
 * - SAction - 操作
 * - SResource - 资源
 * - RRolePerm - 角色权限关联
 * <p>
 * 数据填充规则：
 * 1. SPermission：
 * - 一级目录名 → type
 * - 二级目录名 → directory
 * - 三级目录名 → name
 * - PERM.yml → code, identifier, comment
 * <p>
 * 2. SAction & SResource：
 * - 文件名格式：{name}@{method}@{uri}.yml
 * - 文件内容：keyword, resource
 * - SAction.code = "act." + keyword
 * - SResource.code = "res." + keyword
 * - level 默认值：GET=1, POST=4, PUT=8, DELETE=12
 * - modeRole 默认值：UNION
 * <p>
 * 3. RRolePerm：
 * - 一级目录 → 角色 CODE
 * - 二级目录 → {code}@{priority}
 * - 文件内容 → 权限 code 数组
 */
@Slf4j
@SuppressWarnings("all")
public class BuildPerm {

    /**
     * 执行权限数据的加载与持久化
     * <p>
     * 核心流程：
     * 1. 加载全局配置（environment.json 的 global 节点）
     * 2. 扫描所有模块的 security 目录（plugins/{MID}/security/）
     * 3. 使用 BuildPermLoader 加载权限数据
     * 4. 使用 BuildPermPersister 持久化到数据库（按 code+appId 判重，upsert）
     * 5. 输出统计信息
     *
     * @param vertx Vert.x 实例
     * @return Future&lt;Boolean&gt; 成功返回 true，失败返回 false
     */
    public static Future<Boolean> run(final Vertx vertx) {
        log.info("[ INST ] ========================================");
        log.info("[ INST ] 开始加载权限数据...");
        log.info("[ INST ] ========================================");

        try {
            // 1. 加载全局配置（从 environment.json 的 global 节点）
            final JsonObject globalConfig = BuildShared.loadGlobalConfig();

            // 2. 扫描 security 目录
            final InstPerm instPerm = InstPerm.of();
            final Map<String, java.net.URI> resourceDirs = instPerm.ioResource();
            final Map<String, java.net.URI> roleDirs = instPerm.ioRole();

            log.info("[ INST ] 扫描到 {} 个 RBAC_RESOURCE 目录，{} 个 RBAC_ROLE 目录",
                resourceDirs.size(), roleDirs.size());

            if (resourceDirs.isEmpty()) {
                log.warn("[ INST ] 未找到任何权限资源目录");
                return Future.succeededFuture(false);
            }

            // 3. 加载权限数据
            final BuildPermLoader loader = BuildPermLoader.create(globalConfig);
            loader.loadResources(resourceDirs);

            // 3.1 先加载角色映射（从数据库）
            final BuildPermPersister persister = BuildPermPersister.create(vertx, globalConfig);
            return persister.loadRoleIdMap()
                .compose(roleIdMap -> {
                    log.info("[ INST ] 从数据库加载到 {} 个角色", roleIdMap.size());

                    // 3.2 加载角色权限关联（只加载数据库中存在的角色）
                    loader.loadRoles(roleDirs, roleIdMap);

                    final Map<String, SPermission> permissions = loader.getPermissions();
                    final Map<String, SAction> actions = loader.getActions();
                    final Map<String, SResource> resources = loader.getResources();
                    final List<RRolePerm> rolePerms = loader.getRolePerms();
                    final Map<RRolePerm, String> rolePermToRoleCode = loader.getRolePermToRoleCode();
                    final Map<String, Integer> rolePermCounts = loader.getRolePermCounts();
                    final java.util.Set<String> missingPermDirs = loader.getMissingPermDirs();

                    if (permissions.isEmpty() && actions.isEmpty() && resources.isEmpty()) {
                        log.warn("[ INST ] 未加载到任何权限数据");
                        return Future.succeededFuture(false);
                    }

                    // 打印缺少 PERM.yml 的目录统计
                    if (!missingPermDirs.isEmpty()) {
                        log.warn("[ INST ] ========================================");
                        log.warn("[ INST ] 以下目录缺少 PERM.yml，共 {} 个:", missingPermDirs.size());
                        missingPermDirs.stream().sorted().forEach(dir ->
                            log.warn("[ INST ]   - {}", dir)
                        );
                        log.warn("[ INST ] ========================================");
                    }

                    // 4. 持久化到数据库
                    // 保存权限前，记录旧的 permissionId 映射
                    final Map<String, String> oldPermIdToCode = new java.util.HashMap<>();
                    for (final SPermission permission : permissions.values()) {
                        oldPermIdToCode.put(permission.getId(), permission.getCode());
                    }

                    // 4.1 先保存 SPermission
                    return persister.savePermissions(permissions)
                        .compose(permStats -> {
                            // 4.2 处理 permissionId 关联（权限 upsert 后 ID 可能变化）
                            final Map<String, String> permCodeToId = new java.util.HashMap<>();
                            for (final SPermission permission : permissions.values()) {
                                permCodeToId.put(permission.getCode(), permission.getId());
                            }

                            // 更新 SAction 的 permissionId
                            for (final SAction action : actions.values()) {
                                final String oldPermId = action.getPermissionId();
                                final String permCode = oldPermIdToCode.get(oldPermId);
                                if (permCode != null) {
                                    final String newPermId = permCodeToId.get(permCode);
                                    if (newPermId != null) {
                                        action.setPermissionId(newPermId);
                                    }
                                }
                            }

                            // 更新 RRolePerm 的 permId
                            for (final RRolePerm rolePerm : rolePerms) {
                                final String oldPermId = rolePerm.getPermId();
                                final String permCode = oldPermIdToCode.get(oldPermId);
                                if (permCode != null) {
                                    final String newPermId = permCodeToId.get(permCode);
                                    if (newPermId != null) {
                                        rolePerm.setPermId(newPermId);
                                    }
                                }
                            }

                            // 4.3 保存 SResource（需要先于 SAction，因为 SAction 引用 resourceId）
                            return persister.saveResources(resources)
                                .compose(resStats -> {
                                    // 保存后，resource 的 ID 可能已经变化（upsert 时使用了数据库中的 ID）
                                    // 需要重新建立 code -> id 映射
                                    final Map<String, String> resourceCodeToId = new java.util.HashMap<>();
                                    for (final SResource resource : resources.values()) {
                                        resourceCodeToId.put(resource.getCode(), resource.getId());
                                    }

                                    // 更新 SAction 的 resourceId
                                    for (final SAction action : actions.values()) {
                                        final String keyword = action.getCode().substring(4); // 去掉 "act." 前缀
                                        final String resourceCode = "res." + keyword;
                                        final String resourceId = resourceCodeToId.get(resourceCode);
                                        if (resourceId != null) {
                                            action.setResourceId(resourceId);
                                        }
                                    }

                                    return Future.succeededFuture(new int[][]{permStats, resStats});
                                });
                        })
                        .compose(stats -> {
                            // 4.4 保存 SAction
                            final int[] permStats = stats[0];
                            final int[] resStats = stats[1];

                            return persister.saveActions(actions)
                                .map(actStats -> new int[][]{permStats, resStats, actStats});
                        })
                        .compose(stats -> {
                            // 4.5 保存 RRolePerm（roleId 已在加载时设置）
                            final int[] permStats = stats[0];
                            final int[] resStats = stats[1];
                            final int[] actStats = stats[2];

                            return persister.saveRolePerms(rolePerms, roleIdMap)
                                .map(rpStats -> {
                                    log.info("[ INST ] ========================================");
                                    log.info("[ INST ] 导入统计:");
                                    log.info("[ INST ]   权限(SPermission): 新增 {} / 更新 {}", permStats[0], permStats[1]);
                                    log.info("[ INST ]   操作(SAction): 新增 {} / 更新 {}", actStats[0], actStats[1]);
                                    log.info("[ INST ]   资源(SResource): 新增 {} / 更新 {}", resStats[0], resStats[1]);
                                    log.info("[ INST ]   角色权限关联(RRolePerm): 新增 {} / 更新 {}", rpStats[0], rpStats[1]);

                                    // 打印每个角色的权限关联数量
                                    if (!rolePermCounts.isEmpty()) {
                                        log.info("[ INST ] ----------------------------------------");
                                        log.info("[ INST ] 各角色权限关联数量:");
                                        rolePermCounts.entrySet().stream()
                                            .sorted(Map.Entry.comparingByKey())
                                            .forEach(entry ->
                                                log.info("[ INST ]   {}: {} 个权限", entry.getKey(), entry.getValue())
                                            );
                                    }

                                    log.info("[ INST ] ========================================");
                                    return true;
                                });
                        });
                })
                .recover(err -> {
                    log.error("[ INST ] 导入失败", err);
                    return Future.succeededFuture(false);
                });

        } catch (final Exception e) {
            log.error("[ INST ] 加载失败", e);
            return Future.failedFuture(e);
        }
    }
}
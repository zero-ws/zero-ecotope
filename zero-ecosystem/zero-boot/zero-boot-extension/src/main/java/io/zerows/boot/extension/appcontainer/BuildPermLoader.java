package io.zerows.boot.extension.appcontainer;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限数据加载器
 * 负责从 RBAC_RESOURCE 和 RBAC_ROLE 目录加载权限数据
 */
@Slf4j
@SuppressWarnings("all")
class BuildPermLoader {

    private final JsonObject globalConfig;

    // 加载结果
    private final Map<String, SPermission> permissions = new ConcurrentHashMap<>();
    private final Map<String, SAction> actions = new ConcurrentHashMap<>();
    private final Map<String, SResource> resources = new ConcurrentHashMap<>();
    private final List<RRolePerm> rolePerms = new ArrayList<>();

    // 缓存：identifier -> permissionId (用于关联 SAction 和 SPermission)
    private final Map<String, String> identifierToPermId = new ConcurrentHashMap<>();

    // 缓存：code -> resourceId (用于关联 SAction 和 SResource)
    private final Map<String, String> resourceCodeToId = new ConcurrentHashMap<>();

    // 缓存：RRolePerm 对应的 roleCode
    private final Map<RRolePerm, String> rolePermToRoleCode = new ConcurrentHashMap<>();

    // 缓存：角色权限唯一键 roleId#permId（用于幂等去重）
    private final Set<String> rolePermKeys = ConcurrentHashMap.newKeySet();

    // 统计：每个角色的权限关联数量
    private final Map<String, Integer> rolePermCounts = new ConcurrentHashMap<>();

    // 统计：缺少 PERM.yml 的目录（type/directory/permName）
    private final Set<String> missingPermDirs = ConcurrentHashMap.newKeySet();

    private BuildPermLoader(final JsonObject globalConfig) {
        this.globalConfig = globalConfig;
    }

    static BuildPermLoader create(final JsonObject globalConfig) {
        return new BuildPermLoader(globalConfig);
    }

    /**
     * 加载 RBAC_RESOURCE 目录下的所有权限数据
     * 采用两阶段加载策略解决跨模块依赖问题：
     * 1. 第一阶段：加载所有模块的 PERM.yml（填充 identifierToPermId）
     * 2. 第二阶段：加载所有模块的 action yml（此时可正确关联 permissionId）
     *
     * @param resourceDirs Map&lt;MID, 资源目录URI&gt;
     */
    void loadResources(final Map<String, URI> resourceDirs) {
        log.info("[ INST ] 开始加载权限资源，共 {} 个模块", resourceDirs.size());

        // 第一阶段：只加载 PERM.yml
        log.info("[ INST ] 第一阶段：加载所有 PERM.yml");
        for (final Map.Entry<String, URI> entry : resourceDirs.entrySet()) {
            final String mid = entry.getKey();
            final URI uri = entry.getValue();

            try {
                this.loadPermissionsOnly(mid, uri);
            } catch (final Exception e) {
                log.error("[ INST ] 加载模块 {} PERM.yml 失败", mid, e);
            }
        }
        log.info("[ INST ] 第一阶段完成: SPermission {} 个", this.permissions.size());

        // 第二阶段：加载 action yml 和 resource
        log.info("[ INST ] 第二阶段：加载所有 action yml");
        for (final Map.Entry<String, URI> entry : resourceDirs.entrySet()) {
            final String mid = entry.getKey();
            final URI uri = entry.getValue();

            try {
                this.loadActionsAndResources(mid, uri);
            } catch (final Exception e) {
                log.error("[ INST ] 加载模块 {} action yml 失败", mid, e);
            }
        }

        log.info("[ INST ] 权限资源加载完成: SPermission {} 个, SAction {} 个, SResource {} 个",
            this.permissions.size(), this.actions.size(), this.resources.size());
    }

    /**
     * 加载 RBAC_ROLE 目录下的所有角色权限关联数据
     *
     * @param roleDirs  Map&lt;MID, 角色目录URI&gt;
     * @param roleIdMap Map&lt;roleCode, roleId&gt; 从数据库加载的角色映射
     */
    void loadRoles(final Map<String, URI> roleDirs, final Map<String, String> roleIdMap) {
        log.info("[ INST ] 开始加载角色权限关联，共 {} 个模块，数据库中有 {} 个角色",
            roleDirs.size(), roleIdMap.size());

        for (final Map.Entry<String, URI> entry : roleDirs.entrySet()) {
            final String mid = entry.getKey();
            final URI uri = entry.getValue();

            try {
                this.loadRoleFromUri(mid, uri, roleIdMap);
            } catch (final Exception e) {
                log.error("[ INST ] 加载模块 {} 角色权限关联失败", mid, e);
            }
        }

        log.info("[ INST ] 角色权限关联加载完成: RRolePerm {} 个", this.rolePerms.size());
    }

    Map<String, SPermission> getPermissions() {
        return new HashMap<>(this.permissions);
    }

    Map<String, SAction> getActions() {
        return new HashMap<>(this.actions);
    }

    Map<String, SResource> getResources() {
        return new HashMap<>(this.resources);
    }

    List<RRolePerm> getRolePerms() {
        return new ArrayList<>(this.rolePerms);
    }

    // ==================== RBAC_RESOURCE 加载逻辑 ====================

    /**
     * 第一阶段：只加载 PERM.yml
     */
    private void loadPermissionsOnly(final String mid, final URI uri) throws Exception {
        if ("jar".equals(uri.getScheme())) {
            log.warn("[ INST ] 模块 {} 使用 JAR 协议，暂不支持两阶段加载: {}", mid, uri);
            return;
        }

        final File resourceDir = new File(uri.getPath());
        if (!resourceDir.exists() || !resourceDir.isDirectory()) {
            log.warn("[ INST ] 模块 {} 资源目录不存在: {}", mid, uri);
            return;
        }

        this.scanPermissionsRecursive(resourceDir, null, null, null);
    }

    /**
     * 第二阶段：加载 action yml 和 resource
     */
    private void loadActionsAndResources(final String mid, final URI uri) throws Exception {
        if ("jar".equals(uri.getScheme())) {
            return;
        }

        final File resourceDir = new File(uri.getPath());
        if (!resourceDir.exists() || !resourceDir.isDirectory()) {
            return;
        }

        this.scanActionsRecursive(resourceDir, null, null, null);
    }

    /**
     * 递归扫描并加载 PERM.yml（第一阶段）
     */
    private void scanPermissionsRecursive(final File dir, final String type, final String directory,
                                          final String permName) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (file.isDirectory()) {
                String newType = type;
                String newDirectory = directory;
                String newPermName = permName;

                if (type == null) {
                    newType = file.getName();
                } else if (directory == null) {
                    newDirectory = file.getName();
                } else if (permName == null) {
                    newPermName = file.getName();

                    // 加载 PERM.yml
                    final File permFile = new File(file, "PERM.yml");
                    if (permFile.exists() && permFile.isFile()) {
                        this.loadPermission(permFile, newType, newDirectory, newPermName);
                    }
                }

                this.scanPermissionsRecursive(file, newType, newDirectory, newPermName);
            }
        }
    }

    /**
     * 递归扫描并加载 action yml（第二阶段）
     */
    private void scanActionsRecursive(final File dir, final String type, final String directory,
                                      final String permName) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (file.isDirectory()) {
                String newType = type;
                String newDirectory = directory;
                String newPermName = permName;

                if (type == null) {
                    newType = file.getName();
                } else if (directory == null) {
                    newDirectory = file.getName();
                } else if (permName == null) {
                    newPermName = file.getName();
                }

                this.scanActionsRecursive(file, newType, newDirectory, newPermName);

            } else if (file.getName().endsWith(".yml") && !file.getName().equals("PERM.yml")) {
                if (type != null && directory != null && permName != null) {
                    this.loadActionAndResource(file, type, directory, permName);
                }
            }
        }
    }

    /**
     * 从 URI 加载权限资源（已废弃，保留用于兼容）
     * @deprecated 使用两阶段加载：loadPermissionsOnly + loadActionsAndResources
     */
    @Deprecated
    private void loadResourceFromUri(final String mid, final URI uri) throws Exception {
        final File resourceDir = new File(uri.getPath());
        if (!resourceDir.exists() || !resourceDir.isDirectory()) {
            log.warn("[ INST ] 资源目录不存在: {}", uri);
            return;
        }

        // 递归加载资源目录
        // 目录结构：一级目录(type) / 二级目录(directory) / 三级目录(name)
        this.loadResourceRecursive(resourceDir, null, null, null);
    }

    /**
     * 递归加载资源目录（已废弃，保留用于兼容）
     * @deprecated 使用 scanPermissionsRecursive + scanActionsRecursive
     */
    @Deprecated
    private void loadResourceRecursive(final File dir, final String type, final String directory,
                                       final String permName) throws Exception {
        // 保留兼容方法，不再使用
        this.scanPermissionsRecursive(dir, type, directory, permName);
        this.scanActionsRecursive(dir, type, directory, permName);
    }

    /**
     * 加载 SPermission
     * PERM.yml 格式：
     * data:
     * code: "perm.xxx.xxx"
     * identifier: "xxx.xxx"
     * comment: "备注" (可选)
     */
    private void loadPermission(final File file, final String type, final String directory,
                                final String permName) throws Exception {
        final JsonObject data = Ut.ioYaml(file.getAbsolutePath());
        if (data == null || !data.containsKey("data")) {
            log.warn("[ INST ] PERM.yml 格式错误: {}", file.getAbsolutePath());
            return;
        }

        final JsonObject permData = data.getJsonObject("data");
        final String code = permData.getString("code");
        final String identifier = permData.getString("identifier");

        if (code == null || identifier == null) {
            log.warn("[ INST ] PERM.yml 缺少 code 或 identifier: {}", file.getAbsolutePath());
            return;
        }

        // 使用反序列化创建 SPermission（自动映射 YAML 中的所有字段）
        final SPermission permission = Ut.deserialize(permData, SPermission.class);

        // 生成 ID
        final String permId = UUID.randomUUID().toString();
        permission.setId(permId);

        // 覆盖目录结构字段
        permission.setName(permName);
        permission.setType(type);
        permission.setDirectory(directory);

        // comment：如果 YAML 中没有，使用全路径 TYPE/DIRECTORY/NAME
        if (permission.getComment() == null || permission.getComment().isEmpty()) {
            permission.setComment(type + "/" + directory + "/" + permName);
        }

        // 填充全局字段
        this.fillGlobalFields(permission);

        // 存入缓存
        this.permissions.put(code, permission);
        this.identifierToPermId.put(identifier, permId);

        log.debug("[ INST ] 加载 SPermission: code={}, identifier={}", code, identifier);
    }

    /**
     * 加载 SAction 和 SResource
     * 文件名格式：{name}@{method}@{uri}.yml
     * 例如：视图读取@GET@_api_acl_role-view_$owner_$res.yml
     * <p>
     * 文件内容格式：
     * data:
     * keyword: "acl.role-view.fetch"
     * resource: "resource.security"
     * level: 10 (可选)
     * virtual: true (可选)
     * ... (其他字段可选)
     */
    private void loadActionAndResource(final File file, final String type, final String directory,
                                       final String permName) throws Exception {
        // 解析文件名
        final String fileName = file.getName();
        final String[] nameParts = this.parseActionFileName(fileName);
        if (nameParts == null) {
            log.warn("[ INST ] 无法解析文件名: {}", fileName);
            return;
        }

        final String actionName = nameParts[0];
        final String method = nameParts[1];
        final String uriPattern = nameParts[2];

        // 加载文件内容
        final JsonObject data = Ut.ioYaml(file.getAbsolutePath());
        if (data == null || !data.containsKey("data")) {
            log.warn("[ INST ] Action yml 格式错误: {}", file.getAbsolutePath());
            return;
        }

        final JsonObject actionData = data.getJsonObject("data");
        final String keyword = actionData.getString("keyword");
        final String resourceType = actionData.getString("resource");
        final String overrideIdentifier = actionData.getString("identifier");

        if (keyword == null) {
            log.warn("[ INST ] Action yml 缺少 keyword: {}", file.getAbsolutePath());
            return;
        }

        // 获取当前目录对应的 permissionId
        // 如果 action yml 中有 identifier，使用它；否则从 PERM.yml 中获取
        final String permIdentifier = overrideIdentifier != null ?
            overrideIdentifier : this.extractPermIdentifier(permName, directory, type);
        final String permissionId = this.findPermissionIdByIdentifier(permIdentifier, type, directory, permName);

        // 生成 ID
        final String actionId = UUID.randomUUID().toString();
        final String resourceId = UUID.randomUUID().toString();

        // 使用反序列化创建 SAction（自动映射 YAML 中的所有字段）
        final SAction action = Ut.deserialize(actionData, SAction.class);
        action.setId(actionId);
        action.setCode("act." + keyword);
        action.setName(actionName);
        action.setMethod(method);
        action.setUri(uriPattern);
        action.setPermissionId(permissionId);
        action.setResourceId(resourceId);

        // 如果 YAML 中没有 level，设置默认值
        if (action.getLevel() == null) {
            action.setLevel(this.getDefaultLevel(method));
        }

        this.fillGlobalFields(action);

        // 使用反序列化创建 SResource（自动映射 YAML 中的所有字段）
        final SResource resource = Ut.deserialize(actionData, SResource.class);
        resource.setId(resourceId);
        resource.setCode("res." + keyword);
        resource.setName(actionName);
        resource.setIdentifier(permIdentifier);  // 使用 permIdentifier，不是 permName

        // 覆盖 type 字段（从 YAML 的 resource 字段映射）
        if (resourceType != null) {
            resource.setType(resourceType);
        }

        // level 与关联的 Action 保持一致
        resource.setLevel(action.getLevel());

        // 如果 YAML 中没有 modeRole，设置默认值
        if (resource.getModeRole() == null || resource.getModeRole().isEmpty()) {
            resource.setModeRole("UNION");
        }

        // comment：如果 YAML 中没有，使用 name 作为 fallback
        if (resource.getComment() == null || resource.getComment().isEmpty()) {
            resource.setComment(actionName);
        }

        this.fillGlobalFields(resource);

        // 存入缓存
        this.actions.put(action.getCode(), action);
        this.resources.put(resource.getCode(), resource);
        this.resourceCodeToId.put(resource.getCode(), resourceId);

        log.debug("[ INST ] 加载 SAction: code={}, uri={}, method={}", action.getCode(), uriPattern, method);
        log.debug("[ INST ] 加载 SResource: code={}, type={}", resource.getCode(), resourceType);
    }

    /**
     * 解析文件名
     * 格式：{name}@{method}@{uri}.yml
     * 例如：视图读取@GET@_api_acl_role-view_$owner_$res.yml
     * 返回：[name, method, uri]
     */
    private String[] parseActionFileName(final String fileName) {
        // 去掉 .yml 后缀
        String name = fileName;
        if (name.endsWith(".yml")) {
            name = name.substring(0, name.length() - 4);
        }

        // 按 @ 分割
        final int firstAt = name.indexOf('@');
        if (firstAt < 0) {
            return null;
        }

        final int secondAt = name.indexOf('@', firstAt + 1);
        if (secondAt < 0) {
            return null;
        }

        final String actionName = name.substring(0, firstAt);
        final String method = name.substring(firstAt + 1, secondAt);
        String uriPattern = name.substring(secondAt + 1);

        // 转换 URI：_ 替换成 /，$ 替换成 :
        uriPattern = uriPattern.replace("_", "/");
        uriPattern = uriPattern.replace("$", ":");

        return new String[]{actionName, method, uriPattern};
    }

    /**
     * 根据 HTTP 方法获取默认 level
     * GET = 1, POST = 4, PUT = 8, DELETE = 12
     */
    private Integer getDefaultLevel(final String method) {
        if (method == null) {
            return 1;
        }
        switch (method.toUpperCase()) {
            case "GET":
                return 1;
            case "POST":
                return 4;
            case "PUT":
                return 8;
            case "DELETE":
                return 12;
            default:
                return 1;
        }
    }

    /**
     * 提取当前目录对应的 permission identifier
     * 从已加载的 SPermission 中查找
     */
    private String extractPermIdentifier(final String permName, final String directory, final String type) {
        // 从已加载的 permissions 中查找匹配的 identifier
        for (final SPermission perm : this.permissions.values()) {
            if (permName.equals(perm.getName()) &&
                directory.equals(perm.getDirectory()) &&
                type.equals(perm.getType())) {
                return perm.getIdentifier();
            }
        }

        // 如果找不到，返回默认值
        return type + "." + directory + "." + permName;
    }

    /**
     * 根据 identifier 查找 permissionId
     */
    private String findPermissionIdByIdentifier(final String identifier, final String type,
                                                final String directory, final String permName) {
        // 先从缓存中查找
        final String permId = this.identifierToPermId.get(identifier);
        if (permId != null) {
            return permId;
        }

        // 按 directory/type 查找（回退逻辑）
        for (final SPermission perm : this.permissions.values()) {
            if (directory.equals(perm.getDirectory()) && type.equals(perm.getType())) {
                return perm.getId();
            }
        }

        // 记录缺少 PERM.yml 的三级目录
        this.missingPermDirs.add(type + "/" + directory + "/" + permName);
        return null;
    }

    // ==================== RBAC_ROLE 加载逻辑 ====================

    /**
     * 从 URI 加载角色权限关联
     */
    private void loadRoleFromUri(final String mid, final URI uri, final Map<String, String> roleIdMap) throws Exception {
        if (uri == null) {
            return;
        }

        final File roleDir = new File(uri.getPath());
        if (!roleDir.exists() || !roleDir.isDirectory()) {
            return;
        }

        // 遍历一级目录（角色 code）
        final File[] roleCodeDirs = roleDir.listFiles(File::isDirectory);
        if (roleCodeDirs == null || roleCodeDirs.length == 0) {
            return;
        }

        for (final File roleCodeDir : roleCodeDirs) {
            final String roleCode = roleCodeDir.getName();

            // 检查角色是否存在于数据库中
            final String roleId = roleIdMap.get(roleCode);
            if (roleId == null) {
                continue;
            }

            this.loadRolePermForRole(roleCode, roleId, roleCodeDir);
        }
    }

    /**
     * 加载单个角色的权限关联
     */
    private void loadRolePermForRole(final String roleCode, final String roleId, final File roleCodeDir) throws Exception {
        if (roleCodeDir == null || !roleCodeDir.exists()) {
            return;
        }

        // 遍历目录下的 yml 文件
        final File[] ymlFiles = roleCodeDir.listFiles((d, name) -> name != null && name.endsWith(".yml"));
        if (ymlFiles == null || ymlFiles.length == 0) {
            return;
        }

        for (final File ymlFile : ymlFiles) {
            this.loadRolePermFromFile(roleCode, roleId, ymlFile);
        }
    }

    /**
     * 从 yml 文件加载角色权限关联
     * 文件格式：xxx@{field}.yml
     * 内容格式：
     * data:
     * - "perm.xxx.xxx"
     * - "perm.yyy.yyy"
     */
    private void loadRolePermFromFile(final String roleCode, final String roleId, final File ymlFile) throws Exception {
        if (ymlFile == null || !ymlFile.exists()) {
            return;
        }

        // 加载文件内容
        final JsonObject data = Ut.ioYaml(ymlFile.getAbsolutePath());
        if (data == null) {
            return;
        }

        // 使用 Ut.valueJArray 安全提取数组
        final io.vertx.core.json.JsonArray permArray = Ut.valueJArray(data, "data");
        if (permArray == null || permArray.isEmpty()) {
            return;
        }

        // 创建 RRolePerm
        int rolePermCount = 0;
        for (int i = 0; i < permArray.size(); i++) {
            final String permCode = permArray.getString(i);
            if (permCode == null || permCode.isEmpty()) {
                continue;
            }

            // 根据 permCode 查找 permissionId
            final SPermission perm = this.permissions.get(permCode);
            if (perm == null) {
                continue;
            }

            // 创建 RRolePerm（幂等去重：同一 roleId + permId 只保留一次）
            final String rolePermKey = roleId + "#" + perm.getId();
            if (!this.rolePermKeys.add(rolePermKey)) {
                continue;
            }

            final RRolePerm rolePerm = new RRolePerm();
            rolePerm.setRoleId(roleId);
            rolePerm.setPermId(perm.getId());

            this.rolePerms.add(rolePerm);
            this.rolePermToRoleCode.put(rolePerm, roleCode);
            rolePermCount++;
        }

        // 统计每个角色的权限关联数量
        this.rolePermCounts.merge(roleCode, rolePermCount, Integer::sum);
    }

    /**
     * 获取 RRolePerm 对应的 roleCode 映射
     */
    Map<RRolePerm, String> getRolePermToRoleCode() {
        return new HashMap<>(this.rolePermToRoleCode);
    }

    /**
     * 获取每个角色的权限关联数量统计
     */
    Map<String, Integer> getRolePermCounts() {
        return new HashMap<>(this.rolePermCounts);
    }

    /**
     * 获取缺少 PERM.yml 的目录统计
     */
    Set<String> getMissingPermDirs() {
        return new HashSet<>(this.missingPermDirs);
    }

    // ==================== 辅助方法 ====================

    /**
     * 填充全局配置字段
     */
    private void fillGlobalFields(final SPermission perm) {
        if (this.globalConfig == null) {
            return;
        }

        perm.setSigma(this.globalConfig.getString("sigma"));
        perm.setAppId(this.globalConfig.getString("appId"));
        perm.setLanguage(this.globalConfig.getString("language"));
        perm.setActive(this.globalConfig.getBoolean("active", true));
        perm.setTenantId(this.globalConfig.getString("tenantId"));

        final LocalDateTime now = LocalDateTime.now();
        perm.setCreatedAt(now);
        perm.setUpdatedAt(now);
        perm.setCreatedBy(this.globalConfig.getString("createdBy"));
        perm.setUpdatedBy(this.globalConfig.getString("updatedBy"));
    }

    /**
     * 填充全局配置字段
     */
    private void fillGlobalFields(final SAction action) {
        if (this.globalConfig == null) {
            return;
        }

        action.setSigma(this.globalConfig.getString("sigma"));
        action.setAppId(this.globalConfig.getString("appId"));
        action.setLanguage(this.globalConfig.getString("language"));
        action.setActive(this.globalConfig.getBoolean("active", true));
        action.setTenantId(this.globalConfig.getString("tenantId"));

        final LocalDateTime now = LocalDateTime.now();
        action.setCreatedAt(now);
        action.setUpdatedAt(now);
        action.setCreatedBy(this.globalConfig.getString("createdBy"));
        action.setUpdatedBy(this.globalConfig.getString("updatedBy"));
    }

    /**
     * 填充全局配置字段
     */
    private void fillGlobalFields(final SResource resource) {
        if (this.globalConfig == null) {
            return;
        }

        resource.setSigma(this.globalConfig.getString("sigma"));
        resource.setAppId(this.globalConfig.getString("appId"));
        resource.setLanguage(this.globalConfig.getString("language"));
        resource.setActive(this.globalConfig.getBoolean("active", true));
        resource.setTenantId(this.globalConfig.getString("tenantId"));

        final LocalDateTime now = LocalDateTime.now();
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);
        resource.setCreatedBy(this.globalConfig.getString("createdBy"));
        resource.setUpdatedBy(this.globalConfig.getString("updatedBy"));
    }
}
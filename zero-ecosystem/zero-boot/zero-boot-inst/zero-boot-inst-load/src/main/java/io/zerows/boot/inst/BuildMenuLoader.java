package io.zerows.boot.inst;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XMenu;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用和菜单加载器
 * 负责从 apps 目录加载 XApp 和 XMenu 数据
 */
@Slf4j
class BuildMenuLoader {

    private final Vertx vertx;
    private final Map<String, XApp> apps = new ConcurrentHashMap<>();
    private final Map<String, List<XMenu>> menus = new ConcurrentHashMap<>();
    private final Map<String, String> menuUuidCache = new ConcurrentHashMap<>();
    private final Map<String, String> dirPathToMenuId = new ConcurrentHashMap<>(); // 目录路径 -> 菜单ID
    private final Map<String, Integer> menuIdToLevel = new ConcurrentHashMap<>(); // 菜单ID -> Level
    private final Map<String, String> dirNameToAppId = new ConcurrentHashMap<>(); // 目录名 -> 应用UUID
    private final Map<String, String> instanceMap; // code -> UUID 映射（来自 instance.yml）
    private final JsonObject globalConfig;

    private BuildMenuLoader(final Vertx vertx, final JsonObject globalConfig, final Map<String, String> instanceMap) {
        this.vertx = vertx;
        this.globalConfig = globalConfig;
        this.instanceMap = instanceMap;
    }

    static BuildMenuLoader create(final Vertx vertx, final JsonObject globalConfig, final Map<String, String> instanceMap) {
        return new BuildMenuLoader(vertx, globalConfig, instanceMap);
    }

    /**
     * 从 URI 列表加载应用数据
     * 返回 Future，支持异步数据库查询
     */
    Future<Void> loadApps(final List<URI> appUris) {
        log.info("[ INST ] 开始加载应用数据，共 {} 个文件", appUris.size());

        final List<Future<Void>> futures = new ArrayList<>();
        for (final URI uri : appUris) {
            final Future<Void> future = this.loadAppFromUri(uri)
                .onSuccess(app -> {
                    if (app != null) {
                        this.apps.put(app.getId(), app);
                        log.debug("[ INST ] 加载应用: {}", app.getName());
                    }
                })
                .onFailure(err -> log.error("[ INST ] 加载应用失败", err))
                .mapEmpty();
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.info("[ INST ] 应用加载完成，共 {} 个", this.apps.size());
            return null;
        });
    }

    /**
     * 从 URI 列表加载菜单数据
     * 返回 Future，支持异步操作
     *
     * 三阶段加载（解决并行扫描时父子菜单顺序问题）：
     * 1. 第一阶段：收集所有 MENU.yml 的 ID 和目录映射，但不计算 level
     * 2. 第二阶段：在所有菜单 ID 已知后，计算每个菜单的 level
     * 3. 第三阶段：加载所有菜单，处理父子关系
     */
    Future<Void> loadMenus(final List<URI> menuDirUris) {
        log.info("[ INST ] 开始加载菜单数据，共 {} 个目录", menuDirUris.size());

        // 第一阶段：收集所有 MENU.yml 的 ID 和目录映射
        log.info("[ INST ] 第一阶段：收集所有 MENU.yml 的 ID 和目录映射");
        for (final URI uri : menuDirUris) {
            try {
                final String dirName = this.extractAppIdFromUri(uri);
                if (dirName == null) {
                    continue;
                }

                String actualAppId;
                if ("HOME".equals(dirName)) {
                    actualAppId = this.globalConfig.getString("appId");
                } else {
                    actualAppId = this.dirNameToAppId.get(dirName);
                    if (actualAppId == null) {
                        continue;
                    }
                }

                // 第一阶段：只收集 ID 和目录映射
                this.collectMenuIds(uri, actualAppId);
            } catch (final Exception e) {
                log.error("[ INST ] 收集菜单 ID 失败", e);
            }
        }
        log.info("[ INST ] 第一阶段完成，共收集 {} 个菜单 ID", this.dirPathToMenuId.size());

        // 第二阶段：计算所有菜单的 level
        log.info("[ INST ] 第二阶段：计算所有菜单的 level");
        for (final URI uri : menuDirUris) {
            try {
                final String dirName = this.extractAppIdFromUri(uri);
                if (dirName == null) {
                    continue;
                }

                String actualAppId;
                if ("HOME".equals(dirName)) {
                    actualAppId = this.globalConfig.getString("appId");
                } else {
                    actualAppId = this.dirNameToAppId.get(dirName);
                    if (actualAppId == null) {
                        continue;
                    }
                }

                // 第二阶段：计算 level
                this.calculateMenuLevels(uri, actualAppId);
            } catch (final Exception e) {
                log.error("[ INST ] 计算菜单 level 失败", e);
            }
        }
        log.info("[ INST ] 第二阶段完成，共计算 {} 个菜单 level", this.menuIdToLevel.size());

        // 第三阶段：正式加载所有菜单
        log.info("[ INST ] 第三阶段：加载所有菜单");
        for (final URI uri : menuDirUris) {
            try {
                final String dirName = this.extractAppIdFromUri(uri);
                if (dirName == null) {
                    log.warn("[ INST ] 无法提取目录名");
                    continue;
                }

                // 从目录名映射到应用UUID
                String actualAppId;
                if ("HOME".equals(dirName)) {
                    // HOME 目录使用全局配置的 appId
                    actualAppId = this.globalConfig.getString("appId");
                } else {
                    // 其他目录从映射表中查找对应的应用UUID
                    actualAppId = this.dirNameToAppId.get(dirName);
                    if (actualAppId == null) {
                        log.warn("[ INST ] 未找到目录 {} 对应的应用UUID，跳过", dirName);
                        continue;
                    }
                }

                // 加载当前模块的菜单
                final List<XMenu> appMenus = this.loadMenusFromDirectory(uri, actualAppId);

                // 合并到已有的菜单列表中（支持跨模块）
                this.menus.computeIfAbsent(actualAppId, k -> new ArrayList<>()).addAll(appMenus);

                log.debug("[ INST ] 加载应用 {} 的菜单: {} 个", actualAppId, appMenus.size());
            } catch (final Exception e) {
                log.error("[ INST ] 加载菜单失败", e);
            }
        }

        // 统计总数
        final int totalMenus = this.menus.values().stream().mapToInt(List::size).sum();
        log.info("[ INST ] 菜单加载完成，共 {} 个应用，{} 个菜单", this.menus.size(), totalMenus);
        return Future.succeededFuture();
    }

    Map<String, XApp> getApps() {
        return new HashMap<>(this.apps);
    }

    Map<String, List<XMenu>> getMenus() {
        return new HashMap<>(this.menus);
    }

    /**
     * 从 URI 加载单个应用
     * 核心逻辑：
     * 1. 先从数据库查询（使用 Z_APP_ID + CODE）
     * 2. 数据库优先，查到则使用数据库的 UUID
     * 3. 查不到才执行纯添加流程（yml id → instance.yml 映射 → 生成新 UUID）
     */
    private Future<XApp> loadAppFromUri(final URI uri) {
        try {
            final String path = uri.getPath();
            final String fileName = Paths.get(path).getFileName().toString();

            if (!fileName.endsWith(".yml")) {
                return Future.succeededFuture(null);
            }

            // 提取目录名（用于建立映射关系）
            final String dirName = this.extractDirNameFromAppUri(uri);

            // 加载 YAML 文件
            final JsonObject data = Ut.ioYaml(path);
            if (data == null || !data.containsKey("data")) {
                log.warn("[ INST ] 应用文件格式错误: {}", path);
                return Future.succeededFuture(null);
            }

            final JsonObject appData = data.getJsonObject("data");
            final String code = appData.getString("code");
            if (code == null || code.isEmpty()) {
                log.warn("[ INST ] 应用缺少 code 字段: {}", path);
                return Future.succeededFuture(null);
            }

            // 获取 Z_APP_ID 环境变量
            final String zAppId = this.globalConfig.getString("appId");
            if (zAppId == null || zAppId.isEmpty()) {
                log.warn("[ INST ] 未配置 Z_APP_ID (globalConfig.appId)");
                return Future.succeededFuture(null);
            }

            // 1. 先从数据库查询（WHERE APP_ID=? AND CODE=?）
            return this.queryAppFromDatabase(zAppId, code)
                .compose(dbApp -> {
                    if (dbApp != null) {
                        // 数据库中存在，使用数据库的 UUID
                        log.info("[ INST ] 从数据库加载应用: {} (UUID={})", code, dbApp.getId());

                        // 使用反序列化创建 XApp 对象，然后用数据库的 ID 覆盖
                        final XApp app = Ut.deserialize(appData, XApp.class);
                        app.setId(dbApp.getId());

                        // 建立目录名到应用UUID的映射（用于菜单加载）
                        if (dirName != null) {
                            this.dirNameToAppId.put(dirName, app.getId());
                            log.debug("[ INST ] 建立映射: 目录 {} -> 应用UUID {}", dirName, app.getId());
                        }

                        // 从 globalConfig 填充公共字段
                        this.fillGlobalFields(app);

                        return Future.succeededFuture(app);
                    } else {
                        // 数据库中不存在，执行纯添加流程
                        log.info("[ INST ] 数据库中未找到应用: {} (APP_ID={})", code, zAppId);

                        // 使用反序列化创建 XApp 对象
                        final XApp app = Ut.deserialize(appData, XApp.class);

                        // ID 策略：
                        // 1. yml 中有 id 则使用
                        // 2. 否则检查 instance.yml 中是否有映射（code -> UUID）
                        // 3. 都没有则生成新 UUID
                        String appId = appData.getString("id");
                        if (appId == null || appId.isEmpty()) {
                            // 检查 instance.yml 映射
                            if (this.instanceMap.containsKey(code)) {
                                appId = this.instanceMap.get(code);
                                log.debug("[ INST ] 应用使用 instance.yml 映射: {} -> {}", code, appId);
                            } else {
                                appId = UUID.randomUUID().toString();
                                log.debug("[ INST ] 应用未指定 id，生成新 UUID: {}", appId);
                            }
                        } else {
                            log.debug("[ INST ] 应用使用 yml 中的 id: {}", appId);
                        }
                        app.setId(appId);

                        // 建立目录名到应用UUID的映射（用于菜单加载）
                        if (dirName != null) {
                            this.dirNameToAppId.put(dirName, appId);
                            log.debug("[ INST ] 建立映射: 目录 {} -> 应用UUID {}", dirName, appId);
                        }

                        // 从 globalConfig 填充公共字段
                        this.fillGlobalFields(app);

                        return Future.succeededFuture(app);
                    }
                });
        } catch (final Exception e) {
            log.error("[ INST ] 加载应用失败", e);
            return Future.failedFuture(e);
        }
    }

    /**
     * 从数据库查询应用（WHERE APP_ID=? AND CODE=?）
     */
    private Future<XApp> queryAppFromDatabase(final String appId, final String code) {
        return DB.on(XAppDao.class)
            .<XApp>fetchAsync("APP_ID", appId)
            .map(apps -> {
                // 过滤出匹配 CODE 的应用
                for (final XApp app : apps) {
                    if (code.equals(app.getCode())) {
                        return app;
                    }
                }
                return null;
            })
            .recover(err -> {
                log.error("[ INST ] 查询数据库失败", err);
                return Future.succeededFuture(null);
            });
    }

    /**
     * 填充全局配置字段（XApp）
     * globalConfig 已经通过 Ut.compileAnsible() 处理过环境变量，直接使用即可
     */
    private void fillGlobalFields(final XApp app) {
        if (this.globalConfig == null) {
            return;
        }

        app.setSigma(this.globalConfig.getString("sigma"));
        app.setAppId(this.globalConfig.getString("appId"));
        app.setLanguage(this.globalConfig.getString("language"));
        app.setActive(this.globalConfig.getBoolean("active", true));
        app.setTenantId(this.globalConfig.getString("tenantId"));

        this.fillAuditFields(app::setCreatedBy, app::setUpdatedBy, app::setCreatedAt, app::setUpdatedAt);
    }

    /**
     * 填充菜单的全局字段（XMenu）
     * globalConfig 已经通过 Ut.compileAnsible() 处理过环境变量，直接使用即可
     */
    private void fillGlobalFieldsForMenu(final XMenu menu) {
        if (this.globalConfig == null) {
            return;
        }

        menu.setSigma(this.globalConfig.getString("sigma"));
        menu.setLanguage(this.globalConfig.getString("language"));
        menu.setActive(this.globalConfig.getBoolean("active", true));
        menu.setTenantId(this.globalConfig.getString("tenantId"));

        this.fillAuditFields(menu::setCreatedBy, menu::setUpdatedBy, menu::setCreatedAt, menu::setUpdatedAt);
    }

    /**
     * 填充审计字段（创建人、更新人、创建时间、更新时间）
     * 时间字段直接使用当前时间，无需判断 R2_NOW()
     */
    private void fillAuditFields(
        final java.util.function.Consumer<String> setCreatedBy,
        final java.util.function.Consumer<String> setUpdatedBy,
        final java.util.function.Consumer<java.time.LocalDateTime> setCreatedAt,
        final java.util.function.Consumer<java.time.LocalDateTime> setUpdatedAt
    ) {
        setCreatedBy.accept(this.globalConfig.getString("createdBy"));
        setUpdatedBy.accept(this.globalConfig.getString("updatedBy"));

        // 时间字段直接使用当前时间
        final java.time.LocalDateTime now = java.time.LocalDateTime.now();
        setCreatedAt.accept(now);
        setUpdatedAt.accept(now);
    }

    /**
     * 从 URI 提取目录名（非 UUID）
     * 例如：apps/desktop/nav → desktop
     */
    private String extractAppIdFromUri(final URI uri) {
        final String path = uri.getPath();
        final String[] parts = path.split("/apps/");
        if (parts.length < 2) {
            return null;
        }

        final String afterApps = parts[1];
        final int slashIndex = afterApps.indexOf('/');
        if (slashIndex > 0) {
            // 返回目录名（可以是任意字符串，不限于 UUID）
            return afterApps.substring(0, slashIndex);
        }

        return afterApps;
    }

    /**
     * 从应用 URI 提取目录名
     * 例如：apps/desktop/desktop.yml → desktop
     */
    private String extractDirNameFromAppUri(final URI uri) {
        final String path = uri.getPath();
        final String[] parts = path.split("/apps/");
        if (parts.length < 2) {
            return null;
        }

        final String afterApps = parts[1];
        final int slashIndex = afterApps.indexOf('/');
        if (slashIndex > 0) {
            return afterApps.substring(0, slashIndex);
        }

        // 如果没有斜杠，说明是 apps/{name}.yml 格式
        final String fileName = afterApps;
        if (fileName.endsWith(".yml")) {
            return fileName.substring(0, fileName.length() - 4);
        }

        return afterApps;
    }

    /**
     * 从目录加载菜单
     */
    private List<XMenu> loadMenusFromDirectory(final URI uri, final String appId) throws Exception {
        final List<XMenu> result = new ArrayList<>();

        // 从 URI 获取 File 对象
        final File appDir = new File(uri.getPath());
        final File navDir = new File(appDir, "nav");

        if (!navDir.exists() || !navDir.isDirectory()) {
            log.debug("[ INST ] nav 目录不存在");
            return result;
        }

        // 递归加载菜单，传递 navDir 作为根目录
        this.loadMenusRecursive(navDir, navDir, appId, null, 1, result);

        return result;
    }

    /**
     * 递归加载菜单
     * @param navRoot nav 目录的根路径，用于计算相对路径
     */
    private void loadMenusRecursive(final File navRoot, final File dir, final String appId, final String parentId,
                                    final int level, final List<XMenu> result) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        // 先处理 MENU.yml (如果存在)
        XMenu currentMenu = null;
        final File menuFile = new File(dir, "MENU.yml");
        if (menuFile.exists()) {
            // 从缓存中获取正确的 level
            final String relativePath = this.getRelativePath(navRoot, dir);
            final String dirKey = appId + ":" + relativePath;
            final String cachedMenuId = this.dirPathToMenuId.get(dirKey);
            final Integer cachedLevel = cachedMenuId != null ? this.menuIdToLevel.get(cachedMenuId) : null;
            final int actualLevel = cachedLevel != null ? cachedLevel : level;

            currentMenu = this.loadMenuFromFile(menuFile, appId, parentId, actualLevel, dir.getName(), dir);
            if (currentMenu != null) {
                result.add(currentMenu);
            }
        }

        // 确定当前目录的父菜单 ID
        String currentParentId = parentId;
        if (currentMenu != null) {
            // 当前目录有 MENU.yml，使用其 ID
            currentParentId = currentMenu.getId();
        } else if (parentId == null) {
            // 当前目录没有 MENU.yml，且传入的 parentId 为 null
            // 尝试从缓存中查找（支持跨模块）
            final String relativePath = this.getRelativePath(navRoot, dir);
            final String dirKey = appId + ":" + relativePath;
            final String cachedId = this.dirPathToMenuId.get(dirKey);
            if (cachedId != null) {
                currentParentId = cachedId;
                log.debug("[ INST ] 跨模块查找父菜单: {} -> {}", relativePath, cachedId);
            }
        }
        // 如果 parentId 不为 null，直接使用传入的值（来自上层递归）

        // 确定子菜单的 level
        // 如果有 currentParentId，从缓存中获取其 level，子菜单 level = 父 level + 1
        // 否则使用传入的 level
        int childLevel = level + 1;
        if (currentParentId != null) {
            final Integer parentLevel = this.menuIdToLevel.get(currentParentId);
            if (parentLevel != null) {
                childLevel = parentLevel + 1;
            }
        }

        // 处理子目录和文件
        for (final File file : files) {
            if (file.getName().equals("MENU.yml")) {
                continue;
            }

            if (file.isDirectory()) {
                // 递归处理子目录
                this.loadMenusRecursive(navRoot, file, appId, currentParentId, childLevel, result);
            } else if (file.getName().endsWith(".yml")) {
                // 处理菜单文件，传递文件所在目录
                final XMenu menu = this.loadMenuFromFile(file, appId, currentParentId, childLevel, file.getName(), dir);
                if (menu != null) {
                    result.add(menu);
                }
            }
        }
    }

    /**
     * 从文件加载单个菜单
     * @param parentDir 菜单文件所在的目录，用于提取 TYPE
     */
    private XMenu loadMenuFromFile(final File file, final String appId, final String parentId,
                                   final int level, final String fileName, final File parentDir) throws Exception {
        final JsonObject data = Ut.ioYaml(file.getAbsolutePath());
        if (data == null || !data.containsKey("data")) {
            log.warn("[ INST ] 菜单文件格式错误: {}", file.getAbsolutePath());
            return null;
        }

        final JsonObject menuData = data.getJsonObject("data");
        final String name = menuData.getString("name");
        if (name == null) {
            log.warn("[ INST ] 菜单缺少 name 字段: {}", file.getAbsolutePath());
            return null;
        }

        // 解析文件名或目录名获取 order 和 text
        final String[] parts = this.parseFileName(fileName);
        final Long order = parts[0] != null ? Long.parseLong(parts[0]) : 0L;
        final String text = parts[1];

        // ID 策略：yml 中有 id 则使用，否则生成新 UUID
        String menuId = menuData.getString("id");
        if (menuId == null || menuId.isEmpty()) {
            // 检查缓存中是否已有此菜单的 UUID（基于 appId:name）
            final String cacheKey = appId + ":" + name;
            menuId = this.menuUuidCache.get(cacheKey);
            if (menuId == null) {
                menuId = UUID.randomUUID().toString();
                this.menuUuidCache.put(cacheKey, menuId);
                log.debug("[ INST ] 菜单未指定 id，生成新 UUID: {} (name={})", menuId, name);
            }
        } else {
            log.debug("[ INST ] 菜单使用 yml 中的 id: {} (name={})", menuId, name);
        }

        // 使用反序列化创建 XMenu 对象
        final XMenu menu = Ut.deserialize(menuData, XMenu.class);
        menu.setId(menuId);
        menu.setText(text);
        menu.setOrder(order);
        menu.setLevel((long) level);
        menu.setParentId(parentId);
        menu.setAppId(appId);

        // 设置菜单类型：只看菜单所在目录
        if (menu.getType() == null || menu.getType().isEmpty()) {
            final String dirName = parentDir.getName();
            final String menuType = this.extractTypeFromDirName(dirName);
            menu.setType(menuType);
        }

        // 填充全局字段
        this.fillGlobalFieldsForMenu(menu);

        return menu;
    }

    /**
     * 解析文件名或目录名
     * 格式: {order}@{text} 或 {order}_{text}
     */
    private String[] parseFileName(final String fileName) {
        String name = fileName;
        if (name.endsWith(".yml")) {
            name = name.substring(0, name.length() - 4);
        }

        String order = null;
        String text = name;

        // 尝试 @ 分隔符
        int sepIndex = name.indexOf('@');
        if (sepIndex < 0) {
            // 尝试 _ 分隔符
            sepIndex = name.indexOf('_');
        }

        if (sepIndex > 0) {
            order = name.substring(0, sepIndex);
            text = name.substring(sepIndex + 1);
        }

        return new String[]{order, text};
    }

    /**
     * 从目录名提取 TYPE
     * 格式: TYPE@XXX，返回 XXX
     * 否则返回默认类型 SIDE-MENU
     */
    private String extractTypeFromDirName(final String dirName) {
        if (dirName.startsWith("TYPE@")) {
            return dirName.substring(5); // 去掉 "TYPE@" 前缀
        }
        return "SIDE-MENU"; // 默认类型
    }

    /**
     * 获取相对于 nav 根目录的相对路径
     * 例如：nav/10000@系统管理/1000@用户管理 → 10000@系统管理/1000@用户管理
     */
    private String getRelativePath(final File navRoot, final File dir) {
        final String navPath = navRoot.getAbsolutePath();
        final String dirPath = dir.getAbsolutePath();
        if (dirPath.startsWith(navPath)) {
            String relative = dirPath.substring(navPath.length());
            // 移除开头的分隔符
            if (relative.startsWith("/") || relative.startsWith("\\")) {
                relative = relative.substring(1);
            }
            return relative;
        }
        return dir.getName(); // 后备方案
    }

    /**
     * 第一阶段：收集所有 MENU.yml 的 ID 和目录映射
     * 只收集 ID，不计算 level（因为父菜单可能还未扫描）
     */
    private void collectMenuIds(final URI uri, final String appId) throws Exception {
        final File appDir = new File(uri.getPath());
        final File navDir = new File(appDir, "nav");

        if (!navDir.exists() || !navDir.isDirectory()) {
            return;
        }

        // 递归收集所有 MENU.yml 的 ID
        this.collectMenuIdsRecursive(navDir, navDir, appId);
    }

    /**
     * 递归收集 MENU.yml 的 ID 和目录映射
     */
    private void collectMenuIdsRecursive(final File navRoot, final File dir, final String appId) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        // 检查当前目录是否有 MENU.yml
        final File menuFile = new File(dir, "MENU.yml");
        if (menuFile.exists()) {
            // 读取 MENU.yml 获取 name 和 id
            final JsonObject data = Ut.ioYaml(menuFile.getAbsolutePath());
            if (data != null && data.containsKey("data")) {
                final JsonObject menuData = data.getJsonObject("data");
                final String name = menuData.getString("name");
                if (name != null) {
                    // 生成或获取菜单 ID
                    String menuId = menuData.getString("id");
                    if (menuId == null || menuId.isEmpty()) {
                        final String cacheKey = appId + ":" + name;
                        menuId = this.menuUuidCache.get(cacheKey);
                        if (menuId == null) {
                            menuId = UUID.randomUUID().toString();
                            this.menuUuidCache.put(cacheKey, menuId);
                        }
                    }

                    // 建立目录路径到菜单ID的映射
                    final String relativePath = this.getRelativePath(navRoot, dir);
                    final String dirKey = appId + ":" + relativePath;
                    this.dirPathToMenuId.put(dirKey, menuId);
                    log.debug("[ INST ] 收集菜单 ID: {} -> {} (name={})", relativePath, menuId, name);
                }
            }
        }

        // 递归处理子目录
        for (final File file : files) {
            if (file.isDirectory()) {
                this.collectMenuIdsRecursive(navRoot, file, appId);
            }
        }
    }

    /**
     * 第二阶段：计算所有菜单的 level
     * 此时所有菜单 ID 已知，可以正确计算跨模块的层级关系
     */
    private void calculateMenuLevels(final URI uri, final String appId) throws Exception {
        final File appDir = new File(uri.getPath());
        final File navDir = new File(appDir, "nav");

        if (!navDir.exists() || !navDir.isDirectory()) {
            return;
        }

        // 递归计算所有菜单的 level
        this.calculateMenuLevelsRecursive(navDir, navDir, appId, null);
    }

    /**
     * 递归计算菜单的 level
     */
    private void calculateMenuLevelsRecursive(final File navRoot, final File dir, final String appId,
                                              final String parentId) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        // 先从缓存中获取当前目录的菜单 ID
        final String relativePath = this.getRelativePath(navRoot, dir);
        final String dirKey = appId + ":" + relativePath;
        String currentMenuId = this.dirPathToMenuId.get(dirKey);

        // 如果当前目录有菜单，计算其 level
        if (currentMenuId != null && !this.menuIdToLevel.containsKey(currentMenuId)) {
            int level;
            if (parentId == null) {
                // 顶级菜单，level = 1
                level = 1;
            } else {
                // 子菜单，level = 父菜单 level + 1
                final Integer parentLevel = this.menuIdToLevel.get(parentId);
                if (parentLevel != null) {
                    level = parentLevel + 1;
                } else {
                    // 父菜单的 level 还未计算，递归计算父菜单
                    log.warn("[ INST ] 父菜单 {} 的 level 未计算，使用默认值", parentId);
                    level = 1;
                }
            }
            this.menuIdToLevel.put(currentMenuId, level);
            log.debug("[ INST ] 计算菜单 level: {} -> level={}", currentMenuId, level);
        }

        // 确定传递给子目录的 parentId
        final String childParentId = currentMenuId != null ? currentMenuId : parentId;

        // 递归处理子目录
        for (final File file : files) {
            if (file.isDirectory()) {
                this.calculateMenuLevelsRecursive(navRoot, file, appId, childParentId);
            }
        }
    }

    /**
     * 预扫描菜单目录，只收集 MENU.yml 的路径映射
     * 用于建立全局的目录路径到菜单ID的缓存（跨模块）
     * @deprecated 已被 collectMenuIds 和 calculateMenuLevels 替代
     */
    @Deprecated
    private void preScanMenuDirectories(final URI uri, final String appId) throws Exception {
        final File appDir = new File(uri.getPath());
        final File navDir = new File(appDir, "nav");

        if (!navDir.exists() || !navDir.isDirectory()) {
            return;
        }

        // 递归扫描所有 MENU.yml，从 level=1 开始
        this.preScanRecursive(navDir, navDir, appId, null, 1);
    }

    /**
     * 递归扫描目录，只处理 MENU.yml
     * 同时计算并缓存每个菜单的 level
     * @deprecated 已被 collectMenuIdsRecursive 和 calculateMenuLevelsRecursive 替代
     */
    @Deprecated
    private void preScanRecursive(final File navRoot, final File dir, final String appId,
                                   final String parentId, final int level) throws Exception {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        // 先检查缓存中是否已有此目录的菜单（跨模块）
        final String relativePath = this.getRelativePath(navRoot, dir);
        final String dirKey = appId + ":" + relativePath;
        String currentMenuId = this.dirPathToMenuId.get(dirKey);
        Integer currentLevel = currentMenuId != null ? this.menuIdToLevel.get(currentMenuId) : null;

        // 检查当前目录是否有 MENU.yml
        final File menuFile = new File(dir, "MENU.yml");
        if (menuFile.exists()) {
            // 读取 MENU.yml 获取 name 和 id
            final JsonObject data = Ut.ioYaml(menuFile.getAbsolutePath());
            if (data != null && data.containsKey("data")) {
                final JsonObject menuData = data.getJsonObject("data");
                final String name = menuData.getString("name");
                if (name != null) {
                    // 生成或获取菜单 ID
                    String menuId = menuData.getString("id");
                    if (menuId == null || menuId.isEmpty()) {
                        final String cacheKey = appId + ":" + name;
                        menuId = this.menuUuidCache.get(cacheKey);
                        if (menuId == null) {
                            menuId = UUID.randomUUID().toString();
                            this.menuUuidCache.put(cacheKey, menuId);
                        }
                    }

                    // 计算当前菜单的 level
                    int calculatedLevel;
                    if (parentId == null) {
                        // 顶级菜单，level = 1
                        calculatedLevel = 1;
                    } else {
                        // 子菜单，level = 父菜单 level + 1
                        final Integer parentLevel = this.menuIdToLevel.get(parentId);
                        calculatedLevel = (parentLevel != null ? parentLevel : 0) + 1;
                    }

                    // 建立目录路径到菜单ID的映射
                    this.dirPathToMenuId.put(dirKey, menuId);
                    this.menuIdToLevel.put(menuId, calculatedLevel);

                    currentMenuId = menuId;
                    currentLevel = calculatedLevel;
                    log.debug("[ INST ] 预扫描缓存: {} -> {} (name={}, level={})", relativePath, menuId, name, calculatedLevel);
                }
            }
        }

        // 确定传递给子目录的 parentId 和 level
        String childParentId = parentId;
        int childLevel = level;

        if (currentMenuId != null && currentLevel != null) {
            // 当前目录有菜单（可能来自缓存或当前模块）
            childParentId = currentMenuId;
            childLevel = currentLevel + 1;
        }

        // 递归处理子目录
        for (final File file : files) {
            if (file.isDirectory()) {
                this.preScanRecursive(navRoot, file, appId, childParentId, childLevel);
            }
        }
    }
}

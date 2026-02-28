package io.zerows.boot.inst;

import io.vertx.core.json.JsonObject;
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

    private final Map<String, XApp> apps = new ConcurrentHashMap<>();
    private final Map<String, List<XMenu>> menus = new ConcurrentHashMap<>();
    private final Map<String, String> menuUuidCache = new ConcurrentHashMap<>();
    private final Map<String, String> dirPathToMenuId = new ConcurrentHashMap<>(); // 目录路径 -> 菜单ID
    private final JsonObject globalConfig;

    private BuildMenuLoader(final JsonObject globalConfig) {
        this.globalConfig = globalConfig;
    }

    static BuildMenuLoader create(final JsonObject globalConfig) {
        return new BuildMenuLoader(globalConfig);
    }

    /**
     * 从 URI 列表加载应用数据
     */
    void loadApps(final List<URI> appUris) {
        log.info("[ INST ] 开始加载应用数据，共 {} 个文件", appUris.size());

        for (final URI uri : appUris) {
            try {
                final XApp app = this.loadAppFromUri(uri);
                if (app != null) {
                    this.apps.put(app.getId(), app);
                    log.debug("[ INST ] 加载应用: {}", app.getName());
                }
            } catch (final Exception e) {
                log.error("[ INST ] 加载应用失败", e);
            }
        }

        log.info("[ INST ] 应用加载完成，共 {} 个", this.apps.size());
    }

    /**
     * 从 URI 列表加载菜单数据
     */
    void loadMenus(final List<URI> menuDirUris) {
        log.info("[ INST ] 开始加载菜单数据，共 {} 个目录", menuDirUris.size());

        for (final URI uri : menuDirUris) {
            try {
                final String appId = this.extractAppIdFromUri(uri);
                if (appId == null) {
                    log.warn("[ INST ] 无法提取 appId");
                    continue;
                }

                // HOME 目录使用全局配置的 appId，其他使用目录名
                final String actualAppId = "HOME".equals(appId)
                    ? this.globalConfig.getString("appId")
                    : appId;

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
    }

    Map<String, XApp> getApps() {
        return new HashMap<>(this.apps);
    }

    Map<String, List<XMenu>> getMenus() {
        return new HashMap<>(this.menus);
    }

    /**
     * 从 URI 加载单个应用
     */
    private XApp loadAppFromUri(final URI uri) throws Exception {
        final String path = uri.getPath();
        final String fileName = Paths.get(path).getFileName().toString();

        if (!fileName.endsWith(".yml")) {
            return null;
        }

        // 提取 UUID (文件名去掉 .yml 后缀)
        final String appId = fileName.substring(0, fileName.length() - 4);

        // 加载 YAML 文件
        final JsonObject data = Ut.ioYaml(path);
        if (data == null || !data.containsKey("data")) {
            log.warn("[ INST ] 应用文件格式错误");
            return null;
        }

        final JsonObject appData = data.getJsonObject("data");

        // 使用反序列化创建 XApp 对象
        final XApp app = Ut.deserialize(appData, XApp.class);
        app.setId(appId);

        // 从 globalConfig 填充公共字段
        this.fillGlobalFields(app);

        return app;
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
     * 从 URI 提取 appId
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
            return afterApps.substring(0, slashIndex);
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
            currentMenu = this.loadMenuFromFile(menuFile, appId, parentId, level, dir.getName(), dir);
            if (currentMenu != null) {
                result.add(currentMenu);
                // 缓存相对路径到菜单 ID 的映射（用于跨模块查找）
                final String relativePath = this.getRelativePath(navRoot, dir);
                final String dirKey = appId + ":" + relativePath;
                this.dirPathToMenuId.put(dirKey, currentMenu.getId());
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
        // 如果当前目录有 MENU.yml，子菜单 level = 当前 level + 1
        // 如果当前目录没有 MENU.yml，子菜单 level = 当前 level（不增加层级）
        final int childLevel = currentMenu != null ? level + 1 : level;

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
            log.warn("[ INST ] 菜单文件格式错误");
            return null;
        }

        final JsonObject menuData = data.getJsonObject("data");
        final String name = menuData.getString("name");
        if (name == null) {
            log.warn("[ INST ] 菜单缺少 name 字段");
            return null;
        }

        // 解析文件名或目录名获取 order 和 text
        final String[] parts = this.parseFileName(fileName);
        final Long order = parts[0] != null ? Long.parseLong(parts[0]) : 0L;
        final String text = parts[1];

        // 检查缓存中是否已有此菜单的 UUID
        final String cacheKey = appId + ":" + name;
        String menuId = this.menuUuidCache.get(cacheKey);
        if (menuId == null) {
            menuId = UUID.randomUUID().toString();
            this.menuUuidCache.put(cacheKey, menuId);
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
}

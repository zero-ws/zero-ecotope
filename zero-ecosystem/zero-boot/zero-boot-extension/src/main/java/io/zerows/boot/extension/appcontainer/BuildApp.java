package io.zerows.boot.extension.appcontainer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XMenu;
import io.zerows.platform.apps.KArk;
import io.zerows.platform.apps.KPivot;
import io.zerows.platform.management.StoreApp;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用和菜单构建器
 * 负责加载和持久化应用和菜单数据
 */
@Slf4j
@SuppressWarnings("all")
public class BuildApp {

    /**
     * 执行应用和菜单数据的加载与持久化
     * <p>
     * 核心流程：
     * 1. 加载全局配置（environment.json 的 global 节点）
     * 2. 扫描所有模块的 apps 目录（兼容旧版 flat 结构与新版 nested 结构）
     * 3. 使用 BuildAppMenuLoader 加载应用和菜单数据
     * 4. 使用 BuildAppMenuPersister 持久化到数据库（XApp 按 ID 判重，XMenu 按 NAME+APP_ID 判重）
     * 5. 生成缓存文件（优先 apps/{tenant-id}/{instance-app-id}/{child-app-id}/menu.yml，
     *    兼容旧版 apps/{instance-app-id}/{child-app-id}/menu.yml）
     * 6. 输出统计信息
     *
     * @param vertx Vert.x 实例
     * @return Future<Boolean> 成功返回 true，失败返回 false
     */
    public static Future<Boolean> run(final Vertx vertx) {
        log.info("[ INST ] ========================================");
        log.info("[ INST ] 开始加载应用和菜单...");
        log.info("[ INST ] ========================================");

        try {
            // 1. 加载全局配置（从 environment.json 的 global 节点）
            final JsonObject globalConfig = BuildShared.loadGlobalConfig();

            // 2. 解析 apps 根目录和当前实例缓存目录
            final String appsRoot = resolveAppsRoot();
            final String cacheDir = resolveCacheDir(globalConfig);

            // 3. 加载实例映射配置（从 apps/instance.yml 和缓存标识文件）
            final Map<String, String> instanceMap = loadInstanceMap(appsRoot, cacheDir);

            // 3.1 加载 init 配置（优先当前实例目录下的 instance.yml）
            final JsonObject initConfig = loadInitConfig(appsRoot, cacheDir);

            // 4. 从缓存目录和数据库加载额外的映射
            return loadCacheMappings(vertx, appsRoot, cacheDir, instanceMap)
                .compose(mergedMap -> {
                    // 5. 扫描应用和菜单目录
                    final List<URI> appUris = InstApps.of().ioApp();        // apps/{child-app-id}.yml
                    final List<URI> menuDirUris = InstApps.of().ioRunning(); // apps/{child-app-id}/nav/

                    log.info("[ INST ] 扫描到 {} 个应用文件，{} 个菜单目录", appUris.size(), menuDirUris.size());

                    // 6. 加载应用和菜单数据
                    final BuildMenuLoader loader = BuildMenuLoader.create(vertx, globalConfig, mergedMap, initConfig);
                    return loader.loadApps(appUris)
                        .compose(v -> loader.preloadDirectoryMappings())
                        .compose(v -> loader.loadMenus(menuDirUris))
                        .compose(v -> {

                            final Map<String, XApp> apps = loader.getApps();
                            final Map<String, List<XMenu>> menus = loader.getMenus();

                            if (apps.isEmpty() && menus.isEmpty()) {
                                log.warn("[ INST ] 未加载到任何应用或菜单数据");
                                return Future.succeededFuture(false);
                            }

                            // 7. 创建缓存目录（优先 apps/{tenant-id}/{instance-app-id}）
                            final File cacheDirFile = new File(cacheDir);
                            if (!cacheDirFile.exists()) {
                                final boolean created = cacheDirFile.mkdirs();
                                log.info("[ INST ] 创建缓存目录: {}", cacheDir);
                            } else {
                                log.info("[ INST ] 使用缓存目录: {}", cacheDir);
                            }

                            // 8. 持久化到数据库（XApp 按 ID 判重，XMenu 按 NAME+APP_ID 判重）
                            final BuildMenuPersister persister = BuildMenuPersister.create(vertx, appsRoot, cacheDir);

                            return persister.saveApps(apps)
                                .compose(appStats -> persister.saveMenus(menus, loader.getAppDirectoryMap())
                                    .map(menuStats -> {
                                        final int totalMenus = menus.values().stream().mapToInt(List::size).sum();
                                        log.info("[ INST ] ========================================");
                                        log.info("[ INST ] 导入统计:");
                                        log.info("[ INST ]   应用: 加载 {} / 新增 {} / 更新 {}", apps.size(), appStats[0], appStats[1]);
                                        log.info("[ INST ]   菜单: 加载 {} / 新增 {} / 更新 {}", totalMenus, menuStats[0], menuStats[1]);
                                        log.info("[ INST ]   缓存: {}", cacheDir);
                                        log.info("[ INST ]   实例: {}/instance.yml", cacheDir);
                                        log.info("[ INST ] ========================================");
                                        // Fix：解决菜单导入过程中后续应用部合理的问题（只能在此处加载）
                                        final HApp app = StoreApp.of().value();
                                        final HAmbient ambient = KPivot.of(vertx).running();
                                        final HArk ark = KArk.of(app);
                                        ambient.registry(ark);
                                        return true;
                                    })
                                )
                                .recover(err -> {
                                    log.error("[ INST ] 导入失败", err);
                                    return Future.succeededFuture(false);
                                });
                        })
                        .recover(err -> {
                            log.error("[ INST ] 加载缓存映射失败", err);
                            return Future.succeededFuture(false);
                        });
                })
                .recover(err -> {
                    log.error("[ INST ] 加载缓存映射失败", err);
                    return Future.succeededFuture(false);
                });
        } catch (final Exception e) {
            log.error("[ INST ] 加载失败", e);
            return Future.failedFuture(e);
        }
    }

    /**
     * 加载实例映射配置
     * 从 instance.yml 加载 code 到 UUID 的映射
     * 格式: running: { UUID: code }
     * 返回: Map<code, UUID>
     */
    private static Map<String, String> loadInstanceMap(final String appsRoot, final String cacheDir) {
        try {
            for (final File instanceFile : resolveInstanceFiles(appsRoot, cacheDir)) {
                if (!instanceFile.exists()) {
                    continue;
                }
                final JsonObject instanceJ = Ut.ioYaml(instanceFile.getAbsolutePath());
                if (instanceJ == null || !instanceJ.containsKey("running")) {
                    log.warn("[ INST ] instance.yml 格式错误，缺少 running 节点: {}", instanceFile.getAbsolutePath());
                    continue;
                }

                final JsonObject running = instanceJ.getJsonObject("running");
                final Map<String, String> instanceMap = new HashMap<>();

                for (final String uuid : running.fieldNames()) {
                    final String code = running.getString(uuid);
                    if (code != null && !code.isEmpty()) {
                        instanceMap.put(code, uuid);
                        log.debug("[ INST ] 加载实例映射: {} -> {}", code, uuid);
                    }
                }
                log.info("[ INST ] 从 {} 加载实例映射: {} 条记录", instanceFile.getAbsolutePath(), instanceMap.size());
                return instanceMap;
            }

            final Map<String, String> classpathMap = InstApps.of().ioInstance();
            if (!classpathMap.isEmpty()) {
                return classpathMap;
            }

            log.debug("[ INST ] 未找到可用的 instance.yml，使用空映射");
            return new HashMap<>();
        } catch (final Exception e) {
            log.error("[ INST ] 加载实例映射失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 从 instance.yml 加载 init 配置
     * 格式: init: { menu: {...}, app: {...} }
     * 返回: JsonObject
     */
    private static JsonObject loadInitConfig(final String appsRoot, final String cacheDir) {
        try {
            for (final File instanceFile : resolveInstanceFiles(appsRoot, cacheDir)) {
                if (!instanceFile.exists()) {
                    continue;
                }
                final JsonObject instanceJ = Ut.ioYaml(instanceFile.getAbsolutePath());
                if (instanceJ == null || !instanceJ.containsKey("init")) {
                    log.debug("[ INST ] instance.yml 中未找到 init 节点: {}", instanceFile.getAbsolutePath());
                    continue;
                }
                final JsonObject init = instanceJ.getJsonObject("init");
                log.info("[ INST ] 从 {} 加载 init 配置完成", instanceFile.getAbsolutePath());
                return init;
            }

            final JsonObject classpathInit = InstApps.of().ioInit();
            if (classpathInit != null && !classpathInit.isEmpty()) {
                return classpathInit;
            }

            log.debug("[ INST ] 未找到可用的 init 配置，使用空配置");
            return new JsonObject();
        } catch (final Exception e) {
            log.error("[ INST ] 加载 init 配置失败", e);
            return new JsonObject();
        }
    }

    /**
     * 从缓存目录和数据库加载额外的映射
     * 扫描 apps/{UUID}/{code} 标识文件，与数据库中的应用进行匹配
     * 返回合并后的 code -> UUID 映射
     */
    private static Future<Map<String, String>> loadCacheMappings(
        final Vertx vertx,
        final String appsRoot,
        final String cacheDir,
        final Map<String, String> instanceMap
    ) {
        try {
            final Map<String, String> cacheMap = new HashMap<>();
            final int[] cacheStats = thisLoadCacheMappings(new File(appsRoot), cacheMap);

            // 合并映射：instance.yml 优先级更高
            final Map<String, String> mergedMap = new HashMap<>(cacheMap);
            mergedMap.putAll(instanceMap);

            final int currentMarkers = thisLoadDirectMarkers(new File(cacheDir), cacheDir == null ? null : new File(cacheDir).getName(), new HashMap<>());
            log.info("[ INST ] 加载缓存映射: {} 条记录（instance.yml: {}, legacy: {}, nested: {}, current: {}）",
                mergedMap.size(), instanceMap.size(), cacheStats[0], cacheStats[1], currentMarkers);

            return Future.succeededFuture(mergedMap);
        } catch (final Exception e) {
            log.error("[ INST ] 加载缓存映射失败", e);
            return Future.succeededFuture(instanceMap);
        }
    }

    /**
     * 扫描缓存标识文件，兼容旧版 flat 结构和新版 nested 结构
     */
    private static int[] thisLoadCacheMappings(final File baseDir, final Map<String, String> cacheMap) {
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return new int[]{0, 0};
        }

        int legacyCount = 0;
        int nestedCount = 0;
        final File[] firstLevelDirs = baseDir.listFiles(File::isDirectory);
        if (firstLevelDirs == null || firstLevelDirs.length == 0) {
            return new int[]{0, 0};
        }

        for (final File firstLevelDir : firstLevelDirs) {
            legacyCount += thisLoadDirectMarkers(firstLevelDir, firstLevelDir.getName(), cacheMap);
            final File[] secondLevelDirs = firstLevelDir.listFiles(File::isDirectory);
            if (secondLevelDirs == null) {
                continue;
            }
            for (final File secondLevelDir : secondLevelDirs) {
                nestedCount += thisLoadDirectMarkers(secondLevelDir, secondLevelDir.getName(), cacheMap);
            }
        }
        return new int[]{legacyCount, nestedCount};
    }

    private static int thisLoadDirectMarkers(final File runtimeDir, final String runtimeAppId,
                                             final Map<String, String> cacheMap) {
        if (runtimeDir == null || !runtimeDir.exists() || !runtimeDir.isDirectory()
            || runtimeAppId == null || runtimeAppId.isEmpty()) {
            return 0;
        }

        int markerCount = 0;
        final File[] files = runtimeDir.listFiles(File::isFile);
        if (files == null) {
            return 0;
        }
        for (final File file : files) {
            if (isMarkerFile(file)) {
                final String code = file.getName();
                if (!cacheMap.containsKey(code)) {
                    cacheMap.put(code, runtimeAppId);
                    log.debug("[ INST ] 从缓存加载映射: {} -> {}", code, runtimeAppId);
                }
                markerCount++;
            }
        }
        return markerCount;
    }

    private static boolean isMarkerFile(final File file) {
        final String name = file.getName();
        if (name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".env")) {
            return false;
        }
        if ("instance".equalsIgnoreCase(name) || "Dockerfile".equalsIgnoreCase(name)) {
            return false;
        }
        return 0L == file.length();
    }

    /**
     * 解析 apps 根目录
     * 优先使用 R2MO_HOME 环境变量，未配置则使用当前目录
     */
    private static String resolveAppsRoot() {
        final String r2moHome = System.getenv("R2MO_HOME");
        if (r2moHome != null && !r2moHome.trim().isEmpty()) {
            return r2moHome + "/apps";
        } else {
            return System.getProperty("user.dir") + "/apps";
        }
    }

    /**
     * 解析当前入口应用实例的缓存目录
     * 目录形态：
     * 1. 新版优先：apps/{tenant-id}/{instance-app-id}
     * 2. 兼容旧版：apps/{instance-app-id}
     */
    private static String resolveCacheDir(final JsonObject globalConfig) {
        final String appsRoot = resolveAppsRoot();
        final String appId = globalConfig == null ? null : globalConfig.getString("appId");
        if (appId == null || appId.trim().isEmpty()) {
            return appsRoot;
        }
        final String tenantId = globalConfig.getString("tenantId");
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            return appsRoot + "/" + tenantId.trim() + "/" + appId.trim();
        }
        return appsRoot + "/" + appId.trim();
    }

    private static List<File> resolveInstanceFiles(final String appsRoot, final String cacheDir) {
        final List<File> candidates = new ArrayList<>();
        if (cacheDir != null && !cacheDir.trim().isEmpty()) {
            candidates.add(new File(cacheDir, "instance.yml"));
        }
        if (appsRoot != null && !appsRoot.trim().isEmpty()) {
            final File rootInstance = new File(appsRoot, "instance.yml");
            final boolean duplicated = candidates.stream()
                .anyMatch(file -> file.getAbsolutePath().equals(rootInstance.getAbsolutePath()));
            if (!duplicated) {
                candidates.add(rootInstance);
            }
        }
        return candidates;
    }
}

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
     * 5. 生成缓存文件（apps/{Z_APP_ID}/{child-app-id}/menu.yml）
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
            final Map<String, String> instanceMap = loadInstanceMap(appsRoot);

            // 3.1 加载 init 配置（从 apps/instance.yml 的 init 节点）
            final JsonObject initConfig = loadInitConfig(appsRoot);

            // 4. 从缓存目录和数据库加载额外的映射
            return loadCacheMappings(vertx, appsRoot, cacheDir, instanceMap)
                .compose(mergedMap -> {
                    // 5. 扫描应用和菜单目录
                    final List<URI> appUris = InstApps.of().ioApp();        // apps/{UUID}.yml
                    final List<URI> menuDirUris = InstApps.of().ioRunning(); // apps/{UUID}/nav/

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

                            // 7. 创建缓存目录（apps/{UUID}/menu.yml）
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
     * 从 apps/instance.yml 加载 code 到 UUID 的映射
     * 格式: running: { UUID: code }
     * 返回: Map<code, UUID>
     */
    private static Map<String, String> loadInstanceMap(final String appsRoot) {
        try {
            // 优先从 classpath 加载
            final Map<String, String> classpathMap = InstApps.of().ioInstance();
            if (!classpathMap.isEmpty()) {
                return classpathMap;
            }

            // 如果 classpath 中没有，尝试从文件系统加载
            final File instanceFile = new File(appsRoot + "/instance.yml");
            if (!instanceFile.exists()) {
                log.debug("[ INST ] 未找到 instance.yml，使用空映射");
                return new java.util.HashMap<>();
            }

            final JsonObject instanceJ = Ut.ioYaml(instanceFile.getAbsolutePath());
            if (instanceJ == null || !instanceJ.containsKey("running")) {
                log.warn("[ INST ] instance.yml 格式错误，缺少 running 节点");
                return new java.util.HashMap<>();
            }

            final JsonObject running = instanceJ.getJsonObject("running");
            final Map<String, String> instanceMap = new java.util.HashMap<>();

            // 遍历 running 节点，格式：UUID=code，构造 code -> UUID 映射
            for (final String uuid : running.fieldNames()) {
                final String code = running.getString(uuid);
                if (code != null && !code.isEmpty()) {
                    instanceMap.put(code, uuid);
                    log.debug("[ INST ] 加载实例映射: {} -> {}", code, uuid);
                }
            }

            log.info("[ INST ] 加载实例映射: {} 条记录", instanceMap.size());
            return instanceMap;
        } catch (final Exception e) {
            log.error("[ INST ] 加载实例映射失败", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * 从 apps/instance.yml 加载 init 配置
     * 格式: init: { menu: {...}, app: {...} }
     * 返回: JsonObject
     */
    private static JsonObject loadInitConfig(final String appsRoot) {
        try {
            // 优先从 classpath 加载
            final JsonObject classpathInit = InstApps.of().ioInit();
            if (classpathInit != null && !classpathInit.isEmpty()) {
                return classpathInit;
            }

            // 如果 classpath 中没有，尝试从文件系统加载
            final File instanceFile = new File(appsRoot + "/instance.yml");
            if (!instanceFile.exists()) {
                log.debug("[ INST ] 未找到 instance.yml，使用空 init 配置");
                return new JsonObject();
            }

            final JsonObject instanceJ = Ut.ioYaml(instanceFile.getAbsolutePath());
            if (instanceJ == null || !instanceJ.containsKey("init")) {
                log.debug("[ INST ] instance.yml 中未找到 init 节点");
                return new JsonObject();
            }

            final JsonObject init = instanceJ.getJsonObject("init");
            log.info("[ INST ] 加载 init 配置完成");
            return init;
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
            final Map<String, String> cacheMap = new java.util.HashMap<>();
            final Map<String, String> nestedCacheMap = new java.util.HashMap<>();

            thisLoadCacheMappings(new File(appsRoot), cacheMap);
            if (!appsRoot.equals(cacheDir)) {
                thisLoadCacheMappings(new File(cacheDir), nestedCacheMap);
                cacheMap.putAll(nestedCacheMap);
            }

            // 合并映射：instance.yml 优先级更高
            final Map<String, String> mergedMap = new java.util.HashMap<>(cacheMap);
            mergedMap.putAll(instanceMap);

            log.info("[ INST ] 加载缓存映射: {} 条记录（instance.yml: {}, legacy: {}, nested: {}）",
                mergedMap.size(), instanceMap.size(), cacheMap.size() - nestedCacheMap.size(), nestedCacheMap.size());

            return Future.succeededFuture(mergedMap);
        } catch (final Exception e) {
            log.error("[ INST ] 加载缓存映射失败", e);
            return Future.succeededFuture(instanceMap);
        }
    }

    /**
     * 扫描缓存标识文件，兼容旧版 flat 结构和新版 nested 结构
     */
    private static void thisLoadCacheMappings(final File baseDir, final Map<String, String> cacheMap) {
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return;
        }

        final File[] uuidDirs = baseDir.listFiles(File::isDirectory);
        if (uuidDirs == null || uuidDirs.length == 0) {
            return;
        }

        for (final File uuidDir : uuidDirs) {
            final String uuid = uuidDir.getName();
            final File[] files = uuidDir.listFiles(File::isFile);
            if (files == null) {
                continue;
            }
            for (final File file : files) {
                final String code = file.getName();
                if (!code.endsWith(".yml") && !code.endsWith(".yaml")) {
                    cacheMap.put(code, uuid);
                    log.debug("[ INST ] 从缓存加载映射: {} -> {}", code, uuid);
                }
            }
        }
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
     * 目录形态：apps/{Z_APP_ID}
     */
    private static String resolveCacheDir(final JsonObject globalConfig) {
        final String appsRoot = resolveAppsRoot();
        final String appId = globalConfig == null ? null : globalConfig.getString("appId");
        if (appId == null || appId.trim().isEmpty()) {
            return appsRoot;
        }
        return appsRoot + "/" + appId;
    }
}

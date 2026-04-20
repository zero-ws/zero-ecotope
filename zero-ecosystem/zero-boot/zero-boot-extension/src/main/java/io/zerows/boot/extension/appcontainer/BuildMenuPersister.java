package io.zerows.boot.extension.appcontainer;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.daos.XMenuDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XMenu;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 应用和菜单数据库持久化器
 * 负责将 XApp 和 XMenu 数据保存到数据库
 */
@Slf4j
@SuppressWarnings("all")
class BuildMenuPersister {

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    private final Vertx vertx;
    private final String appsRoot;
    private final String cacheDir;

    private BuildMenuPersister(final Vertx vertx, final String appsRoot, final String cacheDir) {
        this.vertx = vertx;
        this.appsRoot = appsRoot;
        this.cacheDir = cacheDir;
    }

    static BuildMenuPersister create(final Vertx vertx, final String appsRoot, final String cacheDir) {
        return new BuildMenuPersister(vertx, appsRoot, cacheDir);
    }

    /**
     * 保存应用数据到数据库
     * 返回 [新增数, 更新数]
     */
    Future<int[]> saveApps(final Map<String, XApp> apps) {
        log.info("[ INST ] 开始保存应用，共 {} 个", apps.size());

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
        final List<Future<String>> futures = new ArrayList<>();

        for (final XApp app : apps.values()) {
            final Future<String> future = this.upsertApp(app)
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
            final int inserted = insertCount.get();
            final int updated = updateCount.get();
            log.info("[ INST ] 应用保存完成: 新增 {} / 更新 {}", inserted, updated);

            // 生成 instance.yml
            this.generateInstanceYml(apps);

            // 生成缓存标识文件 apps/{Z_APP_ID}/{child-app-id}
            this.generateCacheMarkers(apps);

            return new int[]{inserted, updated};
        });
    }

    /**
     * 保存菜单数据到数据库
     * 返回 [新增数, 更新数]
     * <p>
     * 核心逻辑（支持反复导入）：
     * 1. 第一轮：查询数据库，建立 NAME+APP_ID -> 数据库ID 的映射
     * 2. 第二轮：确定所有菜单的最终 ID（数据库已有则用数据库 ID，否则用新生成的 ID）
     * 3. 第三轮：修正所有菜单的 PARENT_ID（使用第二轮建立的映射）
     * 4. 第四轮：执行 upsert 操作
     */
    Future<int[]> saveMenus(final Map<String, List<XMenu>> menus, final Map<String, String> appDirectoryMap) {
        log.info("[ INST ] 开始保存菜单");

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);

        // 第一轮：查询数据库，建立 NAME+APP_ID -> 数据库ID 的映射
        return this.buildMenuIdMapping(menus)
            .compose(dbIdMapping -> {
                // 第二轮：确定所有菜单的最终 ID，建立 新ID -> 最终ID 的映射
                final Map<String, String> finalIdMapping = this.determineFinalIds(menus, dbIdMapping);

                // 第三轮：修正所有菜单的 ID 和 PARENT_ID
                this.fixMenuIds(menus, finalIdMapping);

                // 第四轮：执行 upsert 操作
                final List<Future<String>> futures = new ArrayList<>();

                for (final Map.Entry<String, List<XMenu>> entry : menus.entrySet()) {
                    final String appId = entry.getKey();
                    final List<XMenu> appMenus = entry.getValue();

                    for (final XMenu menu : appMenus) {
                        final Future<String> future = this.upsertMenu(menu)
                            .onSuccess(action -> {
                                if ("insert".equals(action)) {
                                    insertCount.incrementAndGet();
                                } else if ("update".equals(action)) {
                                    updateCount.incrementAndGet();
                                }
                            });
                        futures.add(future);
                    }

                    // 生成缓存文件
                    this.generateMenuCache(appId, appMenus, appDirectoryMap.get(appId));
                }

                return Future.all(futures).map(v -> {
                    final int inserted = insertCount.get();
                    final int updated = updateCount.get();
                    log.info("[ INST ] 菜单保存完成: 新增 {} / 更新 {}", inserted, updated);
                    return new int[]{inserted, updated};
                });
            });
    }

    /**
     * Upsert 应用数据
     * 返回 "insert" 或 "update" 或 "skip"
     */
    private Future<String> upsertApp(final XApp app) {
        // 先查询是否存在
        return DB.on(XAppDao.class)
            .<XApp>fetchByIdAsync(app.getId())
            .compose(existing -> {
                if (existing == null) {
                    // 不存在，插入
                    return DB.on(XAppDao.class)
                        .insertAsync(app)
                        .map(inserted -> {
                            log.debug("[ INST ] 插入应用: {}", app.getName());
                            return "insert";
                        });
                } else {
                    // 存在，更新
                    return DB.on(XAppDao.class)
                        .updateAsync(app)
                        .map(updated -> {
                            log.debug("[ INST ] 更新应用: {}", app.getName());
                            return "update";
                        });
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存应用失败", err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * Upsert 菜单数据
     * 唯一键：NAME + APP_ID
     * 策略：以 XApp 加载的菜单为准（数据库优先）
     * 返回 "insert" 或 "update" 或 "skip"
     */
    private Future<String> upsertMenu(final XMenu menu) {
        // 先查询是否存在 (按 NAME + APP_ID)
        return DB.on(XMenuDao.class)
            .<XMenu>fetchAsync(KName.NAME, menu.getName())
            .compose(existingList -> {
                // 过滤出同一个 appId 的菜单
                XMenu existing = null;
                for (final XMenu m : existingList) {
                    if (m.getAppId().equals(menu.getAppId())) {
                        existing = m;
                        break;
                    }
                }

                if (existing == null) {
                    // 不存在，插入
                    return DB.on(XMenuDao.class)
                        .insertAsync(menu)
                        .map(inserted -> {
                            log.debug("[ INST ] 插入菜单: {} ({})", menu.getText(), menu.getName());
                            return "insert";
                        });
                } else {
                    // 存在，使用数据库中的 ID（保持 ID 稳定性）
                    // 将传入菜单的 ID 替换为数据库中的 ID，然后更新
                    menu.setId(existing.getId());
                    return DB.on(XMenuDao.class)
                        .updateAsync(menu)
                        .map(updated -> {
                            log.debug("[ INST ] 更新菜单: {} ({})", menu.getText(), menu.getName());
                            return "update";
                        });
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存菜单失败", err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * 建立菜单 ID 映射
     * 查询数据库中所有菜单，建立 NAME+APP_ID -> 数据库ID 的映射
     */
    private Future<Map<String, String>> buildMenuIdMapping(final Map<String, List<XMenu>> menus) {
        final Map<String, String> idMapping = new java.util.HashMap<>();

        // 收集所有 APP_ID
        final List<String> appIds = new ArrayList<>(menus.keySet());

        // 查询数据库中这些应用的所有菜单
        final List<Future<Void>> futures = new ArrayList<>();
        for (final String appId : appIds) {
            final Future<Void> future = DB.on(XMenuDao.class)
                .<XMenu>fetchAsync("APP_ID", appId)
                .map(dbMenus -> {
                    // 建立映射：NAME+APP_ID -> 数据库ID
                    for (final XMenu dbMenu : dbMenus) {
                        final String key = dbMenu.getName() + ":" + dbMenu.getAppId();
                        idMapping.put(key, dbMenu.getId());
                    }
                    return null;
                });
            futures.add(future);
        }

        return Future.all(futures).map(v -> {
            log.debug("[ INST ] 建立菜单 ID 映射: {} 个", idMapping.size());
            return idMapping;
        });
    }

    /**
     * 确定所有菜单的最终 ID
     * 如果菜单在数据库中已存在，使用数据库 ID；否则使用新生成的 ID
     * 返回：新ID -> 最终ID 的映射
     */
    private Map<String, String> determineFinalIds(final Map<String, List<XMenu>> menus,
                                                  final Map<String, String> dbIdMapping) {
        final Map<String, String> finalIdMapping = new java.util.HashMap<>();

        for (final Map.Entry<String, List<XMenu>> entry : menus.entrySet()) {
            final String appId = entry.getKey();
            final List<XMenu> appMenus = entry.getValue();

            for (final XMenu menu : appMenus) {
                final String originalId = menu.getId();
                final String key = menu.getName() + ":" + appId;
                final String dbId = dbIdMapping.get(key);

                if (dbId != null) {
                    // 数据库中已存在，使用数据库 ID
                    finalIdMapping.put(originalId, dbId);
                    menu.setId(dbId);
                } else {
                    // 数据库中不存在，使用新生成的 ID
                    finalIdMapping.put(originalId, originalId);
                }
            }
        }

        log.debug("[ INST ] 确定最终 ID: {} 个菜单", finalIdMapping.size());
        return finalIdMapping;
    }

    /**
     * 修正菜单的 PARENT_ID
     * 使用 finalIdMapping 将新生成的 PARENT_ID 替换为最终 ID
     */
    private void fixMenuIds(final Map<String, List<XMenu>> menus, final Map<String, String> finalIdMapping) {
        int fixedCount = 0;

        for (final List<XMenu> appMenus : menus.values()) {
            for (final XMenu menu : appMenus) {
                if (menu.getParentId() != null) {
                    final String finalParentId = finalIdMapping.get(menu.getParentId());
                    if (finalParentId != null && !finalParentId.equals(menu.getParentId())) {
                        // 修正 PARENT_ID
                        menu.setParentId(finalParentId);
                        fixedCount++;
                    }
                }
            }
        }

        log.debug("[ INST ] 修正 PARENT_ID: {} 个菜单", fixedCount);
    }

    /**
     * 生成菜单缓存文件
     * 使用 YAMLMapper 输出标准 YAML 格式
     * 匹配逻辑：只在缓存不存在或内容不匹配时才重新生成
     */
    private void generateMenuCache(final String appId, final List<XMenu> menus, final String childAppDir) {
        try {
            // 创建缓存目录
            final File appCacheDir = this.resolveMenuCacheDir(appId, childAppDir);
            if (!appCacheDir.exists()) {
                appCacheDir.mkdirs();
            }

            // 使用序列化构造菜单数据
            final JsonArray menuArray = new JsonArray();
            for (final XMenu menu : menus) {
                final JsonObject menuJson = Ut.serializeJson(menu);
                menuArray.add(menuJson);
            }

            // 构造输出对象
            final JsonObject output = new JsonObject().put("data", menuArray);

            // 使用 YAMLMapper 输出 YAML 格式
            final String cacheFilePath = appCacheDir.getAbsolutePath() + "/menu.yml";
            final Object jsonObject = Json.decodeValue(output.encode());
            final String yamlContent = YAML_MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject);

            // 检查缓存是否存在
            final File cacheFile = new File(cacheFilePath);
            boolean shouldWrite = true;
            if (cacheFile.exists()) {
                // 读取现有缓存内容
                final String existingContent = new String(Files.readAllBytes(Paths.get(cacheFilePath)));
                if (existingContent.equals(yamlContent)) {
                    // 内容匹配，跳过写入
                    shouldWrite = false;
                    log.debug("[ INST ] 缓存匹配，跳过生成: {} ({} 个菜单)", appId, menus.size());
                } else {
                    log.debug("[ INST ] 缓存不匹配，重新生成: {} ({} 个菜单)", appId, menus.size());
                }
            } else {
                log.debug("[ INST ] 缓存不存在，生成新缓存: {} ({} 个菜单)", appId, menus.size());
            }

            if (shouldWrite) {
                Files.write(Paths.get(cacheFilePath), yamlContent.getBytes());
                log.debug("[ INST ] 生成缓存: {} ({} 个菜单)", appId, menus.size());
            }
        } catch (final Exception e) {
            log.error("[ INST ] 生成缓存失败", e);
        }
    }

    /**
     * 生成 instance.yml 文件
     * 格式: running: { UUID: code }
     * 包含所有已插入数据库的应用实例映射
     */
    private void generateInstanceYml(final Map<String, XApp> apps) {
        try {
            // 构造 YAML 内容（手动拼接以确保格式正确）
            final StringBuilder yamlContent = new StringBuilder();
            yamlContent.append("running:\n");

            for (final XApp app : apps.values()) {
                if (app.getId() != null && app.getCode() != null) {
                    // 格式: "  UUID: code" (无引号)
                    yamlContent.append("  ")
                        .append(app.getId())
                        .append(": ")
                        .append(app.getCode())
                        .append("\n");
                }
            }

            // 写入文件
            final String instanceFilePath = this.cacheDir + "/instance.yml";
            Files.write(Paths.get(instanceFilePath), yamlContent.toString().getBytes());

            log.info("[ INST ] 生成 instance.yml: {} 个应用实例", apps.size());
        } catch (final Exception e) {
            log.error("[ INST ] 生成 instance.yml 失败", e);
        }
    }

    /**
     * 生成缓存标识文件
     * 在 apps/{Z_APP_ID}/ 目录下创建 {child-app-id} 空白文件
     * 同时兼容旧版 apps/{UUID}/{code} 标识文件，避免存量缓存失效
     */
    private void generateCacheMarkers(final Map<String, XApp> apps) {
        try {
            for (final XApp app : apps.values()) {
                if (app.getId() != null && app.getCode() != null) {
                    this.ensureMarker(new File(this.cacheDir), app.getCode());

                    final File legacyDir = new File(this.appsRoot, app.getId());
                    if (!legacyDir.getAbsolutePath().equals(new File(this.cacheDir).getAbsolutePath())) {
                        this.ensureMarker(legacyDir, app.getCode());
                    }
                }
            }
            log.info("[ INST ] 生成缓存标识文件完成");
        } catch (final Exception e) {
            log.error("[ INST ] 生成缓存标识文件失败", e);
        }
    }

    private void ensureMarker(final File markerDir, final String markerName) throws Exception {
        if (!markerDir.exists()) {
            markerDir.mkdirs();
        }
        final File markerFile = new File(markerDir, markerName);
        if (!markerFile.exists()) {
            markerFile.createNewFile();
            log.debug("[ INST ] 创建缓存标识: {}/{}", markerDir.getName(), markerName);
        }
    }

    private File resolveMenuCacheDir(final String appId, final String childAppDir) {
        if (childAppDir != null && !childAppDir.isEmpty()) {
            return new File(this.cacheDir, childAppDir);
        }
        return new File(this.cacheDir, appId);
    }
}

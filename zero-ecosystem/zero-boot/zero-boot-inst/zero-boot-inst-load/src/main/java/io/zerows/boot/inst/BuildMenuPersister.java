package io.zerows.boot.inst;

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
class BuildMenuPersister {

    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    private final Vertx vertx;
    private final String cacheDir;

    private BuildMenuPersister(final Vertx vertx, final String cacheDir) {
        this.vertx = vertx;
        this.cacheDir = cacheDir;
    }

    static BuildMenuPersister create(final Vertx vertx, final String cacheDir) {
        return new BuildMenuPersister(vertx, cacheDir);
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

            // 生成缓存标识文件 apps/{UUID}/{code}
            this.generateCacheMarkers(apps);

            return new int[]{inserted, updated};
        });
    }

    /**
     * 保存菜单数据到数据库
     * 返回 [新增数, 更新数]
     */
    Future<int[]> saveMenus(final Map<String, List<XMenu>> menus) {
        log.info("[ INST ] 开始保存菜单");

        final AtomicInteger insertCount = new AtomicInteger(0);
        final AtomicInteger updateCount = new AtomicInteger(0);
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
            this.generateMenuCache(appId, appMenus);
        }

        return Future.all(futures).map(v -> {
            final int inserted = insertCount.get();
            final int updated = updateCount.get();
            log.info("[ INST ] 菜单保存完成: 新增 {} / 更新 {}", inserted, updated);
            return new int[]{inserted, updated};
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
                    // 存在，使用数据库中的 ID 更新
                    // 如果 ID 不同，需要先删除旧记录再插入新记录
                    if (!menu.getId().equals(existing.getId())) {
                        log.debug("[ INST ] 菜单 {} 的 ID 变化: {} -> {}",
                            menu.getName(), existing.getId(), menu.getId());
                        // 删除旧记录
                        return DB.on(XMenuDao.class)
                            .deleteByIdAsync(existing.getId())
                            .compose(deleted -> {
                                // 插入新记录
                                return DB.on(XMenuDao.class)
                                    .insertAsync(menu)
                                    .map(inserted -> {
                                        log.debug("[ INST ] 重新插入菜单: {} ({})", menu.getText(), menu.getName());
                                        return "update";
                                    });
                            });
                    } else {
                        // ID 相同，直接更新
                        return DB.on(XMenuDao.class)
                            .updateAsync(menu)
                            .map(updated -> {
                                log.debug("[ INST ] 更新菜单: {} ({})", menu.getText(), menu.getName());
                                return "update";
                            });
                    }
                }
            })
            .recover(err -> {
                log.error("[ INST ] 保存菜单失败", err);
                return Future.succeededFuture("skip");
            });
    }

    /**
     * 生成菜单缓存文件
     * 使用 YAMLMapper 输出标准 YAML 格式
     * 匹配逻辑：只在缓存不存在或内容不匹配时才重新生成
     */
    private void generateMenuCache(final String appId, final List<XMenu> menus) {
        try {
            // 创建缓存目录
            final File appCacheDir = new File(this.cacheDir, appId);
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
     * 在 apps/{UUID}/ 目录下创建 {code} 空白文件
     * 用于重新导入时标识应用，防止缓存重新生成
     */
    private void generateCacheMarkers(final Map<String, XApp> apps) {
        try {
            for (final XApp app : apps.values()) {
                if (app.getId() != null && app.getCode() != null) {
                    // 创建 apps/{UUID}/ 目录
                    final File appDir = new File(this.cacheDir, app.getId());
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }

                    // 创建 apps/{UUID}/{code} 空白文件
                    final File markerFile = new File(appDir, app.getCode());
                    if (!markerFile.exists()) {
                        markerFile.createNewFile();
                        log.debug("[ INST ] 创建缓存标识: {}/{}", app.getId(), app.getCode());
                    }
                }
            }
            log.info("[ INST ] 生成缓存标识文件完成");
        } catch (final Exception e) {
            log.error("[ INST ] 生成缓存标识文件失败", e);
        }
    }
}

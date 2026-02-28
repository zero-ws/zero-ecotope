package io.zerows.boot.inst;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XMenu;
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
public class BuildApp {

    /**
     * 执行应用和菜单数据的加载与持久化
     * <p>
     * 核心流程：
     * 1. 加载全局配置（environment.json 的 global 节点）
     * 2. 扫描所有模块的 apps 目录（apps/{UUID}.yml 和 apps/{UUID}/nav/）
     * 3. 使用 BuildAppMenuLoader 加载应用和菜单数据
     * 4. 使用 BuildAppMenuPersister 持久化到数据库（XApp 按 ID 判重，XMenu 按 NAME+APP_ID 判重）
     * 5. 生成缓存文件（apps/{UUID}/menu.yml）
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
            final JsonObject globalConfig = loadGlobalConfig();

            // 2. 扫描应用和菜单目录
            final List<URI> appUris = InstApps.of().ioApp();        // apps/{UUID}.yml
            final List<URI> menuDirUris = InstApps.of().ioRunning(); // apps/{UUID}/nav/

            log.info("[ INST ] 扫描到 {} 个应用文件，{} 个菜单目录", appUris.size(), menuDirUris.size());

            // 3. 加载应用和菜单数据
            final BuildMenuLoader loader = BuildMenuLoader.create(globalConfig);
            loader.loadApps(appUris);
            loader.loadMenus(menuDirUris);

            final Map<String, XApp> apps = loader.getApps();
            final Map<String, List<XMenu>> menus = loader.getMenus();

            if (apps.isEmpty() && menus.isEmpty()) {
                log.warn("[ INST ] 未加载到任何应用或菜单数据");
                return Future.succeededFuture(false);
            }

            // 4. 创建缓存目录（apps/{UUID}/menu.yml）
            // 优先使用 R2MO_HOME 环境变量，未配置则使用当前目录
            final String cacheDir = resolveCacheDir();
            final File cacheDirFile = new File(cacheDir);
            if (!cacheDirFile.exists()) {
                final boolean created = cacheDirFile.mkdirs();
                log.info("[ INST ] 创建缓存目录: {}", cacheDir);
            } else {
                log.info("[ INST ] 使用缓存目录: {}", cacheDir);
            }

            // 5. 持久化到数据库（XApp 按 ID 判重，XMenu 按 NAME+APP_ID 判重）
            final BuildMenuPersister persister = BuildMenuPersister.create(vertx, cacheDir);

            return persister.saveApps(apps)
                .compose(appStats -> persister.saveMenus(menus)
                    .map(menuStats -> {
                        final int totalMenus = menus.values().stream().mapToInt(List::size).sum();
                        log.info("[ INST ] ========================================");
                        log.info("[ INST ] 导入统计:");
                        log.info("[ INST ]   应用: 加载 {} / 新增 {} / 更新 {}", apps.size(), appStats[0], appStats[1]);
                        log.info("[ INST ]   菜单: 加载 {} / 新增 {} / 更新 {}", totalMenus, menuStats[0], menuStats[1]);
                        log.info("[ INST ]   缓存: {}", cacheDir);
                        log.info("[ INST ] ========================================");
                        return true;
                    })
                )
                .recover(err -> {
                    log.error("[ INST ] 导入失败", err);
                    return Future.succeededFuture(false);
                });
        } catch (final Exception e) {
            log.error("[ INST ] 加载失败", e);
            return Future.failedFuture(e);
        }
    }

    /**
     * 加载全局配置
     * 使用 ZeroFs 从 src/main/resources/init/environment.json 加载配置
     * 使用 Ut.compileAnsible() 处理环境变量替换
     */
    private static JsonObject loadGlobalConfig() {
        try {
            final ZeroFs fs = ZeroFs.of();
            final String envPath = "init/environment.json";

            if (!fs.isExist(envPath)) {
                log.warn("[ INST ] 未找到 environment.json，使用默认配置");
                return new JsonObject()
                    .put("language", "cn")
                    .put("active", true);
            }

            // 使用 ZeroFs 加载配置文件
            final JsonObject tenantJ = fs.inJObject(envPath);

            final String parsed = Ut.compileAnsible(tenantJ.encode());
            log.debug("[ INST ] 加载全局配置完成");

            final JsonObject parsedJ = new JsonObject(parsed);
            return Ut.valueJObject(parsedJ, "global");
        } catch (final Exception e) {
            log.error("[ INST ] 加载全局配置失败", e);
            return new JsonObject()
                .put("language", "cn")
                .put("active", true);
        }
    }

    /**
     * 解析缓存目录
     * 优先使用 R2MO_HOME 环境变量，未配置则使用当前目录
     */
    private static String resolveCacheDir() {
        final String r2moHome = System.getenv("R2MO_HOME");
        if (r2moHome != null && !r2moHome.trim().isEmpty()) {
            // 使用 R2MO_HOME/apps
            return r2moHome + "/apps";
        } else {
            // 使用当前目录/apps
            return System.getProperty("user.dir") + "/apps";
        }
    }
}

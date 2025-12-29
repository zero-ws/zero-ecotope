package io.zerows.plugins.monitor.hawtio;

import io.hawt.embedded.Main;
import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.monitor.metadata.MonitorType;
import io.zerows.plugins.monitor.server.MonitorJmxConnector;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Hawtio 4.x Embedded 启动器 (内置资源版)
 * <p>
 * 1. 支持从 classpath (/plugins/war/...) 自动释放 WAR 包。
 * 2. 兼容 IDE Maven 开发环境推断。
 * </p>
 *
 * @author lang : 2025-12-29
 */
@Slf4j
@SPID("MNTR/HAWTIO")
public class MonitorJmxHawtIo implements MonitorJmxConnector {

    private static final String KEY_PORT = "port";
    private static final String KEY_CONTEXT = "context";
    private static final String KEY_AUTH = "authentication";
    private static final String KEY_WAR = "war-path"; // 支持手动指定

    // 对应 src/main/resources/plugins/war/hawtio-war-4.6.2.war
    private static final String INTERNAL_WAR_PATH = "/plugins/war/hawtio-war-4.6.2.war";

    /**
     * 寻找 hawtio-war 文件
     * 优先级：
     * 1. 配置文件显式指定 (运维手动覆盖)
     * 2. 内置资源释放 (标准发布模式 - FatJar/Docker)
     * 3. Maven 仓库推断 (本地开发兜底)
     */
    private static String findHawtioWarPath(final JsonObject config) {
        // 策略 1: 显式配置 (最高优先级)
        final String configPath = config.getString(KEY_WAR);
        if (configPath != null && !configPath.isEmpty()) {
            final File f = new File(configPath);
            if (f.exists()) {
                log.info("[ MNTR ] 策略1-显式配置: 找到 WAR -> {}", f.getAbsolutePath());
                return f.getAbsolutePath();
            } else {
                log.warn("[ MNTR ] 配置的 war-path 不存在: {}", configPath);
            }
        }

        // 策略 2: 内置资源释放 (推荐: 对应 src/main/resources 下的文件)
        try (final InputStream in = MonitorJmxHawtIo.class.getResourceAsStream(INTERNAL_WAR_PATH)) {
            if (in != null) {
                // 创建临时文件，前缀 z-hawtio-, 后缀 .war
                final File tempWar = File.createTempFile("z-hawtio-", ".war");
                // JVM 退出时自动删除，保持系统清洁
                tempWar.deleteOnExit();

                // 将资源流写入临时文件
                Files.copy(in, tempWar.toPath(), StandardCopyOption.REPLACE_EXISTING);

                final String absPath = tempWar.getAbsolutePath();
                log.info("[ MNTR ] 策略2-内置资源: 已释放 WAR 至 -> {}", absPath);
                return absPath;
            } else {
                log.debug("[ MNTR ] 未在 Classpath 中找到内置 WAR: {}", INTERNAL_WAR_PATH);
            }
        } catch (final Exception e) {
            log.warn("[ MNTR ] 策略2 资源释放失败: {}", e.getMessage());
        }

        // 策略 3: Maven 仓库推断 (本地 IDE 开发兜底)
        // 当你还没把 war 包拷贝到 resources 目录，但本地 maven 仓库里有时生效
        try {
            final ProtectionDomain domain = Main.class.getProtectionDomain();
            final CodeSource codeSource = domain.getCodeSource();
            if (codeSource != null) {
                final URL location = codeSource.getLocation();
                final String embeddedJarPath = new File(location.toURI()).getAbsolutePath();

                if (embeddedJarPath.endsWith(".jar")) {
                    final String warPath = embeddedJarPath
                        .replace("hawtio-embedded", "hawtio-war")
                        .replace(".jar", ".war");

                    final File warFile = new File(warPath);
                    if (warFile.exists()) {
                        log.info("[ MNTR ] 策略3-Maven推断: 找到 WAR -> {}", warFile.getAbsolutePath());
                        return warFile.getAbsolutePath();
                    }
                }
            }
        } catch (final Throwable e) {
            log.warn("[ MNTR ] 策略3 推断失败: {}", e.getMessage());
        }

        log.error("[ MNTR ] 致命错误: 无法找到 hawtio-war 文件！请确保文件存在于 '{}' 或配置 'war-path'。", INTERNAL_WAR_PATH);
        return null;
    }

    private static Thread getThread(final int port, final String contextPath, final String warLocation) {
        final Thread hawtioThread = new Thread(() -> {
            if (warLocation == null) {
                return;
            }

            try {
                // 1. 设置 TCCL (Jetty 启动 WebApp 必须依赖 ContextClassLoader)
                Thread.currentThread().setContextClassLoader(MonitorJmxHawtIo.class.getClassLoader());

                final Main main = new Main();
                main.setPort(port);
                main.setContextPath(contextPath);

                // 2. 注入找到的 WAR 路径
                main.setWarLocation(warLocation);

                log.info("[ MNTR ] Hawtio 服务准备就绪 -> http://localhost:{}{}", port, contextPath);

                // 3. 启动并阻塞
                main.run();

            } catch (final Exception e) {
                log.error("[ MNTR ] Hawtio 启动异常: {}", e.getMessage(), e);
            }
        });

        hawtioThread.setName("zerows-hawtio-server");
        hawtioThread.setDaemon(true);
        return hawtioThread;
    }

    @Override
    public boolean isMatch(final MonitorType required) {
        return MonitorType.HAWTIO == required;
    }

    @Override
    public Future<Boolean> startAsync(final JsonObject config, final Vertx vertxRef) {
        if (config == null) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        final int port = config.getInteger(KEY_PORT, 6091);
        final String rawContext = config.getString(KEY_CONTEXT, "/monitor");
        final String contextPath = rawContext.startsWith("/") ? rawContext : "/" + rawContext;
        final boolean auth = config.getBoolean(KEY_AUTH, false);

        // 寻找 WAR
        final String warPath = findHawtioWarPath(config);
        if (warPath == null) {
            // 找不到 WAR 包时不中断主程序，仅打印错误
            return Future.succeededFuture(Boolean.TRUE);
        }

        System.setProperty("hawtio.authenticationEnabled", String.valueOf(auth));

        log.info("[ MNTR ] 正在初始化 Hawtio Console (v4.6.2)...");

        final Thread hawtioThread = getThread(port, contextPath, warPath);
        hawtioThread.start();

        return Future.succeededFuture(Boolean.TRUE);
    }
}
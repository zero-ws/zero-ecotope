package io.zerows.boot.extension.appcontainer;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 权限资源扫描实现
 * 负责扫描 classpath 下所有 plugins/{MID}/security/ 目录
 */
@Slf4j
@SuppressWarnings("all")
class InstPermLoad implements InstPerm {

    private static volatile boolean initialized = false;
    private static final Map<String, URI> RESOURCE_DIRS = new HashMap<>();
    private static final Map<String, URI> ROLE_DIRS = new HashMap<>();

    /**
     * 懒加载初始化，确保扫描且仅扫描一次 classpath
     */
    private void initIfNeeded() {
        if (initialized) {
            return;
        }
        synchronized (InstPermLoad.class) {
            if (initialized) {
                return;
            }
            try {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                // 扫描所有 plugins 目录
                final Enumeration<URL> urls = classLoader.getResources("plugins");

                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    final String protocol = url.getProtocol();

                    if ("jar".equals(protocol)) {
                        this.loadFromJar(url);
                    } else if ("file".equals(protocol)) {
                        this.loadFromFile(url);
                    }
                }
                initialized = true;

                log.info("[ PERM ] 扫描完成: RBAC_RESOURCE {} 个, RBAC_ROLE {} 个",
                    RESOURCE_DIRS.size(), ROLE_DIRS.size());
            } catch (final Exception e) {
                log.error("[ PERM ] 扫描 plugins 目录失败", e);
            }
        }
    }

    @Override
    public Map<String, URI> ioResource() {
        this.initIfNeeded();
        return new HashMap<>(RESOURCE_DIRS);
    }

    @Override
    public Map<String, URI> ioRole() {
        this.initIfNeeded();
        return new HashMap<>(ROLE_DIRS);
    }

    /**
     * 解析本地文件系统（用于开发环境 IDE 中直接运行）
     */
    private void loadFromFile(final URL url) {
        try {
            final File pluginsDir = new File(url.toURI());
            if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
                return;
            }

            // 遍历 plugins 目录下的所有模块目录 {MID}
            final File[] moduleDirs = pluginsDir.listFiles(File::isDirectory);
            if (moduleDirs == null) {
                return;
            }

            for (final File moduleDir : moduleDirs) {
                final String mid = moduleDir.getName();
                final File securityDir = new File(moduleDir, "security");

                if (securityDir.exists() && securityDir.isDirectory()) {
                    // 检查 RBAC_RESOURCE 目录
                    final File resourceDir = new File(securityDir, "RBAC_RESOURCE");
                    if (resourceDir.exists() && resourceDir.isDirectory()) {
                        RESOURCE_DIRS.put(mid, resourceDir.toURI());
                        log.info("[ PERM ] [File] 找到 RBAC_RESOURCE: plugins/{}/security/RBAC_RESOURCE", mid);
                    }

                    // 检查 RBAC_ROLE 目录
                    final File roleDir = new File(securityDir, "RBAC_ROLE");
                    if (roleDir.exists() && roleDir.isDirectory()) {
                        ROLE_DIRS.put(mid, roleDir.toURI());
                        log.info("[ PERM ] [File] 找到 RBAC_ROLE: plugins/{}/security/RBAC_ROLE", mid);
                    }
                }
            }
        } catch (final Exception e) {
            log.error("[ PERM ] 解析文件系统 URL 失败: {}", url, e);
        }
    }

    /**
     * 解析 JAR 包内部文件系统
     */
    private void loadFromJar(final URL url) throws Exception {
        final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        try (final JarFile jarFile = jarURLConnection.getJarFile()) {
            final Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();

                // 匹配 plugins/{MID}/security/RBAC_RESOURCE 或 RBAC_ROLE 目录
                if (entryName.startsWith("plugins/") && entry.isDirectory()) {
                    // 解析路径: plugins/{MID}/security/{TYPE}/
                    final String[] parts = entryName.split("/");
                    if (parts.length >= 4) {
                        final String mid = parts[1];
                        final String security = parts[2];
                        final String type = parts[3].endsWith("/")
                            ? parts[3].substring(0, parts[3].length() - 1)
                            : parts[3];

                        if ("security".equals(security)) {
                            final URL jarBaseUrl = jarURLConnection.getJarFileURL();
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;

                            if ("RBAC_RESOURCE".equals(type)) {
                                RESOURCE_DIRS.put(mid, new URI(uriStr.replace(" ", "%20")));
                                log.info("[ PERM ] [JAR] 找到 RBAC_RESOURCE: plugins/{}/security/RBAC_RESOURCE", mid);
                            } else if ("RBAC_ROLE".equals(type)) {
                                ROLE_DIRS.put(mid, new URI(uriStr.replace(" ", "%20")));
                                log.info("[ PERM ] [JAR] 找到 RBAC_ROLE: plugins/{}/security/RBAC_ROLE", mid);
                            }
                        }
                    }
                }
            }
        }
    }
}
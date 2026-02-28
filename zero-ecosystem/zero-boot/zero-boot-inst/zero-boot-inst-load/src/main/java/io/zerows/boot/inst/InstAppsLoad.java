package io.zerows.boot.inst;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
class InstAppsLoad implements InstApps {
    // 约定：
    // Boolean.TRUE  -> 存储 yml 文件的 URI 集合 (ioApp)
    // Boolean.FALSE -> 存储 目录 的 URI 集合 (ioRunning)
    private static final Map<Boolean, Set<URI>> APPS = new HashMap<>();
    private static volatile boolean initialized = false;

    static {
        APPS.put(Boolean.TRUE, new LinkedHashSet<>());
        APPS.put(Boolean.FALSE, new LinkedHashSet<>());
    }

    /**
     * 懒加载初始化，确保扫描且仅扫描一次 classpath
     */
    private void initIfNeeded() {
        if (initialized) {
            return;
        }
        synchronized (InstAppsLoad.class) {
            if (initialized) {
                return;
            }
            try {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                final Enumeration<URL> urls = classLoader.getResources("apps");

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
            } catch (final Exception e) { // 这里扩大捕获范围，兼容 URISyntaxException
                log.error("[ INST ] 扫描 apps 目录失败", e);
            }
        }
    }

    @Override
    public List<URI> ioApp() {
        this.initIfNeeded();
        return new ArrayList<>(APPS.get(Boolean.TRUE));
    }

    @Override
    public List<URI> ioRunning() {
        this.initIfNeeded();
        return new ArrayList<>(APPS.get(Boolean.FALSE));
    }

    /**
     * 解析本地文件系统（用于开发环境 IDE 中直接运行）
     */
    private void loadFromFile(final URL url) {
        try {
            final File dir = new File(url.toURI());
            if (dir.exists() && dir.isDirectory()) {
                final File[] children = dir.listFiles();
                if (children != null) {
                    for (final File child : children) {
                        if (child.isFile() && child.getName().endsWith(".yml")) {
                            log.info("[ INST ] [File] 加载直属 yml 文件: `apps/{}`", child.getName());
                            // 直接使用 toURI() 存入 Set
                            APPS.get(Boolean.TRUE).add(child.toURI());
                        } else if (child.isDirectory()) {
                            log.info("[ INST ] [File] 加载直属目录: `apps/{}`", child.getName());
                            // 直接使用 toURI() 存入 Set
                            APPS.get(Boolean.FALSE).add(child.toURI());
                        }
                    }
                }
            }
        } catch (final Exception e) {
            log.error("[ INST ] 解析文件系统 URL 失败: {}", url, e);
        }
    }

    /**
     * 解析 JAR 包内部文件系统
     */
    /**
     * 解析 JAR 包内部文件系统
     */
    private void loadFromJar(final URL url) throws Exception {
        final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        try (final JarFile jarFile = jarURLConnection.getJarFile()) {
            final Enumeration<JarEntry> entries = jarFile.entries();

            // 获取当前 jar 包的基础 URL（例如: file:/path/to/my.jar）
            final URL jarBaseUrl = jarURLConnection.getJarFileURL();

            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();

                // 筛选出 apps/ 目录下的元素，且排除 "apps/" 目录本身
                if (entryName.startsWith("apps/") && entryName.length() > 5) {
                    // 去掉 "apps/" 前缀，获取相对路径
                    final String relativePath = entryName.substring(5);

                    if (!entry.isDirectory()) {
                        // 1. 获取直属 .yml 文件：相对路径中不能包含 '/' (不递归)
                        if (!relativePath.contains("/") && relativePath.endsWith(".yml")) {
                            log.info("[ INST ] [JAR] 加载直属 yml 文件: {}", entryName);
                            // 修复 JDK 20+ 警告：直接使用 URI 构造，并安全处理可能的空格
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                            APPS.get(Boolean.TRUE).add(new URI(uriStr.replace(" ", "%20")));
                        }
                    } else {
                        // 2. 获取直属目录：相对路径以 '/' 结尾，且去掉结尾的 '/' 后不再包含其他 '/' (不递归)
                        final String pathWithoutTrailingSlash = relativePath.substring(0, relativePath.length() - 1);
                        if (!pathWithoutTrailingSlash.contains("/")) {
                            log.info("[ INST ] [JAR] 加载直属目录: {}", entryName);
                            // 修复 JDK 20+ 警告：直接使用 URI 构造
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                            APPS.get(Boolean.FALSE).add(new URI(uriStr.replace(" ", "%20")));
                        }
                    }
                }
            }
        }
    }
}
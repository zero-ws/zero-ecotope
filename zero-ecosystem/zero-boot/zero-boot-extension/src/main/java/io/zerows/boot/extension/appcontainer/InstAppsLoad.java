package io.zerows.boot.extension.appcontainer;

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
@SuppressWarnings("all")
class InstAppsLoad implements InstApps {
    // 约定：
    // Boolean.TRUE  -> 存储 yml 文件的 URI 集合 (ioApp)
    // Boolean.FALSE -> 存储 目录 的 URI 集合 (ioRunning)
    private static final Map<Boolean, Set<URI>> APPS = new HashMap<>();
    private static volatile boolean initialized = false;
    private static URI instanceYmlUri = null;

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

    @Override
    public Map<String, String> ioInstance() {
        this.initIfNeeded();
        if (instanceYmlUri == null) {
            log.debug("[ INST ] 未找到 apps/instance.yml");
            return new HashMap<>();
        }

        try {
            final String path = instanceYmlUri.getPath();
            final io.vertx.core.json.JsonObject data = io.zerows.support.Ut.ioYaml(path);
            if (data == null || !data.containsKey("running")) {
                log.warn("[ INST ] instance.yml 格式错误，缺少 running 节点");
                return new HashMap<>();
            }

            final io.vertx.core.json.JsonObject running = data.getJsonObject("running");
            final Map<String, String> instanceMap = new HashMap<>();

            // 遍历 running 节点，格式：UUID=name
            for (final String uuid : running.fieldNames()) {
                final String name = running.getString(uuid);
                if (name != null && !name.isEmpty()) {
                    instanceMap.put(name, uuid);
                    log.debug("[ INST ] 加载实例映射: {} -> {}", name, uuid);
                }
            }

            log.info("[ INST ] 加载 instance.yml 完成，共 {} 个映射", instanceMap.size());
            return instanceMap;
        } catch (final Exception e) {
            log.error("[ INST ] 读取 instance.yml 失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public io.vertx.core.json.JsonObject ioInit() {
        this.initIfNeeded();
        if (instanceYmlUri == null) {
            log.debug("[ INST ] 未找到 apps/instance.yml");
            return new io.vertx.core.json.JsonObject();
        }

        try {
            final String path = instanceYmlUri.getPath();
            final io.vertx.core.json.JsonObject data = io.zerows.support.Ut.ioYaml(path);
            if (data == null || !data.containsKey("init")) {
                log.debug("[ INST ] instance.yml 中未找到 init 节点");
                return new io.vertx.core.json.JsonObject();
            }

            final io.vertx.core.json.JsonObject init = data.getJsonObject("init");
            log.info("[ INST ] 加载 init 配置完成");
            return init;
        } catch (final Exception e) {
            log.error("[ INST ] 读取 init 配置失败", e);
            return new io.vertx.core.json.JsonObject();
        }
    }

    /**
     * 解析本地文件系统（用于开发环境 IDE 中直接运行）
     */
    private void loadFromFile(final URL url) {
        try {
            final File dir = new File(url.toURI());
            if (dir.exists() && dir.isDirectory()) {
                this.scanFileNode(dir, 0);
            }
        } catch (final Exception e) {
            log.error("[ INST ] 解析文件系统 URL 失败: {}", url, e);
        }
    }

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
                    final String normalized = relativePath.endsWith("/") ? relativePath.substring(0, relativePath.length() - 1) : relativePath;
                    final String[] segments = normalized.split("/");

                    if (!entry.isDirectory()) {
                        if ("instance.yml".equals(normalized)) {
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                            instanceYmlUri = new URI(uriStr.replace(" ", "%20"));
                            log.info("[ INST ] [JAR] 找到 instance.yml");
                        } else if (segments.length == 1 && normalized.endsWith(".yml")) {
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                            log.info("[ INST ] [JAR] 加载直属 yml 文件: {}", entryName);
                            APPS.get(Boolean.TRUE).add(new URI(uriStr.replace(" ", "%20")));
                        } else if (segments.length == 2 && normalized.endsWith(".yml")) {
                            final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                            log.info("[ INST ] [JAR] 加载嵌套 yml 文件: {}", entryName);
                            APPS.get(Boolean.TRUE).add(new URI(uriStr.replace(" ", "%20")));
                        }
                    } else if (segments.length == 1 || segments.length == 2) {
                        final String uriStr = "jar:" + jarBaseUrl + "!/" + entryName;
                        log.info("[ INST ] [JAR] 加载应用目录: {}", entryName);
                        APPS.get(Boolean.FALSE).add(new URI(uriStr.replace(" ", "%20")));
                    }
                }
            }
        }
    }

    private void scanFileNode(final File dir, final int depth) {
        final File[] children = dir.listFiles();
        if (children == null) {
            return;
        }

        for (final File child : children) {
            if (child.isFile() && child.getName().endsWith(".yml")) {
                if ("instance.yml".equals(child.getName()) && depth == 0) {
                    instanceYmlUri = child.toURI();
                    log.info("[ INST ] [File] 找到 instance.yml");
                } else if (depth <= 1) {
                    log.info("[ INST ] [File] 加载应用 yml 文件: {}", child.getPath());
                    APPS.get(Boolean.TRUE).add(child.toURI());
                }
            } else if (child.isDirectory()) {
                final File navDir = new File(child, "nav");
                if (navDir.exists() && navDir.isDirectory()) {
                    log.info("[ INST ] [File] 加载应用目录: {}", child.getPath());
                    APPS.get(Boolean.FALSE).add(child.toURI());
                }
                if (depth == 0) {
                    this.scanFileNode(child, depth + 1);
                }
            }
        }
    }
}

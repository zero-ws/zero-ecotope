package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 模型定义加载器 (包级私有)
 */
@Slf4j
class LoaderSchema {

    private static final String DIR_SCHEMA = "openapi/components/schemas";
    private static final String EXT_MD = ".md";

    private LoaderSchema() {
    }

    static void load(final OpenAPI openAPI) {
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new Components());
        }

        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final Enumeration<URL> urls = loader.getResources(DIR_SCHEMA);

            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    scanFile(new File(url.toURI()), openAPI);
                } else if ("jar".equals(protocol)) {
                    scanJar(url, openAPI);
                }
            }
        } catch (final Exception e) {
            log.error("{} Schema 目录扫描失败: {}", SwaggerConstant.K_PREFIX_DOC, DIR_SCHEMA, e);
        }
    }

    private static void scanFile(final File dir, final OpenAPI openAPI) {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (file.getName().endsWith(EXT_MD)) {
                final String key = file.getName().replace(EXT_MD, "");
                parseAndRegister(key, DIR_SCHEMA + "/" + file.getName(), openAPI);
            }
        }
    }

    private static void scanJar(final URL url, final OpenAPI openAPI) {
        try {
            final String path = url.getPath();
            final String jarPath = path.substring(5, path.indexOf("!"));
            try (final JarFile jar = new JarFile(jarPath)) {
                final Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    final String name = entry.getName();
                    if (name.startsWith(DIR_SCHEMA) && name.endsWith(EXT_MD)) {
                        final String fileName = name.substring(name.lastIndexOf('/') + 1);
                        final String key = fileName.replace(EXT_MD, "");

                        try (final InputStream in = jar.getInputStream(entry)) {
                            // 传入 name 作为路径
                            doParse(key, in, openAPI, name);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            log.error("{} Jar 扫描失败", SwaggerConstant.K_PREFIX_DOC, e);
        }
    }

    private static void parseAndRegister(final String key, final String resourcePath, final OpenAPI openAPI) {
        try (final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return;
            }
            // 传入 resourcePath 作为路径
            doParse(key, in, openAPI, resourcePath);
        } catch (final Exception e) {
            log.error("{} 文件读取失败: {}", SwaggerConstant.K_PREFIX_DOC, resourcePath, e);
        }
    }

    private static void doParse(final String key, final InputStream in, final OpenAPI openAPI, final String path) {
        final Schema<?> schema = LoaderMarkdown.load(in, Schema.class);

        if (schema != null) {
            openAPI.getComponents().addSchemas(key, schema);
            // 1. 打印添加的新的 Schema，包含 $ref 写法和路径
            log.info("{} > Schema 添加成功: {} -> #/components/schemas/{} \t - 文档路径: {}",
                SwaggerConstant.K_PREFIX_DOC,
                key,
                key,
                path
            );
        }
    }
}
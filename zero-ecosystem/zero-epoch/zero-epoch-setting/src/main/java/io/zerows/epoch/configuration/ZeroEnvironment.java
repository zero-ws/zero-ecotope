package io.zerows.epoch.configuration;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.zerows.epoch.annotations.boot.PropertySource;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.specification.configuration.HEnvironment;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-05
 */
@Slf4j
public class ZeroEnvironment implements HEnvironment {
    private static final ConcurrentMap<String, String> ENV_VARS = new ConcurrentHashMap<>();
    private static final ZeroEnvironment INSTANCE = new ZeroEnvironment();

    private ZeroEnvironment() {
        // 私有构造函数，防止外部实例化
    }

    public static ZeroEnvironment of() {
        return INSTANCE;
    }

    public void whenStart(final Class<?> clazz) {
        final PropertySource source = clazz.getDeclaredAnnotation(PropertySource.class);
        if (source != null) {
            final String[] pathExpr = source.value();
            /*
             * 格式说明 -> classpath:application.properties
             *            file:/opt/config/application.properties
             */
            for (final String path : pathExpr) {
                this.loadProperties(path);
            }
        }
        // 打印环境变量
        log.info("[ ZERO ] 运行环境：{}", reportOfEnvironment());
    }

    private void loadProperties(final String path) {
        if (Ut.isNil(path)) {
            log.warn("[ Zero ] 配置路径为空，跳过加载");
            return;
        }

        final Properties properties = new Properties();

        if (path.startsWith("classpath:")) {
            this.loadFromClasspath(path.substring("classpath:".length()), properties);
        } else if (path.startsWith("file:")) {
            this.loadFromFile(path.substring("file:".length()), properties);
        } else {
            // 默认按 classpath 处理
            this.loadFromClasspath(path, properties);
        }

        // 将属性设置到 System.setProperty 和内部缓存
        for (final String key : properties.stringPropertyNames()) {
            final String value = properties.getProperty(key);
            if (Ut.isNotNil(value)) {
                System.setProperty(key, value);
                ENV_VARS.put(key, value);
            }
        }
    }

    private void loadFromClasspath(final String resourcePath, final Properties properties) {
        Fn.jvmAt(() -> {
            try (final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resourcePath)) {
                if (inputStream != null) {
                    properties.load(inputStream);
                    log.info("[ Zero ] 从 classpath 加载配置文件: {}", resourcePath);
                } else {
                    log.warn("[ Zero ] 未找到配置文件: {}", resourcePath);
                }
            }
        });
    }

    private void loadFromFile(final String filePath, final Properties properties) {
        Fn.jvmAt(() -> {
            try (final InputStream inputStream = java.nio.file.Files.newInputStream(java.nio.file.Paths.get(filePath))) {
                properties.load(inputStream);
                log.info("[ Zero ] 从文件系统加载配置文件: {}", filePath);
            }
        });
    }

    @Override
    public String get(final String key) {
        if (Ut.isNil(key)) {
            return null;
        }

        // 优先从环境变量读取
        final String envValue = System.getenv(key);
        if (Ut.isNotNil(envValue)) {
            return envValue;
        }

        // 然后从系统属性读取
        final String sysValue = System.getProperty(key);
        if (Ut.isNotNil(sysValue)) {
            return sysValue;
        }

        // 最后从内部缓存读取
        return ENV_VARS.get(key);
    }

    @Override
    public Integer getInt(final String key) {
        final String value = this.get(key);
        if (Ut.isNotNil(value)) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                log.warn("[ Zero ] 配置项 '{}' 的值 '{}' 不是有效的整数", key, value);
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Set<String> vars() {
        return ENV_VARS.keySet();
    }


    // 环境变量打印专用
    private static String reportOfEnvironment() {
        final StringBuilder content = new StringBuilder();

        content.append("\n======= Zero Framework 环境变量 =======\n");

        final HEnvironment environment = ZeroEnvironment.of();
        Arrays.stream(EnvironmentVariable.NAMES).filter(StrUtil::isNotEmpty).forEach(name -> {
            final String value = environment.get(name);
            if (value != null) {
                content.append("\t").append(name).append(" = ").append(value).append("\n");
            }
        });

        content.append("======================================\n");

        return content.toString();
    }
}
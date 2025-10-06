package io.zerows.epoch.configuration;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.zerows.epoch.annotations.boot.PropertySource;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.specification.configuration.HEnvironment;
import io.zerows.support.Ut;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

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

    // ========== Lifecycle ==========

    public void whenStart(final Class<?> clazz) {
        final PropertySource source = clazz.getDeclaredAnnotation(PropertySource.class);
        if (source != null) {
            for (final String path : source.value()) {
                this.loadProperties(path);
            }
        }
        // 打印环境变量
        log.info("[ ZERO ] 运行环境：{}", reportOfEnvironment());
    }

    // ========== Loaders (Public entry) ==========

    private void loadProperties(final String path) {
        if (Ut.isNil(path)) {
            log.warn("[ ZERO ] 配置路径为空，跳过加载");
            return;
        }
        final Properties props = new Properties();
        final List<ResourceCandidate> candidates = this.candidatesFor(path);
        boolean loaded = false;

        for (final ResourceCandidate c : candidates) {
            loaded = this.tryLoadAndApply(c, props);
            if (loaded) {
                break;
            }
        }
    }

    // ========== Candidate builder ==========

    /**
     * 按协议构建候选资源列表，按序尝试。
     */
    private List<ResourceCandidate> candidatesFor(final String path) {
        final List<ResourceCandidate> list = new ArrayList<>();

        if (path.startsWith("classpath:")) {
            final String p = path.substring("classpath:".length());
            list.add(this.classpathCandidate(p, "[ ZERO ] 从 classpath 加载配置文件: " + p,
                "[ ZERO ] 未找到配置文件: " + p));
        } else if (path.startsWith("file:")) {
            final String p = path.substring("file:".length());
            list.add(this.fileCandidate(p, "[ ZERO ] 从文件系统加载配置文件: " + p,
                "[ ZERO ] 未找到文件配置: " + p));
        } else if (path.startsWith("test:")) {
            final String p = path.substring("test:".length());
            // 1) src/test/resources/ 前缀
            final String withPrefix = "src/test/resources/" + p;
            list.add(this.classpathCandidate(withPrefix,
                "[ ZERO ] 从测试资源目录加载配置文件: " + withPrefix,
                null)); // 若失败，再尝试无前缀
            // 2) 不带前缀（与原逻辑一致）
            list.add(this.classpathCandidate(p,
                "[ ZERO ] 从测试资源目录加载配置文件: " + p,
                "[ ZERO ] 未找到测试配置文件: src/test/resources/" + p));
        } else {
            // 默认按 classpath 处理
            list.add(this.classpathCandidate(path, "[ ZERO ] 从 classpath 加载配置文件: " + path,
                "[ ZERO ] 未找到配置文件: " + path));
        }
        return list;
    }

    // ========== Small helpers ==========

    @AllArgsConstructor
    private static class ResourceCandidate {
        Supplier<InputStream> supplier;
        String successLog;   // 成功加载时的 info 日志
        String notFoundWarn; // 找不到时的 warn（允许为 null：表示由后续候选继续处理）
    }

    private ResourceCandidate classpathCandidate(final String resourcePath,
                                                 final String successInfo,
                                                 final String notFoundWarn) {
        return new ResourceCandidate(
            () -> Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath),
            successInfo,
            notFoundWarn
        );
    }

    private ResourceCandidate fileCandidate(final String filePath,
                                            final String successInfo,
                                            final String notFoundWarn) {
        return new ResourceCandidate(
            () -> {
                try {
                    return Files.newInputStream(Paths.get(filePath));
                } catch (final Exception ignore) {
                    return null;
                }
            },
            successInfo,
            notFoundWarn
        );
    }

    /**
     * 统一封装：尝试通过候选获取流→加载→写入系统与缓存→日志
     */
    private boolean tryLoadAndApply(final ResourceCandidate candidate, final Properties props) {
        return Fn.jvmOr(() -> {
            try (final InputStream in = candidate.supplier.get()) {
                if (in == null) {
                    if (candidate.notFoundWarn != null) {
                        log.warn(candidate.notFoundWarn);
                    }
                    return false;
                }
                props.load(in);
                log.info(candidate.successLog);
                this.applyProperties(props);
                return true;
            } catch (final Exception e) {
                // 保持安静失败：外层还有其它候选；仅在这里给出 debug 方便排查
                log.debug("[ ZERO ] 配置加载异常：{}", e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * Properties → System.setProperty & 内部缓存
     */
    private void applyProperties(final Properties properties) {
        for (final String key : properties.stringPropertyNames()) {
            final String value = properties.getProperty(key);
            if (Ut.isNotNil(value)) {
                System.setProperty(key, value);
                ENV_VARS.put(key, value);
            }
        }
    }

    // ========== HEnvironment ==========

    @Override
    public String get(final String key) {
        if (Ut.isNil(key)) {
            return null;
        }

        final String envValue = System.getenv(key);
        if (Ut.isNotNil(envValue)) {
            return envValue;
        }

        final String sysValue = System.getProperty(key);
        if (Ut.isNotNil(sysValue)) {
            return sysValue;
        }

        return ENV_VARS.get(key);
    }

    @Override
    public Integer getInt(final String key) {
        final String value = this.get(key);
        if (Ut.isNotNil(value)) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                log.warn("[ ZERO ] 配置项 '{}' 的值 '{}' 不是有效的整数", key, value);
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Set<String> vars() {
        return ENV_VARS.keySet();
    }

    // ========== Reporter ==========

    // 环境变量打印专用
    private static String reportOfEnvironment() {
        final StringBuilder content = new StringBuilder();
        content.append("\n======= Zero Framework 环境变量 =======\n");

        final HEnvironment environment = ZeroEnvironment.of();
        Arrays.stream(EnvironmentVariable.NAMES)
            .filter(StrUtil::isNotEmpty)
            .forEach(name -> {
                final String value = environment.get(name);
                if (value != null) {
                    content.append("\t").append(name).append(" = ").append(value).append("\n");
                }
            });

        content.append("======================================\n");
        return content.toString();
    }
}

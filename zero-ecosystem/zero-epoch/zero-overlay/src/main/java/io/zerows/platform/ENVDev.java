package io.zerows.platform;

import io.r2mo.function.Fn;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 开发专用环境变量加载器
 *
 * @author lang : 2025-12-30
 */
@Slf4j
class ENVDev {

    static final ENVDev INSTANCE = new ENVDev();

    private ENVDev() {

    }

    public static ENVDev of() {
        return INSTANCE;
    }

    Map<String, String> envLoad(final String path) {
        if (UtBase.isNil(path)) {
            log.warn("[ ZERO ] 配置路径为空，跳过加载");
            return Map.of();        // 直接返回空
        }
        final Properties props = new Properties();
        final List<ENVCandidate> candidates = this.envList(path);
        final Map<String, String> mapLoad = new HashMap<>();

        for (final ENVCandidate c : candidates) {
            final Map<String, String> mapApplied = this.envApply(props, c);
            if (!mapApplied.isEmpty()) {
                mapLoad.putAll(mapApplied);
                break;
            }
        }
        return mapLoad;
    }

    /**
     * 统一封装：尝试通过候选获取流→加载→写入系统与缓存→日志
     */
    private Map<String, String> envApply(final Properties props, final ENVCandidate candidate) {
        return Fn.jvmOr(() -> {
            try (final InputStream in = candidate.supplier.get()) {
                if (in == null) {
                    if (candidate.notFoundWarn != null) {
                        log.warn(candidate.notFoundWarn);
                    }
                    return Map.of();
                }
                props.load(in);
                log.info(candidate.successLog);
                return this.envApply(props);
            } catch (final Exception e) {
                // 保持安静失败：外层还有其它候选；仅在这里给出 debug 方便排查
                log.debug("[ ZERO ] 配置加载异常：{}", e.getMessage(), e);
                return Map.of();
            }
        });
    }


    /**
     * Properties → System.setProperty & 内部缓存
     */
    private Map<String, String> envApply(final Properties properties) {
        final Map<String, String> envMap = new HashMap<>();
        for (final String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);

            if (UtBase.isNotNil(value)) {
                // ---------------------------------------------------------
                // [新增逻辑] 去除首尾双引号
                // 场景：配置文件中写了 KEY="VALUE" 或 KEY="http://..."
                // ---------------------------------------------------------
                value = value.trim(); // 建议先 trim 去除可能存在的空白符
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }


                envMap.put(key, value);
                // 外层双写
                // System.setProperty(key, value);
                // ENV_VARS.put(key, value);
            }
        }
        return envMap;
    }

    /**
     * 按协议构建候选资源列表，按序尝试。
     */
    private List<ENVCandidate> envList(final String path) {
        final List<ENVCandidate> list = new ArrayList<>();

        if (path.startsWith("classpath:")) {
            final String p = path.substring("classpath:".length());
            list.add(this.envClasspath(p, "[ ZERO ] 从 classpath 加载配置文件: " + p,
                "[ ZERO ] 未找到配置文件 / CLASSPATH: " + p));
        } else if (path.startsWith("file:")) {
            final String p = path.substring("file:".length());
            list.add(this.envFile(p, "[ ZERO ] 从文件系统加载配置文件: " + p,
                "[ ZERO ] 未找到文件配置 / FILE: " + p));
        } else if (path.startsWith("test:")) {
            final String p = path.substring("test:".length());
            // 1) src/test/resources/ 前缀
            final String withPrefix = "src/test/resources/" + p;
            list.add(this.envClasspath(withPrefix,
                "[ ZERO ] 从测试资源目录加载配置文件: " + withPrefix,
                null)); // 若失败，再尝试无前缀
            // 2) 不带前缀（与原逻辑一致）
            list.add(this.envClasspath(p,
                "[ ZERO ] 从测试资源目录加载配置文件: " + p,
                "[ ZERO ] 未找到测试配置文件: src/test/resources/" + p));
        } else {
            // 默认按 classpath 处理
            list.add(this.envClasspath(path, "[ ZERO ] 从 classpath 加载配置文件: " + path,
                "[ ZERO ] 未找到配置文件 / ELSE: " + path));
        }
        return list;
    }

    private ENVCandidate envClasspath(final String resourcePath,
                                      final String successInfo,
                                      final String notFoundWarn) {
        return new ENVCandidate(
            () -> Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath),
            successInfo,
            notFoundWarn
        );
    }

    private ENVCandidate envFile(final String filePath,
                                 final String successInfo,
                                 final String notFoundWarn) {
        return new ENVCandidate(
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
}

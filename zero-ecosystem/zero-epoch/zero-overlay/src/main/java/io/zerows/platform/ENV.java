package io.zerows.platform;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.zerows.platform.annotations.PropertySource;
import io.zerows.specification.configuration.HEnvironment;
import io.zerows.specification.development.HLog;
import io.zerows.support.base.UtBase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lang : 2025-10-05
 */
@Slf4j
public class ENV implements HEnvironment, HLog {

    private static final ConcurrentMap<String, String> ENV_VARS = new ConcurrentHashMap<>();
    private static final ENV INSTANCE = new ENV();

    // MySQL 默认连接参数
    private static final String MYSQL_PARAMS = "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true";

    private ENV() {
        // 私有构造函数，防止外部实例化
    }

    public static ENV of() {
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

        // [新增] 自动计算 JDBC URL
        this.autoCalculateUrls();

        // 打印环境变量
        this.vLog();
    }

    // ========== Auto Calculation (New Added) ==========

    /**
     * 自动计算 JDBC URL
     * 根据 Z_DB_TYPE, Z_DB_HOST, Z_DB_PORT 以及各实例名自动生成 Z_DBx_URL
     */
    private void autoCalculateUrls() {
        // 1. 检查数据库类型，目前仅自动处理 MYSQL
        // 使用常量 EnvironmentVariable.DB_TYPE
        final String dbType = this.get(EnvironmentVariable.DB_TYPE);
        if (!"MYSQL".equalsIgnoreCase(dbType)) {
            log.debug("[ ZERO ] 数据库类型为 [{}]，跳过 MySQL URL 自动计算。", dbType);
            return;
        }

        // 2. 获取基础连接信息
        // 使用常量 EnvironmentVariable.DB_HOST / DB_PORT
        final String host = this.get(EnvironmentVariable.DB_HOST);
        final String port = this.get(EnvironmentVariable.DB_PORT);

        if (UtBase.isNil(host) || UtBase.isNil(port)) {
            log.warn("[ ZERO ] 缺少 {} 或 {} 配置，跳过 URL 计算。",
                EnvironmentVariable.DB_HOST, EnvironmentVariable.DB_PORT);
            return;
        }

        // 3. 定义需要计算的映射关系: { 生成的目标KEY : 依赖的实例名KEY }
        final Map<String, String> targetMapping = new LinkedHashMap<>();
        // 使用常量替换字符串 "Z_DBS_URL" -> DBS_URL 等
        targetMapping.put(EnvironmentVariable.DBS_URL, EnvironmentVariable.DBS_INSTANCE); // 业务库
        targetMapping.put(EnvironmentVariable.DBW_URL, EnvironmentVariable.DBW_INSTANCE); // 工作流库
        targetMapping.put(EnvironmentVariable.DBH_URL, EnvironmentVariable.DBH_INSTANCE); // 历史库

        // 4. 遍历计算
        targetMapping.forEach((urlKey, instanceKey) -> {
            // 如果已经被显式配置过，则不覆盖
            if (this.contains(urlKey)) {
                return;
            }

            final String instanceName = this.get(instanceKey);
            if (UtBase.isNotNil(instanceName)) {
                // 格式: jdbc:mysql://host:port/instance?params
                final String url = StrUtil.format("jdbc:mysql://{}:{}/{}{}",
                    host, port, instanceName, MYSQL_PARAMS);

                // 写入系统属性和缓存
                System.setProperty(urlKey, url);
                ENV_VARS.put(urlKey, url);
                log.info("[ ZERO ] 自动生成数据库 URL: {} = {}", urlKey, url);
            }
        });
    }

    /**
     * 辅助判断是否存在 key
     */
    private boolean contains(final String key) {
        return System.getenv().containsKey(key) ||
            System.getProperties().containsKey(key) ||
            ENV_VARS.containsKey(key);
    }

    // ========== Loaders (Public entry) ==========

    private void loadProperties(final String path) {
        if (UtBase.isNil(path)) {
            log.warn("[ ZERO ] 配置路径为空，跳过加载");
            return;
        }
        final Properties props = new Properties();
        final List<ResourceCandidate> candidates = this.candidatesFor(path);
        boolean loaded;

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

                System.setProperty(key, value);
                ENV_VARS.put(key, value);
            }
        }
    }

    // ========== HEnvironment ==========

    @Override
    public String get(final String key) {
        if (UtBase.isNil(key)) {
            return null;
        }

        final String envValue = System.getenv(key);
        if (UtBase.isNotNil(envValue)) {
            return envValue;
        }

        final String sysValue = System.getProperty(key);
        if (UtBase.isNotNil(sysValue)) {
            return sysValue;
        }
        return ENV_VARS.get(key);
    }

    @Override
    public Set<String> vars() {
        return ENV_VARS.keySet();
    }

    // ========== 特殊处理：增强版变量解析 ==========

    public static String parseVariable(final String wrapValue) {
        if (UtBase.isNil(wrapValue)) {
            return wrapValue;
        }

        // 定义非贪婪匹配的正则表达式：${KEY}
        // [^}] 表示匹配除了右花括号外的所有字符，确保一行内多个变量能被正确分割
        final String regex = "\\$\\{([^}]+)\\}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(wrapValue);
        final StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            // 获取 ${KEY} 中的 KEY
            final String envKey = matcher.group(1);

            // 尝试获取环境变量
            String envValue = ENV.of().get(envKey, (String) null);

            if (UtBase.isNotNil(envValue)) {
                log.info("[ ZERO ] 解析环境变量：{} -> {}", envKey, envValue);
            } else {
                // 策略：如果没找到环境变量，保留原样 "${KEY}"，方便后续排查或由其他层级处理
                envValue = "${" + envKey + "}";
                log.warn("[ ZERO ] 未设置环境变量：{}，保持原样", envKey);
            }

            // 执行替换 (使用 quoteReplacement 防止 envValue 中包含 $ 或 \ 等特殊字符导致报错)
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(envValue));
        }

        // 将剩余未匹配的字符串拼接到尾部
        matcher.appendTail(buffer);

        return buffer.toString();
    }
    // ========== Reporter ==========

    // 环境变量打印专用
    @Override
    @SuppressWarnings("unchecked")
    public ENV vLog() {
        final StringBuilder content = new StringBuilder();
        content.append("\n======= Zero Framework 环境变量 =======\n");

        final HEnvironment environment = ENV.of();

        // 1. 打印 EnvironmentVariable.NAMES 中定义的标准变量
        Arrays.stream(EnvironmentVariable.NAMES)
            .filter(StrUtil::isNotEmpty)
            .forEach(name -> {
                final String value = environment.get(name);
                if (value != null) {
                    content.append("\t").append(name).append(" = ").append(value).append("\n");
                }
            });

        // 2. 额外打印自动生成的 URL (因为 NAMES 数组中不包含 URL 的常量定义，只有 INSTANCE)
        // 使用常量 EnvironmentVariable.DBS_URL / DBW_URL / DBH_URL
        Arrays.asList(
            EnvironmentVariable.DBS_URL,
            EnvironmentVariable.DBW_URL,
            EnvironmentVariable.DBH_URL
        ).forEach(name -> {
            final String value = environment.get(name);
            if (value != null) {
                content.append("\t[Auto] ").append(name).append(" = ").append(value).append("\n");
            }
        });

        content.append("======================================\n");
        final String message = content.toString();
        log.info("[ ZERO ] 运行环境：{}", message);
        return this;
    }
}
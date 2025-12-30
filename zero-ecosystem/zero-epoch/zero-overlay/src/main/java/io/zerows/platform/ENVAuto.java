package io.zerows.platform;

import cn.hutool.core.util.StrUtil;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 辅助工具类
 *
 * @author lang : 2025-12-30
 */
@Slf4j
class ENVAuto {

    // MySQL 默认连接参数
    private static final String MYSQL_PARAMS = "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true";

    static Set<String> whenAuto() {
        final Set<String> names = new TreeSet<>();
        names.addAll(System.getenv().keySet());
        names.addAll(System.getProperties().stringPropertyNames());
        names.addAll(List.of(EnvironmentVariable.NAMES));
        return names.stream().filter(
            item -> item.startsWith("Z_") ||
                item.startsWith("R2MO_") ||
                item.startsWith("AEON_")
        ).collect(Collectors.toSet());
    }

    /**
     * 自动计算 JDBC URL
     * 根据 Z_DB_TYPE, Z_DB_HOST, Z_DB_PORT 以及各实例名自动生成 Z_DBx_URL
     */
    static Map<String, String> whenAuto(final Function<String, String> envFn,
                                        final Predicate<String> containFn) {
        // 1. 检查数据库类型，目前仅自动处理 MYSQL
        // 使用常量 EnvironmentVariable.DB_TYPE
        final String dbType = envFn.apply(EnvironmentVariable.DB_TYPE);
        if (!"MYSQL".equalsIgnoreCase(dbType)) {
            log.debug("[ ZERO ] 数据库类型为 [{}]，跳过 MySQL URL 自动计算。", dbType);
            return Map.of();
        }

        // 2. 获取基础连接信息
        // 使用常量 EnvironmentVariable.DB_HOST / DB_PORT
        final String host = envFn.apply(EnvironmentVariable.DB_HOST);
        final String port = envFn.apply(EnvironmentVariable.DB_PORT);

        if (UtBase.isNil(host) || UtBase.isNil(port)) {
            log.warn("[ ZERO ] 缺少 {} 或 {} 配置，跳过 URL 计算。",
                EnvironmentVariable.DB_HOST, EnvironmentVariable.DB_PORT);
            return Map.of();
        }

        // 3. 定义需要计算的映射关系: { 生成的目标KEY : 依赖的实例名KEY }
        final Map<String, String> targetMapping = new LinkedHashMap<>();
        // 使用常量替换字符串 "Z_DBS_URL" -> DBS_URL 等
        targetMapping.put(EnvironmentVariable.DBS_URL, EnvironmentVariable.DBS_INSTANCE); // 业务库
        targetMapping.put(EnvironmentVariable.DBW_URL, EnvironmentVariable.DBW_INSTANCE); // 工作流库
        targetMapping.put(EnvironmentVariable.DBH_URL, EnvironmentVariable.DBH_INSTANCE); // 历史库

        // 4. 遍历计算
        final Map<String, String> envMap = new HashMap<>();
        targetMapping.forEach((urlKey, instanceKey) -> {
            // 如果已经被显式配置过，则不覆盖
            if (containFn.test(urlKey)) {
                return;
            }

            final String instanceName = envFn.apply(instanceKey);
            if (UtBase.isNotNil(instanceName)) {
                // 格式: jdbc:mysql://host:port/instance?params
                final String url = StrUtil.format("jdbc:mysql://{}:{}/{}{}",
                    host, port, instanceName, MYSQL_PARAMS);

                // 写入系统属性和缓存
                envMap.put(urlKey, url);
                log.info("[ ZERO ] 自动生成数据库 URL: {} = {}", urlKey, url);
            }
        });
        return envMap;
    }

    static String parseEnv(final String wrapValue, final BiFunction<String, String, String> envFn) {
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
            String envValue = envFn.apply(envKey, null);

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
}

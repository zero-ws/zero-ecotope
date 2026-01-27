package io.zerows.epoch.boot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.zerows.epoch.spec.YmLogging;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author lang : 2025-10-06
 */
public class ZeroLogging {

    /**
     * 初始化 SLF4J 日志级别（通过 Logback 实现）
     * 只处理包级别的日志配置，不处理 root 日志级别
     *
     * @param logging 配置对象
     */
    public static void configure(final YmLogging logging) {
        if (logging == null) {
            return;
        }

        // 获取日志配置映射
        final Map<String, String> loggingConfig = logging.getLevel();
        if (loggingConfig == null || loggingConfig.isEmpty()) {
            return;
        }

        // 获取 LoggerContext
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // 遍历配置并设置对应的日志级别（只处理包级别的配置）
        for (final Map.Entry<String, String> entry : loggingConfig.entrySet()) {
            final String packageName = entry.getKey();
            final String levelStr = entry.getValue();

            if (packageName != null && levelStr != null) {
                // 跳过 root 相关的配置
                if ("root".equalsIgnoreCase(packageName) || packageName.trim().isEmpty()) {
                    continue;
                }

                configure(loggerContext, packageName, levelStr);
            }
        }

        // 检查是否配置了当前类或当前包的日志级别
        boolean currentClassConfigured = false;
        for (final Map.Entry<String, String> entry : loggingConfig.entrySet()) {
            final String packageName = entry.getKey();
            if (ZeroLogging.class.getName().startsWith(packageName)) {
                currentClassConfigured = true;
                break;
            }
        }

        // 如果当前类或包没有被配置，或者当前类的级别仍为 null，设置为 DEBUG
        final Logger currentClassLogger = loggerContext.getLogger(ZeroLogging.class.getName());
        if (!currentClassConfigured || currentClassLogger.getLevel() == null) {
            currentClassLogger.setLevel(Level.DEBUG);
        }

        // 打印所有设置的日志级别
        final Logger logger = loggerContext.getLogger(ZeroLogging.class.getName());
        logger.info("[ ZERO ] 日志级别配置详情：");
        for (final Map.Entry<String, String> entry : loggingConfig.entrySet()) {
            final String packageName = entry.getKey();
            final String levelStr = entry.getValue();
            if (packageName != null && levelStr != null && !"root".equalsIgnoreCase(packageName) && !packageName.trim().isEmpty()) {
                logger.info(String.format("[ ZERO ]    %48s -> %s", packageName, levelStr));
            }
        }
    }

    /**
     * 设置指定包名的日志级别
     *
     * @param loggerContext Logger 上下文
     * @param packageName   包名
     * @param levelStr      日志级别字符串（如 DEBUG, INFO, WARN, ERROR, TRACE）
     */
    private static void configure(final LoggerContext loggerContext, final String packageName, final String levelStr) {
        try {
            // 将字符串转换为 Level 枚举
            final Level level = Level.valueOf(levelStr.toUpperCase());

            // 获取或创建对应的 Logger 并设置级别
            final Logger logger = loggerContext.getLogger(packageName);
            logger.setLevel(level);
        } catch (final IllegalArgumentException e) {
            // 如果日志级别无效，记录错误
            final Logger defaultLogger = loggerContext.getLogger(ZeroLogging.class.getName());
            defaultLogger.error("[ ZERO ] 无效日志级别: {} 包名: {}", levelStr, packageName, e);
        }
    }
}
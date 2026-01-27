package io.zerows.support.base;

import cn.hutool.core.date.DateUtil;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.lib.fn.ELFunctionDefinition;
import io.zerows.platform.ENV;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YAML 编译器与占位符解析器
 * <p>
 * 核心功能：
 * 1. 动态替换 YAML 中的 ${VAR} 占位符。
 * 2. 支持两轮解析：先解析环境变量，再解析内部引用。
 * 3. 自动清洗配置值中的多余引号，防止 Nacos 连接失败。
 * 4. 线程安全的 SnakeYAML 使用方式。
 * </p>
 *
 * @author lang : 2025-12-18
 */
class UCompiler {

    // Pattern 是线程安全的，保持 static
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Jinjava JINJAVA = new Jinjava();

    static {
        // 注册全局函数，如 R2_NOW()
        JINJAVA.getGlobalContext().registerFunction(
            new ELFunctionDefinition("", "R2_NOW", DateUtil.class, "now")
        );
    }

    /**
     * 编译 YAML 字符串，解析其中的占位符
     *
     * @param input 原始 YAML 内容
     * @return 解析后的 YAML 内容
     */
    static String compileYml(final String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // 1. 创建局部的 Yaml 实例（SnakeYAML 非线程安全，必须局部创建）
        final Yaml yamlLoader = new Yaml(new SafeConstructor(new LoaderOptions()));

        // 2. 解析所有文档并合并为一个 Map
        final Map<String, Object> merged = new LinkedHashMap<>();
        for (final Object doc : yamlLoader.loadAll(input)) {
            if (doc instanceof Map) {
                //noinspection unchecked
                mergeMaps(merged, (Map<String, Object>) doc);
            }
        }

        // 3. 第一轮解析：解析安全表达式 (环境变量)
        // safeOnly=true，遇到未定义的变量跳过不报错，留给下一轮或保留原样
        final Object firstPass = resolvePlaceholders(merged, true);

        // 4. 构建全局字面量上下文 (用于解决内部引用，如 ${config.namespace})
        final Map<String, String> globalContext = new HashMap<>();
        extractLiteralValues(firstPass, "", globalContext);

        // 5. 第二轮解析：解析剩余占位符 (使用 Context)
        final Object secondPass = resolvePlaceholdersWithContext(firstPass, globalContext);

        // 6. 配置输出选项
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 块状输出，易读
        dumperOptions.setIndent(2);
        // 🟢 关键配置：使用 PLAIN 风格，尽量不给字符串加引号
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        // 7. 输出为单文档 YAML
        return new Yaml(dumperOptions).dump(secondPass);
    }

    /**
     * Ansible 风格的模板渲染
     */
    static String compileAnsible(final String content) {
        final ENV env = ENV.of();
        final Map<String, Object> params = new HashMap<>();
        env.vars().forEach(name -> params.put(name, env.get(name)));
        return JINJAVA.render(content, params);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 递归合并两个 Map
     */
    @SuppressWarnings("unchecked")
    private static void mergeMaps(final Map<String, Object> target, final Map<String, Object> source) {
        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (target.containsKey(key) && target.get(key) instanceof Map && value instanceof Map) {
                mergeMaps((Map<String, Object>) target.get(key), (Map<String, Object>) value);
            } else {
                target.put(key, value);
            }
        }
    }

    /**
     * 递归解析对象中的占位符 (无上下文)
     */
    private static Object resolvePlaceholders(final Object obj, final boolean safeOnly) {
        if (obj instanceof String) {
            return resolveStringPlaceholders((String) obj, safeOnly);
        } else if (obj instanceof Map) {
            final Map<Object, Object> newMap = new LinkedHashMap<>();
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                newMap.put(entry.getKey(), resolvePlaceholders(entry.getValue(), safeOnly));
            }
            return newMap;
        } else if (obj instanceof List) {
            final List<Object> newList = new ArrayList<>();
            for (final Object item : (List<?>) obj) {
                newList.add(resolvePlaceholders(item, safeOnly));
            }
            return newList;
        }
        return obj;
    }

    /**
     * 解析单个字符串中的占位符 (无上下文)
     */
    private static String resolveStringPlaceholders(final String value, final boolean safeOnly) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        // 注意：在 Java 9+ 中可以使用 StringBuilder，旧版本需用 StringBuffer
        final StringBuffer sb = new StringBuffer();
        boolean changed = false;
        while (matcher.find()) {
            final String content = matcher.group(1);
            String resolved;
            try {
                resolved = resolvePlaceholder(content, safeOnly, null);
            } catch (final IllegalArgumentException e) {
                if (safeOnly) {
                    // 安全模式下保留原样
                    resolved = matcher.group(0);
                } else {
                    throw e;
                }
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(resolved));
            changed = true;
        }
        if (changed) {
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

    /**
     * 递归解析对象中的占位符 (带上下文)
     */
    private static Object resolvePlaceholdersWithContext(final Object obj, final Map<String, String> context) {
        if (obj instanceof String) {
            return resolveStringWithContext((String) obj, context);
        } else if (obj instanceof Map) {
            final Map<Object, Object> newMap = new LinkedHashMap<>();
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                newMap.put(entry.getKey(), resolvePlaceholdersWithContext(entry.getValue(), context));
            }
            return newMap;
        } else if (obj instanceof List) {
            final List<Object> newList = new ArrayList<>();
            for (final Object item : (List<?>) obj) {
                newList.add(resolvePlaceholdersWithContext(item, context));
            }
            return newList;
        }
        return obj;
    }

    /**
     * 解析单个字符串中的占位符 (带上下文)
     */
    private static String resolveStringWithContext(final String value, final Map<String, String> context) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String content = matcher.group(1);
            // 这里非 safeOnly，如果找不到会报错（或者返回 null 视逻辑而定，此处沿用原逻辑）
            final String resolved = resolvePlaceholder(content, false, context);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(resolved));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 核心解析逻辑：查找变量值并清洗
     */
    private static String resolvePlaceholder(final String content, final boolean safeOnly, final Map<String, String> context) {
        final int colonIndex = content.indexOf(':');
        final String key = colonIndex != -1 ? content.substring(0, colonIndex) : content;
        final String defaultValue = colonIndex != -1 ? content.substring(colonIndex + 1) : null;

        // 1. 查环境变量
        String value = ENV.of().get(key);
        if (value != null) {
            return clean(value); // 🟢 净化
        }

        // 2. 查默认值
        if (defaultValue != null) {
            return clean(defaultValue); // 🟢 净化
        }

        // 3. 查上下文 (内部引用)
        if (!safeOnly && context != null) {
            value = context.get(key);
            if (value != null) {
                return clean(value); // 🟢 净化
            }
        }

        // 4. 无法解析的处理
        if (safeOnly) {
            throw new IllegalArgumentException("skip"); // 抛出异常由上层捕获，保留原占位符
        } else {
            throw new IllegalArgumentException("[ ZERO ] 占位符 '${" + content + "}' 无法解析，变量 '" + key + "' 未定义或输入丢失。");
        }
    }

    /**
     * 提取字面量值构建上下文 (Flatten)
     */
    private static void extractLiteralValues(final Object obj, final String prefix, final Map<String, String> context) {
        if (obj instanceof Map) {
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                final String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
                extractLiteralValues(entry.getValue(), key, context);
            }
        } else if (obj instanceof List) {
            // List 内部通常不作为引用源，跳过
        } else {
            final String value = String.valueOf(obj);
            // 只有不包含占位符的确切值才放入上下文
            if (!value.contains("${")) {
                context.put(prefix, value);
            }
        }
    }

    /**
     * 🟢 净化值：循环去除首尾的引号
     * 解决 export NS='"value"' 导致的解析错误
     */
    private static String clean(final String value) {
        if (value == null) {
            return null;
        }
        String result = value.trim();
        // 循环去除，防止多层引号 '"value"'
        while ((result.startsWith("\"") && result.endsWith("\"")) ||
            (result.startsWith("'") && result.endsWith("'"))) {
            if (result.length() < 2) {
                break;
            }
            result = result.substring(1, result.length() - 1);
        }
        return result;
    }
}
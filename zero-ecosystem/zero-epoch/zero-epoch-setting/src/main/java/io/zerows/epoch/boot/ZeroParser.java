package io.zerows.epoch.boot;

import io.zerows.epoch.configuration.ZeroEnvironment;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
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

class ZeroParser {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Yaml YAML_PARSER = new Yaml(new SafeConstructor(new LoaderOptions()));
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();

    static {
        DUMPER_OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
        DUMPER_OPTIONS.setIndent(2);
    }

    static String compile(final String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // 1. 解析所有文档并合并为一个 Map
        final Map<String, Object> merged = new LinkedHashMap<>();
        for (final Object doc : YAML_PARSER.loadAll(input)) {
            if (doc instanceof Map) {
                mergeMaps(merged, (Map<String, Object>) doc);
            }
        }

        // 2. 第一轮：解析安全表达式
        final Object firstPass = resolvePlaceholders(merged, true);

        // 3. 构建全局字面量上下文
        final Map<String, String> globalContext = new HashMap<>();
        extractLiteralValues(firstPass, "", globalContext);

        // 4. 第二轮：解析剩余占位符
        final Object secondPass = resolvePlaceholdersWithContext(firstPass, globalContext);

        // 5. 输出为单文档 YAML
        return new Yaml(DUMPER_OPTIONS).dump(secondPass);
    }

    // ———————— 合并两个 Map（递归）———————
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

    // ———————— 第一轮：只处理 "安全" 表达式 ————————
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

    private static String resolveStringPlaceholders(final String value, final boolean safeOnly) {
        if (value == null || !value.contains("${")) {
            return value;
        }

        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        final StringBuffer sb = new StringBuffer();
        boolean changed = false;

        while (matcher.find()) {
            final String content = matcher.group(1);
            String resolved;
            try {
                resolved = resolvePlaceholder(content, safeOnly, null);
            } catch (final IllegalArgumentException e) {
                if (safeOnly) {
                    // 第一轮：无法解析就保留原样
                    resolved = matcher.group(0); // "${...}"
                } else {
                    throw e;
                }
            }
            matcher.appendReplacement(sb, resolved.replace("\\", "\\\\").replace("$", "\\$"));
            changed = true;
        }

        if (changed) {
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

    // ———————— 第二轮：使用 safeContext ————————
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

    private static String resolveStringWithContext(final String value, final Map<String, String> context) {
        if (value == null || !value.contains("${")) {
            return value;
        }

        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        final StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            final String content = matcher.group(1);
            final String resolved = resolvePlaceholder(content, false, context);
            matcher.appendReplacement(sb, resolved.replace("\\", "\\\\").replace("$", "\\$"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // ———————— 核心解析逻辑 ————————
    private static String resolvePlaceholder(final String content, final boolean safeOnly, final Map<String, String> context) {
        final int colonIndex = content.indexOf(':');
        final String key = colonIndex != -1 ? content.substring(0, colonIndex) : content;
        final String defaultValue = colonIndex != -1 ? content.substring(colonIndex + 1) : null;

        // 1. ZeroEnvironment
        String value = ZeroEnvironment.of().get(key);
        if (value != null) {
            return value;
        }

        // 2. 如果有默认值，使用它
        if (defaultValue != null) {
            return defaultValue;
        }

        // 3. 如果不是 safeOnly 模式，且提供了 context，则查 context
        if (!safeOnly && context != null) {
            value = context.get(key);
            if (value != null) {
                return value;
            }
        }

        // 4. 无法解析
        if (safeOnly) {
            // 第一轮：不抛异常，由调用方决定保留
            throw new IllegalArgumentException("skip");
        } else {
            throw new IllegalArgumentException(
                "[ ZERO ] 占位符 '${" + content + "}' 无法解析，变量 '" + key + "' 未定义或输入丢失。"
            );
        }
    }

    // ———————— 提取字面量（无 ${} 的值） ————————
    private static void extractLiteralValues(final Object obj, final String prefix, final Map<String, String> context) {
        if (obj instanceof Map) {
            for (final Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                final String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
                extractLiteralValues(entry.getValue(), key, context);
            }
        } else if (obj instanceof List) {
            // 列表不展开（或可选展开）
        } else {
            final String value = String.valueOf(obj);
            // 只收录不包含占位符的字面量
            if (!value.contains("${")) {
                context.put(prefix, value);
            }
        }
    }
}
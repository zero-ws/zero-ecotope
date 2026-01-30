package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import io.zerows.specification.app.HApp;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 📏 统一导入规则解析器 (Unified Import Rule Parser)
 *
 * <p>
 * 负责解析 {@code vertx-boot.yml} 中定义的配置导入 DSL 字符串。
 * 支持完整的参数捕获，不丢弃任何配置意图。
 * </p>
 *
 * <pre>
 * 📝 语法格式 (Syntax DSL):
 * [optional:][protocol:]<data-id>[?key=value&key2=value2]
 *
 * 🌊 处理流程 (Pipeline):
 * 1. 🔍 Optional Check  : 识别并剥离 "optional:"。
 * 2. 🔌 Protocol Match  : 识别并剥离协议前缀。
 * 3. 🧹 Param Parsing   : <b>核心修正点</b> - 解析 URL 风格参数并存入 Meta，提取纯净 DataID。
 *
 * 🌰 示例 (Examples):
 * Input: "optional:nacos:shared.yaml?refreshEnabled=true&group=DEV"
 * Output:
 * - protocol: NACOS
 * - optional: true
 * - dataId:   "shared.yaml"
 * - params:   { "refreshEnabled": true, "group": "DEV" }
 * </pre>
 *
 * @author lang : 2025-10-06
 */
@Slf4j
class NacosRule {

    private static final NacosRule INSTANCE = new NacosRule();

    private NacosRule() {
    }

    static NacosRule of() {
        return INSTANCE;
    }

    /**
     * 🔄 批量规则解析 (Batch Parsing)
     */
    List<NacosMeta> parseRule(final List<String> imports, final HApp app) {
        if (imports == null || imports.isEmpty()) {
            return Collections.emptyList();
        }
        final List<NacosMeta> result = new ArrayList<>();
        for (final String rule : imports) {
            if (rule == null || rule.trim().isEmpty()) {
                continue;
            }
            // 此时传入的 rule 已经是处理过变量替换的纯字符串
            log.info("[ ZERO ] ( Nacos ) 解析 Nacos 配置导入规则：{}", rule);
            final NacosMeta meta = this.parseRule(rule);
            if (Objects.nonNull(meta)) {
                result.add(meta);
            }
        }
        return result;
    }

    /**
     * ⚙️ 核心解析逻辑 (Core Logic)
     *
     * @param rule 单条规则字符串
     * @return 解析后的元数据
     */
    private NacosMeta parseRule(final String rule) {
        String processed = rule.trim();
        final NacosMeta meta = new NacosMeta();

        // -------------------------------------------------------------
        // 1. 🛡️ 处理 Optional
        // -------------------------------------------------------------
        if (processed.startsWith("optional:")) {
            meta.setOptional(true);
            processed = processed.substring("optional:".length());
        } else {
            meta.setOptional(false);
        }

        // -------------------------------------------------------------
        // 2. 🔌 动态协议匹配 (Dynamic Protocol Matching)
        // -------------------------------------------------------------
        boolean protocolMatched = false;
        for (final ConfigProtocol protocol : ConfigProtocol.values()) {
            final String prefix = protocol.getPrefix();
            if (processed.startsWith(prefix)) {
                meta.setProtocol(protocol);
                processed = processed.substring(prefix.length());
                protocolMatched = true;
                break;
            }
        }
        if (!protocolMatched) {
            meta.setProtocol(ConfigProtocol.NACOS);
        }

        // -------------------------------------------------------------
        // 3. 🧹 参数解析与提取 (Param Parsing & Extraction)
        // Input: "data-id.yaml?refreshEnabled=true&group=B"
        // -------------------------------------------------------------
        if (processed.contains("?")) {
            // 分割 DataID 和 QueryString
            final String[] parts = processed.split("\\?", 2);
            // Part 0: 纯净的 DataID
            processed = parts[0];

            // Part 1: 参数解析
            if (parts.length > 1 && !parts[1].isEmpty()) {
                this.parseQueryParams(parts[1], meta.getParams());
            }
        }

        // -------------------------------------------------------------
        // 4. ✅ 结果构建
        // -------------------------------------------------------------
        if (processed.isEmpty()) {
            return null;
        }

        meta.setDataId(processed);
        return meta;
    }

    /**
     * 🛠️ 辅助方法：解析 Query String
     *
     * @param queryString 例如 "refreshEnabled=true&timeout=3000"
     * @param target      目标 JsonObject
     */
    private void parseQueryParams(final String queryString, final JsonObject target) {
        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key;
            final String value;

            if (idx > 0) {
                key = pair.substring(0, idx);
                value = pair.substring(idx + 1);
            } else {
                // 处理无值参数，如 "?refreshEnabled" 默认为 true
                key = pair;
                value = "true";
            }

            // 类型推断：尝试将 "true"/"false" 转为 boolean，数字转为 number
            if ("true".equalsIgnoreCase(value)) {
                target.put(key, true);
            } else if ("false".equalsIgnoreCase(value)) {
                target.put(key, false);
            } else if (value.matches("-?\\d+")) {
                // 简单的整数匹配，可视情况去掉，Nacos 参数通常是字符串或布尔
                try {
                    target.put(key, Integer.parseInt(value));
                } catch (final NumberFormatException e) {
                    target.put(key, value);
                }
            } else {
                target.put(key, value);
            }
        }
    }
}
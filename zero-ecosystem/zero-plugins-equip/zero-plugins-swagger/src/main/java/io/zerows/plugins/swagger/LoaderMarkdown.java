package io.zerows.plugins.swagger;

import io.swagger.v3.core.util.Yaml;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Markdown 文档加载解析工具 (包级私有)
 */
@Slf4j
class LoaderMarkdown {

    private static final String CODE_FENCE = "```";

    private LoaderMarkdown() {
    }

    static <T> T load(final InputStream in, final Class<T> clazz) {
        final List<String> blocks = extractCodeBlocks(in);

        if (blocks.isEmpty()) {
            return null;
        }

        for (int i = 0; i < blocks.size(); i++) {
            final String block = blocks.get(i);
            try {
                final T result = Yaml.mapper().readValue(block, clazz);
                if (result != null) {
                    return result;
                }
            } catch (final Exception e) {
                // 仅在调试模式下打印详细解析错误，避免刷屏
                if (log.isTraceEnabled()) {
                    log.trace("{} 块 [{}] 解析失败: {}", SwaggerConstant.K_PREFIX_DOC, i, e.getMessage());
                }
            }
        }
        return null;
    }

    private static List<String> extractCodeBlocks(final InputStream in) {
        final List<String> blocks = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            boolean inBlock = false;
            StringBuilder currentBlock = new StringBuilder();

            for (final String line : reader.lines().toList()) {
                final String trimmed = line.trim();

                if (trimmed.startsWith(CODE_FENCE)) {
                    if (inBlock) {
                        blocks.add(currentBlock.toString());
                        currentBlock = new StringBuilder();
                        inBlock = false;
                    } else {
                        inBlock = true;
                    }
                    continue;
                }

                if (inBlock) {
                    // 保留原始缩进，这对 YAML 至关重要
                    currentBlock.append(line).append("\n");
                }
            }
        } catch (final Exception e) {
            log.error("{} 流读取失败", SwaggerConstant.K_PREFIX_DOC, e);
        }
        return blocks;
    }
}
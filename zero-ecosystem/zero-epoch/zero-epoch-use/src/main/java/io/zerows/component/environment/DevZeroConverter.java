package io.zerows.component.environment;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 自定义 Logback Converter，用于智能处理日志消息。
 * 它会检查消息的开头，如果匹配预设的关键字（如 [PLUG], Defer），
 * 则将该关键字替换为带颜色的版本。
 * 在 pattern 中使用 %smartmsg 来代替 %msg。
 *
 * @author lang : 2025-11-06
 * @since 1.0.0
 */
public class DevZeroConverter extends CompositeConverter<ILoggingEvent> {

    // --- 配置部分 ---
    // 定义消息前缀与颜色/文本的映射关系
    // Key 是日志消息开头需要匹配的字符串 (区分大小写)
    // Value 是替换后的带颜色的完整字符串
    private static final Map<String, String> PREFIX_REPLACEMENTS = new HashMap<>();

    static {
        // ANSI 颜色码参考: https://en.wikipedia.org/wiki/ANSI_escape_code#Colors
        // \033[样式;前景色;背景色m 文本 \033[0m
        PREFIX_REPLACEMENTS.put("[ PLUG ]", "\033[38;5;38m[ PLUG ]\033[0m");   // 青蓝色 [ PLUG ]
        PREFIX_REPLACEMENTS.put("[ XMOD ]", "\033[38;5;10m[ XMOD ]\033[0m");  // 绿色 [ XMOD ]
        PREFIX_REPLACEMENTS.put("[ MNTR ]", "\033[38;5;208m[ MNTR ]\033[0m"); // 橙黄色 [ MNTR ]
        PREFIX_REPLACEMENTS.put("[ ZERO ]", "\033[38;5;91m[ ZERO ]\033[0m"); // 紫色 [ ZERO ]
        PREFIX_REPLACEMENTS.put("[ INST ]", "\033[38;5;106m[ INST ]\033[0m"); // 黄绿色 [ INST ]
        PREFIX_REPLACEMENTS.put("Defer", "\033[38;5;196mDefer\033[0m");     // 红色 Defer
        // 可以在这里轻松添加更多前缀和颜色
        // 例如: PREFIX_REPLACEMENTS.put("[CACHE]", "\033[1;38;5;220m[ CACHE ]\033[0m"); // 加粗金色
    }

    /**
     * {@inheritDoc}
     * <p>
     * 分析原始日志消息，如果开头匹配预定义前缀，则进行替换。
     * </p>
     */
    @Override
    protected String transform(final ILoggingEvent event, final String in) { // in 参数在这里通常是原始的 %msg
        // 获取原始消息内容 (由 Logback 传入)
        final String originalMessage = event.getFormattedMessage();

        // 如果消息为 null 或为空，直接返回
        if (originalMessage == null || originalMessage.isEmpty()) {
            return "";
        }

        // 遍历预定义的前缀映射
        for (final Entry<String, String> entry : PREFIX_REPLACEMENTS.entrySet()) {
            final String prefixToMatch = entry.getKey();
            final String replacement = entry.getValue();

            // 检查消息是否以该前缀开头
            if (originalMessage.startsWith(prefixToMatch)) {
                // 如果匹配，替换掉开头的前缀，并返回新的消息
                // substring(prefixToMatch.length()) 会去掉 "[ PLUG ]" 这部分
                // 然后将带颜色的 "[ PLUG ]" 加在前面
                return replacement + originalMessage.substring(prefixToMatch.length());
            }
        }

        // 如果没有匹配的前缀，则原样返回消息
        return originalMessage;
    }
}
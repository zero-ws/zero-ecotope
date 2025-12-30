package io.zerows.platform;

import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * @author lang : 2025-12-30
 */
@AllArgsConstructor
class ENVCandidate {
    Supplier<InputStream> supplier;
    String successLog;   // 成功加载时的 info 日志
    String notFoundWarn; // 找不到时的 warn（允许为 null：表示由后续候选继续处理）
}

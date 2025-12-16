package io.zerows.extension.skeleton.boot;

import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 配合底层的数据加载器，加载器分两部分
 * <pre><code>
 *     1. 是否带有匹配的方式加载
 *     2. 是否加载 OOB 数据记录
 * </code></pre>
 *
 * @author lang : 2023-06-12
 */
@Slf4j
class DataIo {

    static Stream<String> ioFiles(final String folder, final String prefix, final boolean oob) {

        final ZeroFs fs = ZeroFs.of();
        final List<String> files = fs.inFiles(folder, prefix);
        log.info("[ INST ] 已加载文件数：{}", files.size());
        if (oob) {
            final OCacheConfiguration configuration = OCacheConfiguration.of();
            final Set<MDConfiguration> configSet = configuration.valueSet();
            configSet.stream()
                .map(each -> each.inFiles(prefix))
                .forEach(files::addAll);
        }
        log.info("[ INST ] 扩展文件数（含 OOB）: {}", files.size());
        // 并行
        return files.stream().filter(DataIo::ensure);
    }

    private static boolean ensure(final String filename) {
        // File not null
        if (Ut.isNil(filename)) {
            return false;
        }
        // Ignore "~" start
        if (filename.contains("~")) {
            return false;
        }
        // Excel only
        return filename.endsWith("xlsx") || filename.endsWith("xls");
    }
}

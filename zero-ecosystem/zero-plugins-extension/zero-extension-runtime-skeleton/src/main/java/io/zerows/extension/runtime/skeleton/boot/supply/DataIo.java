package io.zerows.extension.runtime.skeleton.boot.supply;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.extension.HExtension;
import io.zerows.extension.runtime.skeleton.refine.Ke;

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
class DataIo {

    static Stream<String> ioFiles(final String folder, final String prefix, final boolean oob) {

        final List<String> files = Ut.ioFilesN(folder, null, prefix);
        Ke.LOG.Ke.info(DataIo.class, "Before Extension Files = {0}", String.valueOf(files.size()));
        final Set<HExtension> boots = HExtension.initialize();
        if (!boots.isEmpty() && oob) {
            boots.forEach(boot -> files.addAll(boot.oob(prefix)));
            // boots.forEach(boot -> files.addAll(boot.oob(prefix)));
        }
        Ke.LOG.Ke.info(DataIo.class, "After Extension Files = {0}", String.valueOf(files.size()));
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

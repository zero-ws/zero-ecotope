package io.zerows.extension.skeleton.boot;

import cn.hutool.core.util.StrUtil;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author lang : 2023-06-12
 */
@Slf4j
class DataIo {

    static final Set<String> OOB_FILES = new HashSet<>();

    private static Stream<String> waitFor(final String prefix) {
        if (StrUtil.isEmpty(prefix)) {
            log.info("{} OOB 文件数量：{}", KeConstant.K_PREFIX_LOAD, OOB_FILES.size());
            return OOB_FILES.stream();
        }
        final Set<String> matched = OOB_FILES.stream()
            .filter(item -> item.contains(prefix))
            .collect(HashSet::new, Set::add, Set::addAll);
        log.info("{} OOB 文件数量（前缀：{}）: {}", KeConstant.K_PREFIX_LOAD, prefix, matched.size());
        return matched.stream();
    }

    static Stream<String> ioFiles(final String folder, final String prefix, final boolean oob) {
        final ZeroFs fs = ZeroFs.of();

        // 1. 基础扫描
        final List<String> files = fs.inFiles(folder, prefix);
        final int baseCount = files.size();

        // 2. 【调试】仅针对基础扫描信息进行调试（如需关闭，注释掉此行即可）
        debug(files);

        // 3. OOB 追加逻辑
        int addedCount = 0;
        if (oob) {
            final int sizeBefore = files.size();
            // 消费 Primed 启动过程中产生的配置信息
            waitFor(prefix).forEach(files::add);
            addedCount = files.size() - sizeBefore;
        }

        // 4. 最终过滤与去重
        final List<String> finalFiles = files.stream()
            .filter(DataIo::ensure)
            .distinct()
            .toList();

        // 5. 【唯一统计日志】
        log.info("[ INST ] 数据统计 - 基础数量: {}, 追加数量: {}, 最终数量: {}",
            baseCount, addedCount, finalFiles.size());

        return finalFiles.stream();
    }

    /**
     * 调试专用：仅记录基础扫描清单
     */
    private static void debug(final List<String> files) {
        // 仅在当前目录输出基础扫描到的原始文件清单
        Ut.ioOut("debug-files.log", Ut.fromJoin(files, "\n"));
    }

    private static boolean ensure(final String filename) {
        if (Ut.isNil(filename)) {
            return false;
        }
        if (filename.contains("~")) {
            return false;
        }
        return filename.endsWith("xlsx") || filename.endsWith("xls");
    }
}
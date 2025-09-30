package io.zerows.core.uca.fs;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.constant.VString;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * 本地文件夹操作专用接口，用于不同环境中的兼容提取
 *
 * @author lang : 2024-06-17
 */
public interface LocalDir {

    Cc<String, LocalDir> CC_DIR = Cc.open();

    static LocalDir of(final String folder) {
        return CC_DIR.pick(() -> new LocalDirNorm(folder), folder);
    }

    /*
     * 直接提取当前环境中的 root 信息，根据 "/" 来提取，此处是旧流程，不论使用哪种方式读取，此处的结果会是提取当前目录
     * 这种当前目录的提取模式会直接忽略 classpath 和 jar 内部路径，直接根据当前环境就提取了相关信息，生产模式运行时也
     * 会直接提取运行 jar 文件的当前路径，为第一优先级的处理模式。
     */
    static String root() {
        final Thread current = Thread.currentThread();
        final URL rootUrl = current.getClass().getResource("/");
        if (Objects.isNull(rootUrl)) {
            return VString.EMPTY;
        }
        final File rootFile = new File(rootUrl.getFile());
        return rootFile.getAbsolutePath() + VString.SLASH;
    }

    List<String> directories(String directory);
}

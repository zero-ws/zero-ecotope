package io.zerows.core.uca.fs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author lang : 2024-06-17
 */
public class LocalDirNorm extends AbstractLocalDir {
    private final LocalDir currentDir;
    private final LocalDir embeddedDir;

    LocalDirNorm(final String root) {
        super(root);
        this.currentDir = new LocalDirCurrent();
        this.embeddedDir = new LocalDirEmbed(root);
    }

    @Override
    public List<String> directories(final String directory) {
        final Set<String> directories = new TreeSet<>(this.currentDir.directories(directory));
        if (directories.isEmpty()) {
            // 当前目录无法读取时才从嵌入 jar 环境中继续读取
            directories.addAll(this.embeddedDir.directories(directory));
        }
        return new ArrayList<>(directories);
    }
}

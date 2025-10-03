package io.zerows.component.fs;

import io.zerows.platform.constant.VString;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-06-17
 */
public class LocalDirCurrent extends AbstractLocalDir {
    LocalDirCurrent() {
        super(null);
    }

    // root = null
    @Override
    public List<String> directories(final String directory) {
        final String root = LocalDir.root();
        return this.listDirectoriesN(directory, root);
    }

    private List<String> listDirectoriesN(final String folder, final String root) {
        final List<String> folders = new ArrayList<>();
        // root + folder
        final File folderObj = this.ioFileAbsolute(folder, root);
        if (Objects.nonNull(folderObj) && folderObj.isDirectory()) {
            folders.add(folder);
            // Else
            final String[] folderList = folderObj.list();
            assert folderList != null;
            final String relatedPath = folderObj.getAbsolutePath().replace("\\", "/");
            Arrays.stream(folderList).forEach(folderS -> {
                final String rootCopy = root.replace("\\", "/");
                final String pathReplaced = relatedPath.replace(rootCopy, VString.EMPTY);
                folders.addAll(this.listDirectoriesN(pathReplaced + "/" + folderS, root));
            });
            this.logger().debug("Directories found: size = {0}", String.valueOf(folders.size()));
        }
        return folders;
    }
}

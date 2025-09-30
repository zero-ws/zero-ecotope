package io.zerows.core.uca.fs;

import io.zerows.ams.util.HUt;
import io.zerows.core.uca.log.LogUtil;

import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * @author lang : 2024-06-17
 */
public abstract class AbstractLocalDir implements LocalDir {

    private final String root;

    protected AbstractLocalDir(final String root) {
        this.root = root;
    }

    protected LogUtil logger() {
        return LogUtil.from(this.getClass());
    }

    protected File ioFileAbsolute(final String folder, final String root) {
        // root + folder
        File folderObj = new File(HUt.isNil(root) ? folder : HUt.ioPath(root, folder));
        if (!folderObj.exists()) {
            final URL url = HUt.ioURL(folder);
            if (Objects.nonNull(url)) {
                // Url Processing to File
                folderObj = new File(url.getPath());
            } else {
                folderObj = null;
            }
        }
        return folderObj;
    }
}

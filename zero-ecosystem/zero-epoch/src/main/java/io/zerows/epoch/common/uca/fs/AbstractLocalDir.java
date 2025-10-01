package io.zerows.epoch.common.uca.fs;

import io.zerows.ams.util.UtBase;
import io.zerows.epoch.common.uca.log.LogUtil;

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
        File folderObj = new File(UtBase.isNil(root) ? folder : UtBase.ioPath(root, folder));
        if (!folderObj.exists()) {
            final URL url = UtBase.ioURL(folder);
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

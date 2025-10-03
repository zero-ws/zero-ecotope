package io.zerows.component.fs;

import io.r2mo.function.Fn;
import io.zerows.component.log.LogAs;
import io.zerows.constant.VMessage;
import io.zerows.support.UtBase;
import io.zerows.specification.vital.HFS;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.StandardCopyOption;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class LocalFs implements HFS {
    @Override
    public boolean mkdir(final String dirName) {
        final File file = new File(dirName);
        if (!file.exists()) {
            LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_MKDIR, file.getAbsolutePath());
            Fn.jvmAt(() -> FileUtils.forceMkdir(file));
        }
        return true;
    }

    @Override
    public boolean rm(final String filename) {
        if (!UtBase.ioExist(filename)) {
            return true;
        }
        final File file = UtBase.ioFile(filename);
        if (file.exists()) {
            LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_RM, file.getAbsolutePath());
            Fn.jvmAt(() -> FileUtils.forceDelete(file));
        }
        return true;
    }


    @Override
    public boolean cp(final String nameFrom, final String nameTo) {
        final File fileSrc = new File(nameFrom);
        final File fileDst = new File(nameTo);
        if (fileSrc.exists() && fileDst.exists()) {
            if (fileDst.isDirectory()) {
                // The target must be directory
                if (fileSrc.isFile()) {
                    // File -> Directory
                    LogAs.Fs.info(this.getClass(),
                        VMessage.HFS.IO_CMD_CP, nameFrom, nameTo, "copyFileToDirectory");
                    Fn.jvmAt(() -> FileUtils.copyFileToDirectory(fileSrc, fileDst, true));
                } else {
                    if (fileSrc.getName().equals(fileDst.getName())) {
                        // Directory -> Directory ( Overwrite )
                        LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_CP, nameFrom, nameTo, "copyDirectory");
                        Fn.jvmAt(() -> FileUtils.copyDirectory(fileSrc, fileDst, true));
                    } else {
                        // Directory -> Directory / ( Children )
                        LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_CP, nameFrom, nameTo, "copyDirectoryToDirectory");
                        Fn.jvmAt(() -> FileUtils.copyDirectoryToDirectory(fileSrc, fileDst));
                    }
                }
            } else {
                // File -> File
                if (fileSrc.isFile()) {
                    LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_CP, nameFrom, nameTo, "copyFile");
                    Fn.jvmAt(() -> FileUtils.copyFile(fileSrc, fileDst, true));
                }
            }
        } else {
            LogAs.Fs.warn(this.getClass(), VMessage.HFS.ERR_CMD_CP, nameFrom, nameTo);
        }
        return true;
    }

    @Override
    public boolean rename(final String nameFrom, final String nameTo) {
        final File fileSrc = new File(nameFrom);
        if (fileSrc.exists()) {
            final File fileTo = new File(nameTo);
            final File fileToP = fileTo.getParentFile();
            LogAs.Fs.info(this.getClass(), VMessage.HFS.IO_CMD_MOVE, fileSrc.getAbsolutePath(), fileToP.getAbsolutePath());
            if (fileSrc.isDirectory()) {
                // 目录拷贝：目录 -> 目录
                Fn.jvmAt(() -> FileUtils.moveDirectory(fileSrc, fileTo));
            } else {
                // 文件拷贝（替换原始文件）
                if (UtBase.ioExist(nameTo)) {
                    // Fix: org.apache.commons.io.FileExistsException:
                    //      File element in parameter 'null' already exists:
                    this.rm(nameTo);
                }
                Fn.jvmAt(() -> FileUtils.moveFile(fileSrc, fileTo, StandardCopyOption.REPLACE_EXISTING));
            }
        }
        return true;
    }
}

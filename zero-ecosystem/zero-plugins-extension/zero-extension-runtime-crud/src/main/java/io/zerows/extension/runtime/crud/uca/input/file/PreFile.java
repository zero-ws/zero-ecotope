package io.zerows.extension.runtime.crud.uca.input.file;

import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.input.Pre;

/**
 * 文件处理专用处理器，内置于 {@link io.vertx.mod.crud.uca.input.file}，提供了四种不同的实现
 * <pre><code>
 *     - {@link FileUploadPre} 附件上传（添加）
 *     - {@link FileSavePre} 附件同步，保存更新
 *     - {@link FileRemovePre} 附件删除
 *     - {@link FileFetchPre} 附件读取
 * </code></pre>
 * 注意一个文件的附件的同步（更新）主要是：添加/删除，因为其他记录和附件记录的最终关系
 * 并非是数据存储的基本关系，而是关联关系，实际管理一个模型记录中的附件本身是在维护关联关系
 *
 * @author lang : 2023-08-04
 */
public interface PreFile {
    static Pre fileIn(final boolean createOnly) {
        if (createOnly) {
            return Pooled.CCT_PRE.pick(FileUploadPre::new, FileUploadPre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(FileSavePre::new, FileSavePre.class.getName());
        }
    }

    static Pre fileOut() {
        return Pooled.CCT_PRE.pick(FileRemovePre::new, FileRemovePre.class.getName());
    }

    static Pre fileData() {
        return Pooled.CCT_PRE.pick(FileFetchPre::new, FileFetchPre.class.getName());
    }
}

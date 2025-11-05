package io.zerows.extension.crud.uca.input;

import io.zerows.extension.crud.common.Pooled;

/**
 * 文件处理专用处理器，内置于 {@link io.vertx.mod.crud.uca.input.file}，提供了四种不同的实现
 * <pre><code>
 *     - {@link PreFileUploadPre} 附件上传（添加）
 *     - {@link PreFileSavePre} 附件同步，保存更新
 *     - {@link PreFileRemovePre} 附件删除
 *     - {@link PreFileFetchPre} 附件读取
 * </code></pre>
 * 注意一个文件的附件的同步（更新）主要是：添加/删除，因为其他记录和附件记录的最终关系
 * 并非是数据存储的基本关系，而是关联关系，实际管理一个模型记录中的附件本身是在维护关联关系
 *
 * @author lang : 2023-08-04
 */
public interface PreFile {
    static Pre fileIn(final boolean createOnly) {
        if (createOnly) {
            return Pooled.CCT_PRE.pick(PreFileUploadPre::new, PreFileUploadPre.class.getName());
        } else {
            return Pooled.CCT_PRE.pick(PreFileSavePre::new, PreFileSavePre.class.getName());
        }
    }

    static Pre fileOut() {
        return Pooled.CCT_PRE.pick(PreFileRemovePre::new, PreFileRemovePre.class.getName());
    }

    static Pre fileData() {
        return Pooled.CCT_PRE.pick(PreFileFetchPre::new, PreFileFetchPre.class.getName());
    }
}

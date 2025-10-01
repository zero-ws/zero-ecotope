package io.zerows.epoch.corpus.model.uca.normalize;

import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDConfiguration;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDId;
import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Objects;

/**
 * 扫描所有数据目录，提取相关数据文件
 *
 * @author lang : 2024-05-12
 */
class EquipFile implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {
        final MDId id = configuration.id();
        final Bundle owner = id.owner();
        /*
         * 数据特殊目录，主要包含了如下：
         * - data：基础数据目录（按表进行划分）
         * - modulat：模块化目录
         * - security：安全相关目录，以及权限管理
         * - workflow：工作流相关目录
         * - reporting：报表相关目录
         * - extension：扩展目录
         */
        configuration.addFile(this.scanDir(id.path() + "/data", owner));
        configuration.addFile(this.scanDir(id.path() + "/modulat", owner));
        configuration.addFile(this.scanDir(id.path() + "/security", owner));
        configuration.addFile(this.scanDir(id.path() + "/workflow", owner));
        configuration.addFile(this.scanDir(id.path() + "/reporting", owner));
        configuration.addFile(this.scanDir(id.path() + "/extension", owner));
    }

    private List<String> scanDir(final String dataDir, final Bundle owner) {
        if (Objects.isNull(owner)) {
            // 递归读取所有目录EXCEL_2007
            return Ut.ioFilesN(dataDir, VPath.SUFFIX.EXCEL_2007);
        } else {
            // 递归读取所有目录
            return Ut.Bnd.ioFileN(dataDir, owner, VPath.SUFFIX.EXCEL_2007);
        }
    }
}

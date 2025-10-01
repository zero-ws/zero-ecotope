package io.zerows.epoch.component.normalize;

import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.corpus.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.configuration.module.MDId;
import io.zerows.epoch.corpus.configuration.module.children.MDPage;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
class EquipWeb implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {

        // web
        final MDId id = configuration.id();
        final List<String> fileList = this.scanPage("/web", id);
        fileList.stream().filter(filePath -> !filePath.contains("SHARED")).map(filePath -> {
            final MDPage page = new MDPage(id);
            // 路径处理
            final String pathRelated = this.normalizePath(filePath, id);
            page.configure(pathRelated);
            return page;
        }).forEach(configuration::addPage);
    }

    private String normalizePath(final String filePath, final MDId id) {
        final Bundle owner = id.owner();
        if (Objects.isNull(owner)) {
            final String split = id.value();
            return filePath.split(split)[2];
        } else {
            // 如果是 Bundle 有值，此处的 filePath 一定会是相对路径
            return filePath;
        }
    }

    @SuppressWarnings("all")
    private List<String> scanPage(final String pathRoot, final MDId id) {
        final Bundle owner = id.owner();
        final String webDir = id.path() + pathRoot;
        if (Objects.isNull(owner)) {
            // id.path() 中已经带了 plugins 目录
            return Ut.ioFilesN(webDir, VPath.SUFFIX.JSON);
        } else {
            return Ut.Bnd.ioFileN(webDir, owner, VPath.SUFFIX.JSON);
        }
    }
}

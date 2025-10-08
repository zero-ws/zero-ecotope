package io.zerows.epoch.jigsaw;

import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.basicore.MDPage;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;

import java.util.List;

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
        final String split = id.value();
        return filePath.split(split)[2];
    }

    @SuppressWarnings("all")
    private List<String> scanPage(final String pathRoot, final MDId id) {
        final String webDir = id.path() + pathRoot;
        return Ut.ioFilesN(webDir, VValue.SUFFIX.JSON);
    }
}

package io.zerows.epoch.jigsaw;

import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.epoch.web.MDId;
import io.zerows.epoch.web.MDPage;
import io.zerows.platform.constant.VValue;

import java.util.List;

/**
 * @author lang : 2024-06-26
 */
class EquipWeb implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {

        // web
        final MDId id = configuration.id();
        final List<String> fileList = this.scanPage(id);
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
        return filePath.split(split)[1];
    }

    @SuppressWarnings("all")
    private List<String> scanPage(final MDId id) {
        final ZeroFs io = ZeroFs.of(id);
        return io.inFiles("web", VValue.SUFFIX.JSON);
    }
}

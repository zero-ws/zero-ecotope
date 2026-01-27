package io.zerows.epoch.jigsaw;

import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.epoch.web.MDId;
import lombok.extern.slf4j.Slf4j;

/**
 * 扫描所有数据目录，提取相关数据文件
 *
 * @author lang : 2024-05-12
 */
@Slf4j
class EquipFile implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {
        final MDId id = configuration.id();
        /*
         * 数据特殊目录，主要包含了如下：
         * - data：基础数据目录（按表进行划分）
         * - modulat：模块化目录
         * - security：安全相关目录，以及权限管理
         * - workflow：工作流相关目录
         * - reporting：报表相关目录
         * - extension：扩展目录
         */
        final ZeroFs io = ZeroFs.of(id);
        configuration.addFile(io.inFiles("data"));
        configuration.addFile(io.inFiles("modulat"));
        configuration.addFile(io.inFiles("security"));
        configuration.addFile(io.inFiles("workflow"));
        configuration.addFile(io.inFiles("reporting"));
        configuration.addFile(io.inFiles("extension"));
    }
}

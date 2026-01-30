package io.zerows.epoch.jigsaw;

import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.epoch.web.MDId;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2024-05-08
 */
@Slf4j
class EquipEntry implements EquipAt {
    private final EquipAt equipAtShape = new EquipShape();
    private final EquipAt equipAtDao = new EquipModel();
    private final EquipAt equipAtFile = new EquipFile();
    private final EquipAt equipAtWorkflow = new EquipWorkflow();
    private final EquipAt equipAtWeb = new EquipWeb();

    @Override
    public void initialize(final MDConfiguration configuration) {
        // 快速返回（防止二次初始化）
        final MDId id = configuration.id();
        Objects.requireNonNull(id);

        if (OCacheConfiguration.initialized(id.value())) {
            log.info("[ ZERO ] 扩展配置已初始化，跳过初始化流程! id = {}", id.value());
            return;
        }


        final String name = ZeroFs.of(id).name();
        if (Ut.isNil(name)) {
            log.warn("[ ZERO ] 无配置名称: shape -> name，跳过加载！id = {}", id.value());
            return;
        }


        /*
         * 执行配置初始化的前提条件
         * - MDId 不为空
         * - shape -> name 不为空
         */
        // addShape
        this.equipAtShape.initialize(configuration);

        // addEntity
        this.equipAtDao.initialize(configuration);

        // addFiles
        this.equipAtFile.initialize(configuration);

        // addWorkflow
        this.equipAtWorkflow.initialize(configuration);

        // addWeb
        this.equipAtWeb.initialize(configuration);


        // 执行完成后更改缓存
        OCacheConfiguration.of(configuration.id().owner()).add(configuration);
    }
}

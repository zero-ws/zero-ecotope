package io.zerows.epoch.corpus.model.uca.normalize;

import io.zerows.epoch.corpus.model.store.module.OCacheConfiguration;
import io.zerows.epoch.corpus.model.store.module.OCacheDao;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDConfiguration;
import io.zerows.epoch.corpus.metadata.atom.configuration.MDId;

/**
 * @author lang : 2024-05-08
 */
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
        if (OCacheConfiguration.initialized(id.value())) {
            this.logger().info("The Extension Configuration has been initialized! id = {}", id.value());
            return;
        }


        // 先执行初始化，保证 MDMeta 在当前环境中存在
        OCacheDao.of(configuration.id().owner()).configure(configuration);


        {
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
        }


        // 执行完成后更改缓存
        OCacheConfiguration.of(configuration.id().owner()).add(configuration);
    }
}

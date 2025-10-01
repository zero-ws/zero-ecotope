package io.zerows.epoch.component.normalize;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.configuration.module.MDId;
import io.zerows.epoch.corpus.configuration.module.modeling.MDConnect;
import io.zerows.epoch.corpus.configuration.module.modeling.MDEntity;
import io.zerows.epoch.mem.module.OCacheConfiguration;
import org.osgi.framework.Bundle;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 底层访问全局缓存提取所有当前所需的
 *
 * @author lang : 2024-05-09
 */
class EquipModel implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {


        // model/connect.yml 文件提取
        final MDId id = configuration.id();
        final Bundle owner = id.owner();

        final String connectFile = id.path() + "/model/connect.yml";


        final MakerIo<MDConnect> makerConnect = MakerIo.ofConnect();
        // 此处键值是表名
        final ConcurrentMap<String, MDConnect> connectMap = makerConnect.build(connectFile, owner);


        final JsonObject configurationJ = configuration.inConfiguration();
        final Boolean isOverwrite = configurationJ.getBoolean("overwrite", Boolean.FALSE);
        if (isOverwrite) {
            // 重写模式，直接从已经存在的 MDConnect 中提取信息来填充
            final Set<MDConnect> connects = OCacheConfiguration.entireConnect();
            connects.forEach(connect -> connectMap.put(connect.getTable(), connect));
            this.logger().info("Connect Overwrite Mode: Size = {}", connects.size());
        }


        configuration.setConnect(connectMap);


        final String modelDir = id.path() + "/model";
        final MakerIo<MDEntity> makerEntity = MakerIo.ofEntity();
        final ConcurrentMap<String, MDEntity> entityMap = makerEntity.build(modelDir, owner,
            // 第三参此处必须包含
            connectMap);
        configuration.setEntity(entityMap);
    }
}

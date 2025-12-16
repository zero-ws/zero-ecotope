package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.boot.ZeroOr;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.specification.development.compiled.HBundle;

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
        final HBundle owner = id.owner();

        final String connectFile = id.path() + "/model/connect.yml";

        final ZeroOr io = ZeroOr.of(id);
        final MakerIo<MDConnect> makerConnect = MakerIo.ofConnect(io);
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
        final MakerIo<MDEntity> makerEntity = MakerIo.ofEntity(io);
        final ConcurrentMap<String, MDEntity> entityMap = makerEntity.build(modelDir, owner,
            // 第三参此处必须包含
            connectMap);
        configuration.setEntity(entityMap);
    }
}

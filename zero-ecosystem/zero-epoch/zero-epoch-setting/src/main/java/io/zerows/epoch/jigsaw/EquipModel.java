package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 底层访问全局缓存提取所有当前所需的
 *
 * @author lang : 2024-05-09
 */
@Slf4j
class EquipModel implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {
        // model/connect.yml 文件提取
        final MDId id = configuration.id();
        final HBundle owner = id.owner();
        final ZeroFs io = ZeroFs.of(id);


        // -------------- model/connect.yml 文件处理，调用 MakerIoConnect
        final String connectFile = "model/connect.yml";
        final MakerIo<MDConnect> makerConnect = MakerIo.ofConnect(io);
        // 此处键值是表名
        final ConcurrentMap<String, MDConnect> connectMap = makerConnect.build(connectFile, owner);


        // -------------- 覆盖模式单独处理
        final JsonObject configurationJ = configuration.inConfiguration();
        final Boolean isOverwrite = configurationJ.getBoolean("overwrite", Boolean.FALSE);
        if (isOverwrite) {
            // 重写模式，直接从已经存在的 MDConnect 中提取信息来填充
            final Set<MDConnect> connects = OCacheConfiguration.entireConnect();
            connects.forEach(connect -> connectMap.put(connect.getTable(), connect));
            log.info("[ XMOD ] 连接重写模式：Size = {}", connects.size());
        }
        configuration.setConnect(connectMap);


        // -------------- model 目录处理，调用 MakerIoEntity
        final String modelDir = "model";
        final MakerIo<MDEntity> makerEntity = MakerIo.ofEntity(io);
        final ConcurrentMap<String, MDEntity> entityMap = makerEntity.build(modelDir, owner,
            // 第三参此处必须包含
            connectMap);
        configuration.setEntity(entityMap);
    }
}

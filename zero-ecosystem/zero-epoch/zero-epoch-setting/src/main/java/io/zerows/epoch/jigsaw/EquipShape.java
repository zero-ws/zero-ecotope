package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VPath;
import io.zerows.platform.constant.VString;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2024-05-09
 */
@Slf4j
class EquipShape implements EquipAt {
    @Override
    public void initialize(final MDConfiguration configuration) {
        final MDId id = configuration.id();
        Objects.requireNonNull(id);

        // 加载 <id>.xml 配置文件
        // plugins/<id>.yml
        final String baseDir = id.path();
        final String filename = baseDir + VString.DOT + VPath.SUFFIX.YML;


        // plugins/<id>/configuration.json
        final String fileConfiguration = baseDir + VString.SLASH + "configuration.json";
        final HBundle owner = id.owner();
        if (Objects.isNull(owner)) {
            log.info("[ ZERO ] 正常环境的配置加载: {}", filename);
        }
        final JsonObject metadata = Ut.ioYaml(filename);
        final JsonObject shapeJ = Ut.valueJObject(metadata, KName.SHAPE);


        // 名称合法就处理 shape
        final String name = Ut.valueString(shapeJ, KName.NAME);
        if (Ut.isNotNil(name)) {
            configuration.addShape(name, Ut.ioJObject(fileConfiguration));
        }
    }
}

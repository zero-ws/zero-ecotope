package io.zerows.epoch.component.normalize;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.corpus.configuration.module.MDConfiguration;
import io.zerows.epoch.corpus.configuration.module.MDId;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-09
 */
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
        final Bundle owner = id.owner();
        if (Objects.isNull(owner)) {
            this.logger().info("Norm environment, configuration loading: {}", filename);
        } else {
            this.logger().info("OSGI environment, configuration loading: {} of bundle {}",
                filename, owner.getSymbolicName());
        }
        final JsonObject metadata = Ut.Bnd.ioYamlJ(filename, owner);
        final JsonObject shapeJ = Ut.valueJObject(metadata, KName.SHAPE);


        // 名称合法就处理 shape
        final String name = Ut.valueString(shapeJ, KName.NAME);
        if (Ut.isNotNil(name)) {
            configuration.addShape(name, Ut.Bnd.ioJObject(fileConfiguration, owner));
        }
    }
}

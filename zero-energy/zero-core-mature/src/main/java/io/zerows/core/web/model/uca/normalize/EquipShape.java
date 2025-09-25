package io.zerows.core.web.model.uca.normalize;

import io.vertx.core.json.JsonObject;
import io.zerows.ams.constant.VPath;
import io.zerows.ams.constant.VString;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.atom.configuration.MDId;
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

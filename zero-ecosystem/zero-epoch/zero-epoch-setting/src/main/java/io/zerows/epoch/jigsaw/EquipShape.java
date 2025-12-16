package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.boot.ZeroFs;
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

        final ZeroFs io = ZeroFs.of(id);
        /*
         * 新版引入了 MDMod 的核心配置项，所以不用再加载 plugins/{id}.yml 文件，直接做设置绑定即可
         * 而且 EquipShape 是内部调用，所以代码走到这里 name 一定存在，外层已检查过
         * - MDId
         * - shape -> name
         */
        final String name = io.name();


        // plugins/<id>/configuration.json
        final String filename = "configuration.json";
        final HBundle owner = id.owner();
        if (Objects.isNull(owner)) {
            log.info("[ XMOD ] 正常环境的配置加载: {}", filename);
        }


        final JsonObject configurationJ = io.inJObject(filename);
        if (Ut.isNil(configurationJ)) {
            log.debug("[ XMOD ] 配置文件不存在: id = {}, file = {}", id.value(), filename);
            return;
        }
        configuration.addShape(name, configurationJ);
    }
}

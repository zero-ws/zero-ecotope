package io.zerows.boot.extension.appcontainer;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BuildShared {
    /**
     * 加载全局配置
     * 使用 ZeroFs 从 src/main/resources/init/environment.json 加载配置
     * 使用 Ut.compileAnsible() 处理环境变量替换
     */
    static JsonObject loadGlobalConfig() {
        try {
            final ZeroFs fs = ZeroFs.of();
            final String envPath = "init/environment.json";

            if (!fs.isExist(envPath)) {
                log.warn("[ INST ] 未找到 environment.json，使用默认配置");
                return new JsonObject()
                    .put("language", "cn")
                    .put("active", true);
            }

            // 使用 ZeroFs 加载配置文件
            final JsonObject tenantJ = fs.inJObject(envPath);

            final String parsed = Ut.compileAnsible(tenantJ.encode());
            log.debug("[ INST ] 加载全局配置完成");

            final JsonObject parsedJ = new JsonObject(parsed);
            return Ut.valueJObject(parsedJ, "global");
        } catch (final Exception e) {
            log.error("[ INST ] 加载全局配置失败", e);
            return new JsonObject()
                .put("language", "cn")
                .put("active", true);
        }
    }
}

package io.zerows.epoch.configuration;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.spec.InPreArgs;
import io.zerows.epoch.spec.InPreVertx;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SPID("ConfigServer/nacos")// 必须的ID配置
@Slf4j
public class NacosConfigProvider implements ConfigProvider {
    @Override
    public YmConfiguration configure(final InPreArgs config, final HApp app) {
        // 提取 Nacos 连接选项
        final NacosOptions options = config.optionsAs(NacosOptions.class);

        // 提取导入规则表（import: [...]）
        final InPreVertx.Config configVertx = config.configVertx();

        // 配置检查
        this.configureEnsure(options, configVertx, app);

        // 应用选项矫正
        options.applyOption();

        final List<NacosMeta> metaList = NacosRule.of().parseRule(configVertx.getImports(), app);

        final List<JsonObject> waitFor = new ArrayList<>();
        metaList.stream()
            .map(metadata -> NacosClient.of().readConfig(metadata, options))
            .forEach(waitFor::add);
        return null;
    }

    private void configureEnsure(final NacosOptions options, final InPreVertx.Config configVertx,
                                 final HApp app) {
        // 🛡️ 失败检查 vertx-boot.yml
        if (Objects.isNull(options)) {
            throw new _500ServerInternalException("Nacos 基本配置丢失 / vertx.cloud.nacos");
        }
        if (Objects.isNull(configVertx)) {
            throw new _500ServerInternalException("Nacos 基本配置丢失 / vertx.config");
        }
        final List<String> imports = configVertx.getImports();
        if (Objects.isNull(imports) || imports.isEmpty()) {
            throw new _500ServerInternalException("Nacos 基本配置丢失 / vertx.config.import");
        }

        // 🛡️ Nacos 名称是否和 vertx.application.name 保持一致
        final String name = app.name();
        final String nameNacos = options.getName();
        if (Ut.isNil(nameNacos) || !nameNacos.equals(name)) {
            throw new _500ServerInternalException("Nacos 配置名称不匹配 / vertx.cloud.nacos.name != vertx.application.name");
        }
    }
}

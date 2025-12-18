package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.management.OCacheConfiguration;
import io.zerows.platform.enums.EmApp;
import io.zerows.plugins.excel.ExcelConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author lang : 2024-06-12
 */
@Slf4j
public class ExcelEnvConnect implements ExcelEnv<Void> {

    private static final OCacheConfiguration STORE = OCacheConfiguration.of();

    /**
     * <pre>
     * 新配置处理，位于 vertx.yml 中的 excel: 节点，非 {@link EmApp.Native} 插件，只可以作为扩展来处理
     * excel:
     *   pen: "io.zerows.plugins.excel.style.BlueTpl"
     *   environment:
     *     - name: environment.ambient.xlsx
     *       path: "init/oob/environment.ambient.xlsx"
     *       alias:
     *         - /src/main/resources/init/oob/environment.ambient.xlsx
     *   temp: /tmp/
     *   tenant: "init/environment.json"
     * </pre>
     * 新版配置过程中直接计算 unique 而不再采用之前的 mapping 配置来处理，通过计算来获取核心配置信息（自动加载）
     *
     * @param config 结构如上 excel 节点
     *
     * @return 配置对象
     */
    @Override
    public Void prepare(final JsonObject config) {
        final Set<MDConfiguration> configSet = STORE.valueSet();
        for (final MDConfiguration configuration : configSet) {
            final MDId id = configuration.id();
            final Set<MDConnect> connects = configuration.inConnect();
            log.info("{} Connect 配置检查：{}，MDConnect 数量：{}",
                ExcelConstant.K_PREFIX, id.value(), connects.size());
        }
        log.info("{} Connect 模块配置数量：{}", ExcelConstant.K_PREFIX, configSet.size());
        return null;
    }
}

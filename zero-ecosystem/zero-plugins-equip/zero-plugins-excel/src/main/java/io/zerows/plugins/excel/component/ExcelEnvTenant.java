package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.plugins.excel.metadata.ExTenant;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     1. Excel 的配置不能在模块中单独配置，需要在整体配置中执行
 *     2. Excel 配置中的 tenant 只能用于本地版本的导入，若使用远程版本导入则要开启 Excel 附加的配置进行重新布局
 * </pre>
 *
 * @author lang : 2024-06-12
 */
@Slf4j
public class ExcelEnvTenant implements ExcelEnv<ExTenant> {
    @Override
    public ExTenant prepare(final JsonObject config) {
        if (!config.containsKey(YmSpec.excel.tenant)) {
            return null;
        }

        final ZeroFs fs = ZeroFs.of();
        final String tenantFile = Ut.valueString(config, YmSpec.excel.tenant);
        if (!fs.isExist(tenantFile)) {
            log.warn("{} Tenant 未指定租户文件，跳过配置！", ExcelConstant.K_PREFIX);
            return null;
        }
        final JsonObject tenantJ = fs.inJObject(tenantFile);
        /*
         * 环境变量转换，替换旧版的
         */
        final String parsed = Ut.compileAnsible(tenantJ.encode());

        log.info("{} Tenant 导入数据的租户配置: {}", ExcelConstant.K_PREFIX, parsed);
        return ExTenant.create(new JsonObject(parsed));
    }
}

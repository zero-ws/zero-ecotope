package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.platform.constant.VString;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lang : 2024-06-12
 */
@Slf4j
public class ExcelEnvFormula implements ExcelEnv<Map<String, Workbook>> {

    private Function<String, Workbook> workbookFn;

    public ExcelEnvFormula bind(final Function<String, Workbook> workbookFn) {
        this.workbookFn = workbookFn;
        return this;
    }

    @Override
    public Map<String, Workbook> prepare(final JsonObject config) {
        // 新版将 environment 改成了 formula 节点
        if (!config.containsKey(YmSpec.excel.formula.__)) {
            // Fix: Cannot invoke "java.util.Map.size()" because "m" is null
            log.warn("{} Formula 未指定配置，跳过处理！", ExcelConstant.K_PREFIX);
            return new ConcurrentHashMap<>();
        }


        if (Objects.isNull(this.workbookFn)) {
            // Fix: Cannot invoke "java.util.Map.size()" because "m" is null
            log.warn("{} Formula 未绑定 Workbook 加载方法，跳过 Formula 配置处理！", ExcelConstant.K_PREFIX);
            return new ConcurrentHashMap<>();
        }


        final JsonArray environments = config.getJsonArray(YmSpec.excel.formula.__);
        log.info("{} Formula 表达式配置: {}", ExcelConstant.K_PREFIX, environments.encode());
        final Map<String, Workbook> reference = new ConcurrentHashMap<>();
        environments.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .forEach(each -> {
                /*
                 * Build reference
                 */
                final String path = each.getString(YmSpec.excel.formula.path);
                /*
                 * Reference Evaluator
                 */
                final String name = each.getString(YmSpec.excel.formula.name);
                final Workbook workbook = this.workbookFn.apply(path);
                reference.put(name, workbook);
                reference.putAll(this.prepareAlias(each, workbook));
            });
        return reference;
    }

    private Map<String, Workbook> prepareAlias(final JsonObject each, final Workbook workbook) {
        final Map<String, Workbook> reference = new ConcurrentHashMap<>();
        /*
         * Alias Parsing
         */
        if (each.containsKey(YmSpec.excel.formula.alias)) {
            final JsonArray alias = each.getJsonArray(YmSpec.excel.formula.alias);
            final File current = new File(VString.EMPTY);
            Ut.itJArray(alias, String.class, (item, index) -> {
                final String filename = current.getAbsolutePath() + item;
                final File file = new File(filename);
                if (file.exists()) {
                    reference.put(file.getAbsolutePath(), workbook);
                }
            });
        }
        return reference;
    }
}

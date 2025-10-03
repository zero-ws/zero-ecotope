package io.zerows.plugins.office.excel.uca.initialize;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.application.YmlCore;
import io.zerows.support.Ut;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lang : 2024-06-12
 */
public class ExcelEnvFormula implements ExcelEnv<Map<String, Workbook>> {

    private Function<String, Workbook> workbookFn;

    public ExcelEnvFormula bind(final Function<String, Workbook> workbookFn) {
        this.workbookFn = workbookFn;
        return this;
    }

    @Override
    public Map<String, Workbook> prepare(final JsonObject config) {
        if (!config.containsKey(YmlCore.excel.ENVIRONMENT)) {
            // Fix: Cannot invoke "java.util.Map.size()" because "m" is null
            return new ConcurrentHashMap<>();
        }


        if (Objects.isNull(this.workbookFn)) {
            // Fix: Cannot invoke "java.util.Map.size()" because "m" is null
            return new ConcurrentHashMap<>();
        }


        final JsonArray environments = config.getJsonArray(YmlCore.excel.ENVIRONMENT);
        this.logger().debug("[ Έξοδος ] Configuration environments: {0}", environments.encode());
        final Map<String, Workbook> reference = new ConcurrentHashMap<>();
        environments.stream().filter(Objects::nonNull)
            .map(item -> (JsonObject) item)
            .forEach(each -> {
                /*
                 * Build reference
                 */
                final String path = each.getString(YmlCore.excel.environment.PATH);
                /*
                 * Reference Evaluator
                 */
                final String name = each.getString(YmlCore.excel.environment.NAME);
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
        if (each.containsKey(YmlCore.excel.environment.ALIAS)) {
            final JsonArray alias = each.getJsonArray(YmlCore.excel.environment.ALIAS);
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

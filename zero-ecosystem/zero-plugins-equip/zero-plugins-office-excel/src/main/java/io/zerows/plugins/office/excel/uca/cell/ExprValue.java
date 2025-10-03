package io.zerows.plugins.office.excel.uca.cell;

import io.vertx.core.json.JsonArray;
import io.zerows.constant.VName;
import io.zerows.constant.VPath;
import io.zerows.constant.VString;
import io.zerows.constant.VValue;
import io.zerows.epoch.based.constant.KName;
import io.zerows.exception.web._60050Exception501NotSupport;
import io.zerows.epoch.program.Ut;
import io.zerows.plugins.office.excel.eon.ExConstant;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

/**
 * Processing
 * - CODE:config
 * - NAME:config
 * - CODE:NAME:config
 */
public class ExprValue implements ExValue {

    private static final ConcurrentMap<String, BiFunction<String, ConcurrentMap<String, String>, String>> PATH_FN =
        new ConcurrentHashMap<>() {
            {
                // CODE:config
                this.put(ExConstant.CELL.CODE_CONFIG, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    return Ut.ioPath(pathRoot, code);
                });
                // NAME:config
                this.put(ExConstant.CELL.NAME_CONFIG, (pathRoot, paramMap) -> {
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, name);
                });
                // NAME:class
                this.put(ExConstant.CELL.NAME_CLASS, (pathRoot, paramMap) -> {
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, name);
                });
                // CODE:NAME:config
                this.put(ExConstant.CELL.CODE_NAME_CONFIG, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, code) + File.pathSeparator + name;
                });
                // CODE:class
                this.put(ExConstant.CELL.CODE_CLASS, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    return Ut.ioPath(pathRoot, code);
                });
                // NAME_ABBR:config
                this.put(ExConstant.CELL.NAME_ABBR_CONFIG, (pathRoot, paramMap) -> {
                    final String nameAbbr = paramMap.get("nameAbbr");
                    return Ut.ioPath(pathRoot, nameAbbr);
                });
            }
        };

    @Override
    @SuppressWarnings("all")
    public Object to(final Object value, final ConcurrentMap<String, String> paramMap) {
        final String valueExpr = value.toString();
        final String path = this.getPath(valueExpr, paramMap);
        if (valueExpr.endsWith(VName.CLASS)) {
            final JsonArray content = Ut.ioJArray(path);
            if (VValue.ONE == content.size()) {
                return content.getString(VValue.IDX);
            } else {
                return value;
            }
        } else if (valueExpr.endsWith(VName.CONFIG)) {
            // 此处直接提取字符串
            return Ut.ioString(path);
        } else {
            return value;
        }
    }

    private String getPath(final String value, final ConcurrentMap<String, String> paramMap) {
        Objects.requireNonNull(value);
        final String pathRoot = paramMap.get(KName.DIRECTORY);
        final String field = paramMap.get(KName.FIELD);

        final BiFunction<String, ConcurrentMap<String, String>, String> exprFn =
            PATH_FN.getOrDefault(value.trim(), null);
        if (Objects.isNull(exprFn)) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        final String filepath = exprFn.apply(pathRoot, paramMap);
        return Ut.ioPath(filepath, field) + VString.DOT + VPath.SUFFIX.JSON;
    }
}

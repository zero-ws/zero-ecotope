package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.support.Ut;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
public class ExValueExpr implements ExValue {

    private static final ConcurrentMap<String, BiFunction<String, ConcurrentMap<String, String>, String>> PATH_FN =
        new ConcurrentHashMap<>() {
            {
                // CODE:config
                this.put(ExcelConstant.CELL.CODE_CONFIG, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    return Ut.ioPath(pathRoot, code);
                });
                // NAME:config
                this.put(ExcelConstant.CELL.NAME_CONFIG, (pathRoot, paramMap) -> {
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, name);
                });
                // NAME:class
                this.put(ExcelConstant.CELL.NAME_CLASS, (pathRoot, paramMap) -> {
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, name);
                });
                // CODE:NAME:config
                this.put(ExcelConstant.CELL.CODE_NAME_CONFIG, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, code) + File.separator + name;
                });
                // CODE:NAME:class
                this.put(ExcelConstant.CELL.CODE_NAME_CLASS, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    final String name = paramMap.get(KName.NAME);
                    return Ut.ioPath(pathRoot, code) + File.separatorChar + name;
                });
                // CODE:class
                this.put(ExcelConstant.CELL.CODE_CLASS, (pathRoot, paramMap) -> {
                    final String code = paramMap.get(KName.CODE);
                    return Ut.ioPath(pathRoot, code);
                });
                // NAME_ABBR:config
                this.put(ExcelConstant.CELL.NAME_ABBR_CONFIG, (pathRoot, paramMap) -> {
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
        final String code = paramMap.get(KName.CODE);
        final String field = paramMap.get(KName.FIELD);
        final BiFunction<String, ConcurrentMap<String, String>, String> exprFn =
            PATH_FN.getOrDefault(value.trim(), null);
        if (Objects.isNull(exprFn)) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        final String filepath = exprFn.apply(pathRoot, paramMap);
        
        // 判断pathRoot路径最后的目录是否与code同名，同名则直接拼接避免智能去重
        final String normalizedRoot = pathRoot.replace("\\", "/");
        final List<String> pathSegments = Arrays.stream(normalizedRoot.split("/"))
            .filter(s -> !s.isEmpty())
            .toList();
        
        if (!pathSegments.isEmpty() && code != null && pathSegments.getLast().equals(code)) {
            // 同名情况：直接拼接，避免Ut.ioPath的智能去重
            return normalizedRoot + "/" + code + "/" + field + VString.DOT + VValue.SUFFIX.JSON;
        } else {
            // 不同名：使用原有逻辑
            return Ut.ioPath(filepath, field) + VString.DOT + VValue.SUFFIX.JSON;
        }
    }
}

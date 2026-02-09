package io.zerows.plugins.excel.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.excel.ExcelConstant;
import io.zerows.plugins.excel.metadata.ExRecord;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2024-06-13
 */
class ExExprRecord implements ExExpr {
    @Override
    public JsonObject parse(final ExRecord record) {
        final ConcurrentMap<String, String> paramMap = this.pickParam(record);


        // CURRENT 可以直接从 ExRecord -> ExTable 中提取，此处可忽略，主要是先构造 paramMap（第三参）
        // FIX: 经典BUG / java.util.ConcurrentModificationException
        new HashSet<>(record.keySet()).forEach(field -> {
            final Object value = record.get(field);
            final ExValue component = this.pickComponent(value);
            // 必须传递 field
            paramMap.put(KName.FIELD, field);
            final Object result = component.to(value, paramMap);
            record.put(field, result);
            // Fix: Legacy 临时解决方案
            if (KName.KEY.equals(field)) {
                record.put(KName.ID, result);
            }
        });
        return record.toJson();
    }

    private ConcurrentMap<String, String> pickParam(final ExRecord record) {
        final ConcurrentMap<String, String> paramMap = new ConcurrentHashMap<>();
        // 特殊参数
        final String directory = record.refTable().getDirectory();
        if (Ut.isNotNil(directory)) {
            paramMap.put(KName.DIRECTORY, directory);
        }
        // 记录专用参数
        /*
         * - name
         * - code
         * - nameAbbr
         */
        Arrays.stream(ExcelConstant.CELL.PARAM_INPUT).forEach(paramName -> {
            final String paramValue = record.get(paramName);
            if (Ut.isNotNil(paramValue)) {
                paramMap.put(paramName, paramValue);
            }
        });
        return paramMap;
    }

    private ExValue pickComponent(final Object value) {
        if (Objects.isNull(value)) {
            // null -> PureValue
            return ExValue.of(ExValuePure::new);
        }

        final String valueLiteral = value.toString().trim();
        /*
         * MATCH ->
         * - {UUID}
         * - PWD
         * - CODE:config
         * - NAME:config
         * - CODE:class
         * - CODE:NAME:config
         */
        final Supplier<ExValue> valueFn = ExValueFn.FN_MATCH.get(valueLiteral);
        if (Objects.nonNull(valueFn)) {
            return ExValue.of(valueFn);
        }

        /*
         * PREFIX ->
         * - JSON:
         * - FILE:
         */
        final String found = Arrays.stream(ExcelConstant.CELL.PREFIX)
            .filter(valueLiteral::startsWith)
            .findFirst().orElse(null);
        if (Objects.isNull(found)) {
            return ExValue.of(ExValuePure::new);
        } else {
            return ExValue.of(ExValueFn.FN_PREFIX.get(found));
        }
    }
}

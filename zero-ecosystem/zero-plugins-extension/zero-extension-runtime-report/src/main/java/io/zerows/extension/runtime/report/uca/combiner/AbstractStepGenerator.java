package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;

import java.util.function.Consumer;

/**
 * @author lang : 2024-11-25
 */
public abstract class AbstractStepGenerator implements StepGenerator {

    private final RGeneration generation;

    protected AbstractStepGenerator(final RGeneration generation) {
        this.generation = generation;
    }

    static StepGenerator of(final RGeneration generation, final Class<?> implCls) {
        return CC_SKELETON.pick(() -> Ut.instance(implCls, generation),
            implCls.getName() + VString.SLASH + generation.key());
    }

    public RGeneration metadata() {
        return this.generation;
    }

    /**
     * 特殊方法提取字段
     *
     * @param data       基础参数数据
     * @param exprField  表达式字段
     *                   1. 如果包含了 `` 的前后结尾，则直接使用 {@link Ut#fromExpression} 函数解析
     *                   2. 如果不包含 ` 则直接将其作为字段提取
     * @param consumerFn 消费函数
     */
    @SuppressWarnings("unchecked")
    protected <T> void parseAndExtract(final JsonObject data, final String exprField,
                                       final Consumer<T> consumerFn) {
        if (Ut.isNil(exprField)) {
            return;
        }
        final String trimExpr = exprField.trim();
        final T value;
        if (trimExpr.startsWith("`") && trimExpr.endsWith("`")) {
            value = (T) Ut.fromExpression(trimExpr, data);
        } else {
            value = (T) Ut.valueString(data, trimExpr);
        }
        consumerFn.accept(value);
    }
}

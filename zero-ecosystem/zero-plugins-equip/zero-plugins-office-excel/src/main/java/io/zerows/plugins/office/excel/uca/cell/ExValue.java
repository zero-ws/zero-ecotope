package io.zerows.plugins.office.excel.uca.cell;

import io.r2mo.typed.cc.Cc;
import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/*
 * Cell Processing for value
 */
@FunctionalInterface
public interface ExValue {

    Cc<String, ExValue> CCT_VALUE = Cc.openThread();

    static ExValue of(final Supplier<ExValue> constructorFn) {
        return CCT_VALUE.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    /**
     * 带参数的值转换流程
     *
     * @param value    原始值
     * @param paramMap 参数表
     *
     * @return 转换后的值
     */
    Object to(Object value, ConcurrentMap<String, String> paramMap);

    default OLog logger() {
        return Ut.Log.plugin(this.getClass());
    }
}

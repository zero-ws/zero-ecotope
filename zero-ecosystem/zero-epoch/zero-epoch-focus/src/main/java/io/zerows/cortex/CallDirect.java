package io.zerows.cortex;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.zerows.support.Fx;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
class CallDirect implements Invoker.Action {

    /**
     * <pre>
     * 🧨 直接执行 (Direct Execution)
     * 使用 Java 反射机制直接执行目标方法。
     *
     * 1. ⚡ 性能优化 (Performance Optimization):
     *    此实现绕过了 `Ut.invoke` 中复杂的参数解析逻辑。
     *    在原始设计中，`Ut.invoke` 会在运行时分析元数据，对于当前特定场景而言，
     *    这种开销是不必要的。
     *
     * 2. 🐛 遗留问题修复 (Legacy Issue Resolution):
     *    解决了旧代码中发现的“二次调用”或“重复调用”问题。
     *    通过直接信任传入的参数，我们确保方法仅被执行一次，
     *    避免了可能导致多次执行的回退或重试逻辑。
     * </pre>
     *
     * @param proxy  实例对象
     * @param method 待调用的方法
     * @param args   方法参数
     * @param <T>    返回类型
     * @return 方法调用结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(final Object proxy, final Method method, final Object... args) {
        try {
            return (T) method.invoke(proxy, args);
        } catch (final InvocationTargetException | IllegalAccessException ex) {
            log.error(ex.getMessage(), ex);
            final WebException found = Fx.failAt(ex);
            if (Objects.isNull(found)) {
                throw new _500ServerInternalException(ex.getMessage());
            }
            throw found;
        }
    }
}

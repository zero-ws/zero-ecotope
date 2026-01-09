package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.cosmic.plugins.validation.Rigor;
import io.zerows.cosmic.plugins.validation.ValidatorEntry;
import io.zerows.epoch.annotations.Codex;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
class SentryPreCodex implements Sentry.Pre {

    private static final Cc<String, ValidatorEntry> CC_VALIDATOR = Cc.openThread();
    private transient final ValidatorEntry verifier = CC_VALIDATOR.pick(ValidatorEntry::new);

    @Override
    public void verify(final RoutingContext context, final WebRequest wrapRequest,
                       final Object[] parsed) {
        // 构造验证规则
        final Map<String, List<WebRule>> rulers = this.verifier.buildRulers(wrapRequest);
        if (rulers.isEmpty()) {
            // 无对应验证规则
            return;
        }

        final Kv<Integer, Class<?>> found = this.findParameter(wrapRequest.getEvent().getAction());
        if (Objects.isNull(found.value())) {
            // 直接跳过，不继续验证
            return;
        }

        final Rigor rigor = Rigor.get(found.value());
        if (Objects.isNull(rigor)) {
            log.warn("[ ZERO ] ( Web ) 无法找到符合类型的 type = {} 对应 Rigor 组件！", found.value());
            return;
        }
        final Object value = parsed[found.key()];
        final WebException error = rigor.verify(rulers, value);
        if (Objects.isNull(error)) {
            return;
        }

        throw error;
    }

    /**
     * 根据基本规则，一个方法只能带有一个 Codex 注解，因为 Codex 注解是服务于请求体的整体校验，核心在于
     * <pre>
     *     自定义扩展注解
     *     - {@link jakarta.ws.rs.BeanParam}
     *     - {@link jakarta.ws.rs.extension.BodyParam}
     *     - {@link jakarta.ws.rs.extension.StreamParam}
     * </pre>
     * 如果方法带有多个注解会直接启动报错，扫描时会验证，所以此处只会存在一个 Codex 注解，直接返回 {@link Kv} 即可
     *
     * @param method 目标方法
     * @return 参数索引与类型键值对
     */
    private Kv<Integer, Class<?>> findParameter(final Method method) {
        int index = 0;
        final Kv<Integer, Class<?>> result = Kv.create();
        for (final Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Codex.class)) {
                result.set(index, parameter.getType());
                break;
            }
            index++;
        }
        return result;
    }
}

package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cosmic.plugins.validation.ValidatorEntry;
import io.zerows.epoch.annotations.Validated;

import java.util.function.Supplier;

/**
 * JSR330 signal
 */
public interface Sentry<Context> {
    Cc<String, ValidatorEntry> CC_VALIDATOR = Cc.openThread();
    Cc<String, Sentry.Pre> CC_PRE = Cc.openThread();

    static ValidatorEntry of() {
        return CC_VALIDATOR.pick(ValidatorEntry::new);
    }

    static Sentry.Pre ofPre(final Supplier<Sentry.Pre> constructorFn) {
        return CC_PRE.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    /**
     * @param wrapRequest WrapRequest instance for before validator
     * @return Handler for Context
     */
    Handler<Context> signal(WebRequest wrapRequest);

    /**
     * 专用于检查预处理阶段的参数合法性的前置组件，主要用于验证，配合 Bean Validation 以及 Hibernate Validator 实现参数的各种校验功能，
     * 除开那些组件以外，还包括 {@link Validated} 注解的支持，这种注解可直接验证 {@link JsonObject} 以及 {@link JsonArray} 类型的参数
     * 的相关细节，实现对请求参数的全面检查。
     */
    interface Pre {

        /**
         * 检查专用前置方法
         *
         * @param context     RoutingContext 上下文
         * @param wrapRequest 封装之后的请求（带有 meta 定义）
         * @param parsed      解析过的参数
         */
        void verify(RoutingContext context, WebRequest wrapRequest, Object[] parsed);
    }
}

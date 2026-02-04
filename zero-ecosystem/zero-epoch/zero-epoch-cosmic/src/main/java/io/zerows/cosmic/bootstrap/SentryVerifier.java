package io.zerows.cosmic.bootstrap;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.epoch.web.Account;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Sentry 组件，专用于验证请求参数，对接 JSR 310 的 Bean Validation 规范
 */
@Slf4j
public class SentryVerifier extends AimBase implements Sentry<RoutingContext> {

    /**
     * 启动阶段就提取规则集合，其中包括
     * <pre>
     *     1. Bean Validation 基本规则
     *     2. Codex 注解规则
     *     3. Codex 扩展规则处理
     * </pre>
     * 验证流程
     * <pre>
     *     1. {@link SentryPrePure} 做纯参数校验
     *     2. {@link SentryPreUpload} 做上传参数校验
     * </pre>
     *
     * @param wrapRequest WrapRequest 待验证的请求信息
     * @return Handler for Context
     */
    @Override
    public Handler<RoutingContext> signal(final WebRequest wrapRequest) {
        return (context) -> {
            try {
                // 认证结果
                final User logged = context.user();
                if (Objects.nonNull(logged)) {
                    // 有账号状态，先做 Token 检查
                    final Future<UserAt> userAsync = Account.userAt(logged);
                    userAsync.onComplete(userAt -> {
                        // 放入上下文，供后续使用
                        context.put(UserAt.class.getName(), userAt);
                        // 执行前置校验
                        this.executePre(context, wrapRequest);
                    });
                } else {
                    // 无账号状态
                    this.executePre(context, wrapRequest);
                }
            } catch (final WebException error) {
                // Bad Request 返回 400 异常分流
                AckFlow.replyError(context, error, wrapRequest.getEvent());
            } catch (final Throwable ex) {
                // DEBUG: 特殊流程
                log.error(ex.getMessage(), ex);
                context.fail(ex);
            }
        };
    }

    private void executePre(final RoutingContext context, final WebRequest wrapRequest) {
        // 参数解析
        final Object[] parsed = this.buildArgs(context, wrapRequest.getEvent());


        // 校验流程中直接抛出异常，不抛异常才会走到最后
        // Step-01: 纯参数校验
        Sentry.ofPre(SentryPrePure::new).verify(context, wrapRequest, parsed);


        // Step-02: 上传校验 / validated = null
        Sentry.ofPre(SentryPreUpload::new).verify(context, wrapRequest, parsed);


        // Step-03: 规则校验 / validated = null
        Sentry.ofPre(SentryPreCodex::new).verify(context, wrapRequest, parsed);


        // 上述流程中都没有抛出异常，继续往下走
        context.next();
    }
}

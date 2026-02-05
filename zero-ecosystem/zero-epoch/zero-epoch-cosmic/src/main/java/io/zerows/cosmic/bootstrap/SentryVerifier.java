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
            // 1. 准备 UserAt 的异步任务
            final User logged = context.user();
            final Future<UserAt> userAtFuture = (Objects.nonNull(logged))
                ? Account.userAt(logged)
                : Future.succeededFuture(null);

            userAtFuture
                .onSuccess(userAt -> {
                    // 2. 统一处理逻辑
                    try {
                        // 如果获取到了 UserAt，注入上下文
                        if (userAt != null) {
                            context.put(UserAt.class.getName(), userAt);
                        }

                        // 3. 执行前置校验 (此处是同步调用)
                        this.executePre(context, wrapRequest);

                    } catch (final WebException error) {
                        // 4. 捕获业务校验异常 (如 400 Bad Request)
                        // 建议：业务校验失败属于预期内异常，可以用 warn 级别，但也加上统一前缀方便排查
                        log.warn("[ ZERO-ERROR ] ⚠️ 业务校验未通过 (WebException): {}", error.getMessage());
                        AckFlow.replyError(context, error, wrapRequest.getEvent());
                    } catch (final Throwable ex) {
                        // 5. 捕获其他运行时异常 (空指针、类转换等代码错误)
                        log.error("[ ZERO-ERROR ] ❌ 前置校验执行期间发生严重错误", ex);
                        context.fail(ex);
                    }
                })
                .onFailure(ex -> {
                    // 6. 处理 Account.userAt 自身发生的异步错误 (如 DB 连接失败)
                    log.error("[ ZERO-ERROR ] ❌ 异步获取用户账户信息失败 (Account.userAt)", ex);
                    context.fail(ex);
                });
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

package io.zerows.cortex.webflow;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.enums.EmWeb;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;
import jakarta.ws.rs.extension.StreamParam;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Objects;

@Slf4j
public class AtomicMime<T> implements Atomic<T> {

    private static final Cc<String, Atomic<?>> CC_ATOMIC = Cc.openThread();
    private static final Cc<String, Resolver<?>> CC_RESOLVER = Cc.openThread();
    private static final Cc<String, Resolver.Solve<?>> CC_SOLVE = Cc.openThread();

    @Override
    @SuppressWarnings("unchecked")
    public WebEpsilon<T> ingest(final RoutingContext context,
                                final WebEpsilon<T> income)
        throws WebException {
        final WebEpsilon<T> epsilon;
        if (EmWeb.MimeParser.TYPED == income.getMime()) {
            /* 按类型提取响应解析器 **/
            final Atomic<T> atomic = (Atomic<T>) CC_ATOMIC.pick(AtomicTyped::new, AtomicTyped.class.getName());
            epsilon = atomic.ingest(context, income);
        } else if (EmWeb.MimeParser.STANDARD == income.getMime()) {
            /* 系统标准的响应解析器 **/
            final Atomic<T> atomic = (Atomic<T>) CC_ATOMIC.pick(AtomicStandard::new, AtomicStandard.class.getName());
            epsilon = atomic.ingest(context, income);
        } else {
            /* 自定义响应解析器 **/
            final Resolver<T> resolver = this.getResolver(context, income);
            epsilon = resolver.resolve(context, income);
        }
        return epsilon;
    }

    /**
     * <pre>
     * vertx:
     *   mvc:
     *     resolver:
     *       default:
     *       application/xml:
     * </pre>
     * 包含解析器的注解：{@link BodyParam} 和 {@link StreamParam}
     *
     * @param context RoutingContext 路由对象
     * @param income  Zero 定义的 {@link WebEpsilon} 对象
     * @return 解析器
     */
    @SuppressWarnings("unchecked")
    private Resolver<T> getResolver(final RoutingContext context,
                                    final WebEpsilon<T> income) {
        /* 1. 先提取 Resolver 组件 **/
        final Annotation annotation = income.getAnnotation();
        final Class<?> resolverCls = Ut.invoke(annotation, YmSpec.vertx.mvc.resolver.__);
        final String header = context.request().getHeader(HttpHeaders.CONTENT_TYPE);


        /* 2. 查看是否是默认定制，若是 ResolverUnset.class 证明未定义 **/
        if (ResolverUnset.class == resolverCls) {


            /*
             * 新配置处理
             * 1. 如果配置 = null，直接使用默认解析器
             * 2. 根据 Content-Type 选择解析器
             *    - Content-Type = null，直接提取 default 解析器
             *    - 其他 Content-Type，根据配置选择
             */
            final HConfig config = NodeStore.findInfix(context.vertx(), EmApp.Native.MVC);
            if (Objects.isNull(config)) {
                // ❌️ 此处可能出现类型不兼容的转型错误，由于并非自定义，所以默认只能使用 ResolverJson
                final Resolver<T> resolver = (Resolver<T>) CC_RESOLVER.pick(ResolverJson::new, ResolverJson.class.getName());
                log.info("[ ZERO ] ( Resolver ) 选择（不兼容）解析器 {} / Content-Type = {}, 请求地址：{}",
                    resolver, header, context.request().absoluteURI());
                return resolver;
            }


            final String resolver;
            if (null == header) {
                resolver = AtomicResolver.ofResolver(config);
                log.info("[ ZERO ] ( Resolver ) 选择 [DEFAULT] 默认解析器 {} / Content-Type = null , 请求地址：{}",
                    resolver, context.request().absoluteURI());
            } else {
                final MediaType type = MediaType.valueOf(header);
                resolver = AtomicResolver.ofResolver(config, type);
                log.info("[ ZERO ] ( Resolver ) 选择解析器 {} / Content-Type = {}, 请求地址：{}",
                    resolver, header, context.request().absoluteURI());
            }
            return (Resolver<T>) CC_RESOLVER.pick(() -> Ut.instance(resolver), resolver);
        } else {
            log.info("[ ZERO ] ( Resolver ) 从注解配置中选择解析器 \"{}\" / Content-Type = {}", resolverCls, header);
            if (Ut.isImplement(resolverCls, Resolver.class)) {
                /*
                 * 如果 resolverCls 实现了 Resolver 接口，直接返回实例
                 */
                return (Resolver<T>) CC_RESOLVER.pick(() -> Ut.instance(resolverCls), resolverCls.getName());
            } else {
                /*
                 * 细粒度解析器，这种模式下会将更加细粒度的内容处理到 Resolver 中，核心逻辑在于解析 String 类型的响应内容
                 */
                final Resolver<T> resolver = (Resolver<T>) CC_RESOLVER.pick(() -> Ut.instance(ResolverForSolve.class), ResolverForSolve.class.getName());
                final Resolver.Solve<T> solve = (Resolver.Solve<T>) CC_SOLVE.pick(() -> Ut.instance(resolverCls), resolverCls.getName());
                Ut.contract(resolver, Resolver.Solve.class, solve);
                return resolver;
            }
        }
    }
}

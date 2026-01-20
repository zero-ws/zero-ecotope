package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.annotations.Format;
import io.zerows.epoch.assembly.DiProxyInstance;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.web.Envelop;
import io.zerows.program.Ux;
import jakarta.ws.rs.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public abstract class AimAsync extends AimBase {

    protected Future<Envelop> invokeAsync(final RoutingContext context,
                                          final WebEvent event) {
        final Object proxy = event.getProxy();
        /*
         * Method arguments building here.
         */
        final Object[] arguments = this.buildArgs(context, event);
        /*
         * Whether it's interface mode or agent mode
         */
        final Future<Envelop> invoked;
        if (proxy instanceof DiProxyInstance) {
            final Method method = event.getAction();
            final JsonObject message;
            final Format format = method.getDeclaredAnnotation(Format.class);
            // 判断是否开启了“智能模式”（JAX-RS 键值对映射）
            if (Objects.nonNull(format) && format.smart()) {
                // 新模式：使用注解名或参数名作为 Key
                message = this.compileArgs(method, arguments);
            } else {
                // 旧模式：使用数字索引 0, 1, 2... 作为 Key
                message = this.compileArgs(arguments);
            }
            /*
             * Interface 模式，必须在调用的时候处理
             */
            invoked = AckFlow.nextT(context, message);
        } else {
            /*
             * Agent mode
             */
            final Object returnValue = this.invoke(event, arguments);
            invoked = AckFlow.nextT(context, returnValue);
        }

        return invoked.compose(response -> {
            /*
             * The next method of compose for future building assist data such as
             * Headers,
             * User,
             * Session
             * Context
             * It's critical for Envelop object when communication
             */
            response.bind(context);
            return Ux.future(response);
        });
    }

    private JsonObject compileArgs(final Object[] arguments) {
        final JsonObject message = new JsonObject();
        for (int idx = 0; idx < arguments.length; idx++) {
            message.put(String.valueOf(idx), arguments[idx]);
        }
        return message;
    }

    private JsonObject compileArgs(final Method method, final Object[] arguments) {
        final JsonObject message = new JsonObject();
        if (arguments == null || arguments.length == 0) {
            return message;
        }

        final Parameter[] parameters = method.getParameters();
        final Annotation[][] paramAnnos = method.getParameterAnnotations();

        for (int idx = 0; idx < arguments.length; idx++) {
            // 1. 尝试从 JAX-RS 注解中提取名称
            String name = this.extractName(paramAnnos[idx]);

            // 2. 如果没有注解，则降级使用反射名 (arg0, arg1 或 开启了 -parameters 后的真实名)
            if (name == null || name.isBlank()) {
                name = parameters[idx].getName();
            }

            // 3. 将参数值放入消息体，处理 null 值避免 JsonObject 抛错
            message.put(name, arguments[idx]);
        }
        return message;
    }

    private String extractName(final Annotation[] annotations) {
        if (annotations == null) {
            return null;
        }
        for (final Annotation annotation : annotations) {
            // 优先级处理：OAuth2 常用 FormParam
            if (annotation instanceof FormParam) {
                return ((FormParam) annotation).value();
            }
            if (annotation instanceof QueryParam) {
                return ((QueryParam) annotation).value();
            }
            if (annotation instanceof HeaderParam) {
                return ((HeaderParam) annotation).value();
            }
            if (annotation instanceof PathParam) {
                return ((PathParam) annotation).value();
            }
            if (annotation instanceof CookieParam) {
                return ((CookieParam) annotation).value();
            }
        }
        return null;
    }
}

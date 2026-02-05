package io.zerows.cosmic.plugins.validation;

import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.cosmic.plugins.validation.exception._60000Exception400Validation;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.WebApi;
import io.zerows.management.OCacheStore;
import io.zerows.support.Ut;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ValidatorEntry {

    private static final ConcurrentMap<String, Map<String, List<WebRule>>>
        RULERS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, JsonObject> STORED = OCacheStore.CC_CODEX.get();

    private static final ValidatorInterpolator INTERPOLATOR = new ValidatorInterpolator();
    @SuppressWarnings("all")
    private static final Validator VALIDATOR = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(INTERPOLATOR)
        .buildValidatorFactory()
        .getValidator();

    /**
     * Validate the method parameters based on javax.validation: Hibernate Validator.
     *
     * @param proxy  The checked object.
     * @param method The checked method.
     * @param args   The checked method's parameters.
     * @param <T>    The object type: Generic types.
     */
    public <T> void verifyMethod(
        final T proxy,
        final Method method, //这是那个“无关类”的方法
        final Object[] args) {
        // 1. Get method validator
        final ExecutableValidator validatorParam = VALIDATOR.forExecutables();

        // 2. 尝试查找接口上的对应方法
        // 如果是动态代理且能通过 @Address 找到接口方法，则使用接口方法；否则降级使用原始方法
        final Method methodToUse = this.findInterfaceMethod(proxy, method, args);

        // ============ 插入 Debug 调用 ============
        // 传入你需要检查的 method 和 最终送进去校验的 realArgs
        // this.debugValidationContext(methodToUse, realArgs);

        // 使用最终确定的 methodToUse 进行校验
        final Set<ConstraintViolation<T>> constraints
            = validatorParam.validateParameters(proxy, methodToUse, args);

        // 4. Throw out exception
        if (!constraints.isEmpty()) {
            final ConstraintViolation<T> item = constraints.iterator().next();
            this.replyError(proxy, methodToUse, item);
        }
    }

    /**
     * [Debug Tool] 打印校验上下文信息
     * 用于排查：Hibernate Validator 不生效、参数错位、注解未读取等问题
     */
    private void debugValidationContext(final Method method, final Object[] args) {
        // 开关：生产环境建议设为 false，或者配合 log level 使用
        final boolean enableDebug = true;
        if (!enableDebug) {
            return;
        }

        System.out.println("\n====== [DEBUG] Validation Context Start ======");

        if (method == null) {
            System.out.println("!!! Critical Error: Method is NULL !!!");
            System.out.println("====== [DEBUG] Validation Context End ======\n");
            return;
        }

        System.out.println("1. 目标方法: " + method.getDeclaringClass().getSimpleName() + "#" + method.getName());

        final int paramCount = method.getParameterCount();
        final int argCount = args == null ? 0 : args.length;

        System.out.println("2. 参数数量对比: 接口定义[" + paramCount + "] vs 实际传入[" + argCount + "]");
        if (paramCount != argCount) {
            System.out.println("   [警告] ⚠️ 参数数量不匹配！校验器可能会报错或忽略校验。");
        }

        // 遍历最大长度，展示对齐情况
        final int maxLoop = Math.max(paramCount, argCount);
        for (int i = 0; i < maxLoop; i++) {
            System.out.println("   --- Index [" + i + "] ---");

            // A. 打印传入的实参 (Args)
            if (i < argCount) {
                final Object val = args[i];
                System.out.println("   [传入值] : " + (val == null ? "null" : "[" + val + "]"));
                System.out.println("   [值类型] : " + (val == null ? "N/A" : val.getClass().getSimpleName()));
            } else {
                System.out.println("   [传入值] : <缺失> (Args 长度不足)");
            }

            // B. 打印接口定义的形参 (Params) 及注解
            if (i < paramCount) {
                final Parameter p = method.getParameters()[i];
                final Annotation[] anns = p.getAnnotations();
                if (anns.length == 0) {
                    System.out.println("   [注解列表]: (无)");
                } else {
                    for (final Annotation a : anns) {
                        // 打印注解简名，方便查看
                        System.out.println("   [注解发现]: @" + a.annotationType().getSimpleName());
                        // 如果需要看全限定名，可以用 a.annotationType().getName()
                    }
                }
            } else {
                System.out.println("   [接口定义]: <无> (传入了多余的参数)");
            }
        }
        System.out.println("====== [DEBUG] Validation Context End ======\n");
    }

    /**
     * 辅助方法：通过 @Address 注解在动态代理的接口中查找对应的方法。
     * <p>
     * 解决问题：Hibernate Validator 在校验 JDK 动态代理时，必须传入接口上的 Method 对象，
     * 否则无法读取接口上定义的 @NotNull 等约束注解。
     *
     * @param proxy        代理对象
     * @param sourceMethod 原始方法（通常来自 Controller/Web 层）
     * @param args         方法参数值（用于校验参数数量匹配）
     * @return 找到的接口 Method 对象，如果没有找到或不符合条件则返回 null
     */
    private Method findInterfaceMethod(final Object proxy, final Method sourceMethod, final Object[] args) {
        // 快速失败检查：如果不是动态代理，直接跳过
        if (proxy == null || !Proxy.isProxyClass(proxy.getClass())) {
            return sourceMethod;
        }

        try {
            // 1. 获取源方法上的 Address 值
            final Address sourceAddress = sourceMethod.getDeclaredAnnotation(Address.class);
            if (sourceAddress == null) {
                return sourceMethod; // 源方法没有 @Address，无法匹配
            }

            final String targetKey = sourceAddress.value();

            // 2. 遍历代理对象的所有接口
            final Class<?>[] interfaces = proxy.getClass().getInterfaces();

            for (final Class<?> interfaceClazz : interfaces) {
                // 3. 遍历接口的所有方法
                for (final Method interfaceMethod : interfaceClazz.getMethods()) {
                    // 4. 检查接口方法是否有 @Address
                    final Address destAddress = interfaceMethod.getAnnotation(Address.class);

                    // 5. 比对 Address 的值是否相等
                    if (destAddress != null && targetKey.equals(destAddress.value())) {

                        // 6. [安全性检查] 参数数量必须一致
                        // 只有参数数量匹配，Hibernate Validator 才能正常工作
                        final int argsCount = (args == null) ? 0 : args.length;
                        if (interfaceMethod.getParameterCount() == argsCount) {
                            return interfaceMethod; // 找到了！
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            // 容错处理：记录日志，防止查找过程中的反射异常打断主流程
            log.warn("Address mapping failed during validation lookup: {}", ex.getMessage());
        }
        return sourceMethod; // 未找到
    }

    private <T> void replyError(final T proxy, final Method method, final ConstraintViolation<T> item) {
        // 1. 卫语句：减少一层缩进
        if (item == null) {
            return;
        }

        // 2. 国际化消息处理
        String message = item.getMessage();
        if (message.startsWith("${") && message.endsWith("}")) {
            final String key = message.substring(2, message.length() - 1);
            final String resolved = INTERPOLATOR.findMessage(key, Locale.SIMPLIFIED_CHINESE);
            if (Ut.isNotNil(resolved)) {
                message = resolved;
            }
        }

        // 3. 定位目标参数对象
        Parameter targetParam = null;
        for (final Path.Node node : item.getPropertyPath()) {
            if (node.getKind() == ElementKind.PARAMETER) {
                final int index = node.as(Path.ParameterNode.class).getParameterIndex();
                if (index < method.getParameterCount()) {
                    targetParam = method.getParameters()[index];
                }
                break; // 找到即跳出
            }
        }

        // 4. 解析参数来源类型 (Source Type) 和 参数名 (Param Name)
        String sourceType = "Body"; // 默认来源
        String paramName = "unknown";

        if (targetParam != null) {
            // 优先判断 JAX-RS 注解，确定参数来源
            if (targetParam.isAnnotationPresent(HeaderParam.class)) {
                sourceType = "Header";
                paramName = targetParam.getAnnotation(HeaderParam.class).value();
            } else if (targetParam.isAnnotationPresent(QueryParam.class)) {
                sourceType = "Query";
                paramName = targetParam.getAnnotation(QueryParam.class).value();
            } else if (targetParam.isAnnotationPresent(PathParam.class)) {
                sourceType = "Path";
                paramName = targetParam.getAnnotation(PathParam.class).value();
            } else if (targetParam.isAnnotationPresent(FormParam.class)) {
                sourceType = "Form";
                paramName = targetParam.getAnnotation(FormParam.class).value();
            } else if (targetParam.isAnnotationPresent(CookieParam.class)) {
                sourceType = "Cookie";
                paramName = targetParam.getAnnotation(CookieParam.class).value();
            } else {
                // 无注解，通常是 Body 或 Context
                sourceType = "Body";
                paramName = targetParam.getName();
            }
        } else {
            // 兜底：如果无法通过反射获取参数，使用原始路径
            final String fullPath = item.getPropertyPath().toString();
            paramName = fullPath.contains(".")
                ? fullPath.substring(fullPath.lastIndexOf(".") + 1)
                : fullPath;
        }

        // 5. 格式化最终消息：[来源 参数名] 错误信息
        // 示例: "[Header X-App-Id] 不能为 null"
        final String finalMessage = String.format("[%s %s] %s", sourceType, paramName, message);

        throw new _60000Exception400Validation(proxy.getClass(), method, finalMessage);
    }

    /**
     * Advanced ruler building for Body content validation based on yml configuration.
     *
     * @param wrapRequest The container to contains event, configuration, ruler.
     * @return The configured rulers.
     */
    public Map<String, List<WebRule>> buildRulers(
        final WebRequest wrapRequest) {
        final Map<String, List<WebRule>> rulers
            = new LinkedHashMap<>();
        final ConcurrentMap<String, Class<? extends Annotation>>
            annotions = wrapRequest.getAnnotations();
        Observable.fromIterable(annotions.keySet())
            .filter(KWeb.ARGS.MIME_DIRECT::equals)
            .map(annotions::get)
            // 1. Check whether contains @BodyParam
            .any(item -> BodyParam.class == item || BeanParam.class == item)
            // 2. Build rulers
            .map(item -> WebApi.nameOf(wrapRequest.getEvent()))
            .map(this::buildRulers)
            .subscribe(rulers::putAll).dispose();
        return rulers;
    }

    private Map<String, List<WebRule>> buildRulers(final String key) {
        if (RULERS.containsKey(key)) {
            return RULERS.get(key);
        }

        final JsonObject rule = STORED.get(key); // ZeroCodex.getCodex(key);
        final Map<String, List<WebRule>> ruler = new LinkedHashMap<>();
        if (null != rule) {
            Ut.itJObject(rule, (value, field) -> {
                // Checked valid rule config
                final List<WebRule> rulers = this.buildRulers(value);
                if (!rulers.isEmpty()) {
                    ruler.put(field, rulers);
                }
            });
            if (!ruler.isEmpty()) {
                RULERS.put(key, ruler);
            }
        }
        return ruler;
    }

    private List<WebRule> buildRulers(final Object config) {
        final List<WebRule> rulers = new ArrayList<>();
        if (config instanceof final JsonArray configData) {
            Ut.itJArray(configData, JsonObject.class, (item, index) -> {
                final WebRule ruler = WebRule.create(item);
                rulers.add(ruler);
            });
        }
        return rulers;
    }
}

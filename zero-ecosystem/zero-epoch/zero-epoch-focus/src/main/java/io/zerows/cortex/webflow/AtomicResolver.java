package io.zerows.cortex.webflow;

import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.WebEpsilon;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.core.MediaType;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <strong>原子解析器查找工具</strong>
 *
 * <h3>🚀 基本介绍</h3>
 * <pre>
 * 该类用于根据 {@link MediaType} 查找对应的参数解析器 (Param Resolver) 实现类名。
 * </pre>
 *
 * <h3>📌 核心逻辑</h3>
 * <pre>
 * 1. ⚙️ 首先尝试从系统配置 {@link HConfig} 中查找用户自定义的解析器映射。
 *    (配置路径: {@code vertx.mvc -> resolver})
 *
 * 2. 🔄 如果未找到配置，则回退到系统内置的默认映射 {@link #CC_RESOLVER}。
 * </pre>
 *
 * <h3>📚 内置映射</h3>
 * <pre>
 * - default:                  {@link ResolverJson} (application/json)
 * - application/json:         {@link ResolverJson}
 * - application/octet-stream: {@link ResolverBuffer}
 * - multipart/form-data:      {@link ResolverForm}
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AtomicResolver {
    private static final ConcurrentMap<String, String> CC_RESOLVER = new ConcurrentHashMap<>() {
        {
            this.put("default", ResolverJson.class.getName());
            this.put(MediaType.APPLICATION_JSON, ResolverJson.class.getName());
            this.put(MediaType.APPLICATION_OCTET_STREAM, ResolverBuffer.class.getName());
            this.put(MediaType.MULTIPART_FORM_DATA, ResolverForm.class.getName());
        }
    };

    /**
     * <h3>🎯 注解解析器提取</h3>
     * <pre>
     * 从 {@link WebEpsilon} 中提取参数上的注解，并解析出对应的 Resolver 实现类。
     *
     * 特殊处理:
     * - 如果注解是 {@link BeanParam}，则返回 {@link ResolverUnset}，
     *   表示后续流程需要进行自动发现或递归解析 Bean 内部字段。
     * - 其他情况通过反射调用注解上的 <code>resolver</code> 属性获取 Resolver 类。
     * </pre>
     *
     * @param income 参数元数据描述对象 {@link WebEpsilon}
     * @param <T>    参数类型泛型
     * @return 解析器实现类 {@link Class}
     */
    static <T> Class<?> ofResolver(final WebEpsilon<T> income) {
        /* 1. 先提取 Resolver 组件 **/
        final Annotation annotation = income.getAnnotation();
        // Fix: 过滤 BeanParam 的处理
        return BeanParam.class == annotation.annotationType()
            ? ResolverUnset.class       // 使用 ResolverUnset 占位触发自动发现
            : Ut.invoke(annotation, YmSpec.vertx.mvc.resolver.__);
    }

    /**
     * <h3>⚙️ 默认解析器</h3>
     * <pre>
     * 获取系统默认的参数解析器类名。
     * 通常用于 Content-Type 未指定或未匹配到特定解析器时的兜底方案。
     * 默认为 JSON 解析器 {@link ResolverJson}。
     * </pre>
     *
     * @param config 系统核心配置
     * @return 解析器全限定类名 (Fully Qualified Class Name)
     */
    static String ofResolver(final HConfig config) {
        final JsonObject resolvers = config.options(YmSpec.vertx.mvc.resolver.__);
        if (Objects.isNull(resolvers)) {
            return CC_RESOLVER.get("default");
        } else {
            return Ut.valueString(resolvers, "default");
        }
    }

    /**
     * <h3>🔍 指定解析器</h3>
     * <pre>
     * 根据 Media Type 获取对应的参数解析器类名。
     * 优先查找配置文件中的定义，如果未找到则使用内置的默认映射。
     * </pre>
     *
     * @param config 系统核心配置
     * @param type   HTTP 请求的 Content-Type {@link MediaType}
     * @return 解析器全限定类名，如果未找到则可能返回 null
     * @throws NullPointerException if {@code type} is null
     */
    static String ofResolver(final HConfig config, final MediaType type) {
        Objects.requireNonNull(type, "[ ZERO ] ( AtomicResolver ) MediaType 不能为空.");
        final String key = type.getType() + "/" + type.getSubtype();

        // 第一优先级
        final JsonObject resolvers = config.options(YmSpec.vertx.mvc.resolver.__);
        final String resolver;
        if (Objects.isNull(resolvers)) {
            // 检索内部库
            resolver = CC_RESOLVER.get(key);
        } else {
            // 检索配置信息
            resolver = Ut.valueString(resolvers, key);
        }
        return resolver;
    }
}

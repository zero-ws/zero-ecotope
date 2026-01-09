package io.zerows.cortex.webflow;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import jakarta.ws.rs.core.MediaType;

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

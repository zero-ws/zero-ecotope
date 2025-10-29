package io.zerows.epoch.metadata.security;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Optional;

/**
 * 安全项相关的方法，根据配置初始化，此处初始化的维度是类型，简单说就是不同类型在配置中出现次数会不同
 * <pre>
 *     1. {@link SecurityType#BASIC}：无配置，固定算法
 *     2. {@link SecurityType#JWT}：当前应用运行时直接保留唯一配置
 *        vertx:
 *          security:
 *            jwt:
 *              options: {@link JsonObject} - JWT 配置项（默认配置）
 *        app:
 *          security:
 *            jwt:
 *              options: {@link JsonObject} - JWT 配置项（覆盖前者）
 *      3. 应用中如果存在配置则直接调用 SecurityManager 执行注册方法替换掉系统默认的，但最终每个 App 也只能保留一份默认的 JWT 配置，
 *         这是当前版本的一个核心限制，不允许出现多种 JWT 配置在某个应用内！
 * </pre>
 * 所以此处的配置池化操作留给上层的 SecurityManager 来负责，而不是在此处进行统一管理，这样 SecurityConfig 就可以演变成纯配置对象，
 * 这种纯对象可直接调用 {@link Data} 的方式进行简化。
 */
@Data
@Accessors(chain = true, fluent = true)
public class SecurityConfig implements Serializable {
    @Setter(AccessLevel.NONE)
    private final JsonObject options = new JsonObject();
    @Setter(AccessLevel.NONE)
    private final String key;
    @Setter(AccessLevel.NONE)
    private final SecurityType type;

    public SecurityConfig(final SecurityType type, final JsonObject options) {
        this.type = type;
        this.key = type.key();
        Optional.ofNullable(options)
            .ifPresent(optionOpt -> this.options.mergeIn(optionOpt, true));
    }
}

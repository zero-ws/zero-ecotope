package io.zerows.epoch.annotations;

import java.lang.annotation.*;

/**
 * 🛡️ 安全墙注解，用于限制请求
 * <pre>
 * 1. 🚫 返回 401 响应 (未授权)
 * 2. 🚫 返回 403 响应 (禁止访问)
 * </pre>
 * 用于安全需求。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Wall {
    /**
     * 🔐 默认安全限制应用于所有请求的路径。
     *
     * @return 将被安全过滤器处理的路径。
     */
    String path() default "/*";

    /**
     * 📌 安全墙的必需标识，它提供以下组件构建
     * <pre>
     * 1. 🛠️ Handler 构建
     * 2. 🏭 Provider 构建
     * </pre>
     * 它映射到 "secure" 配置节点，该节点可以配置以下信息
     * <pre>
     *     此处的 value 直接对应配置中的某个节点，配置中的片段如
     *     security:
     *       master:     （配置主要针对此处）
     *         type:  ??? 决定了 Provider 的类型
     *         options:   传递给 Provider 的配置选项
     * </pre>
     *
     * @return 当前构建所需的标识。
     */
    String value() default "master";

    /**
     * 🔄 安全墙序列的值，用于认证处理器链。
     * 「旧版本」:
     * <pre>
     * 1. 📌 所有安全墙类必须包含不同的标识
     * 2. 🔑 主要安全墙应为 0，其他可为 1, 2, 3。
     * 3. 🔄 安全墙处理器序列应按 0,1,2,3... 触发。
     *    此标识需要多处理器模式。
     * </pre>
     * <p>
     * 「新版本」:
     * <pre>
     * 1. 📁 该顺序可用于将你的安全墙按路径分组，这意味着当一个路径包含 n 个 Aegis 配置时，
     *    可以在这里使用 ChainAuthHandler。请注意，顺序仅用于“分组”，它与路由顺序不同，这里很重要。
     *
     * 2. 🧭 如果你定义了多个授权方法，系统将选择顺序值最小的那个
     * </pre>
     *
     * @return 将被构建到安全链中的处理器顺序值。
     */
    int order() default 0;          // VValue.ZERO;


    String type() default "NONE";        // 预留字段，未来可能会用到
}
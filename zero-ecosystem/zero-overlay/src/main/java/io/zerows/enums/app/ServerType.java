package io.zerows.enums.app;

/**
 * 服务器类型专用枚举，在不同场景定义为主不一样，针对不同容器其服务器的种类也有所区别
 * <pre><code>
 *     - http：      标准HTTP服务器（HTTP的具体版本隐藏在配置中，简单说只要是HTTP那么它对应的服务器的类型都是HTTP）
 *                   至于协议版本是 1、2、3 这个直接在配置中去反应，而不反应到类型中
 *     - sock：      WebSocket服务器
 *     - rx：        RxJava模式下的HTTP服务器，这种模式主要牵涉Vertx中的编程风格
 *     - ipc：       RPC服务器，内部通讯专用
 *     - api：       API网关，微服务模式下的Gateway
 * </code></pre>
 */
public enum ServerType {
    HTTP("http"),
    SOCK("sock"),
    RX("rx"),
    IPC("ipc"),
    API("api"),
    NONE("none");

    private transient final String literal;

    ServerType(final String literal) {
        this.literal = literal;
    }

    public static ServerType of(final String literal) {
        switch (literal) {
            case "http":
                return HTTP;
            case "sock":
                return SOCK;
            case "rx":
                return RX;
            case "ipc":
                return IPC;
            case "api":
                return API;
            default:
                return NONE;
        }
    }

    public String key() {
        return this.literal;
    }

    public boolean match(final String literal) {
        return this.literal.equals(literal);
    }
}

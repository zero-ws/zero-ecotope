package io.zerows.platform.enums;

/**
 * @author lang : 2023-05-31
 */
public final class EmWeb {
    private EmWeb() {
    }

    /*
     * Api -> Channel -> Service
     * Here are many kind of channels and it's used in different income requirements
     */
    public enum Channel {
        /*
         * 「Adaptor」
         *  1）This channel is for X_SOURCE, this component could be cross different database such as
         *     MySQL, Oracle, etc.
         *  2）In this kind of situation, the component major features are "SQL Database Adapting", that's why it's name
         *     is Adaptor.
         *  3) If you involve X_SOURCE, one app could access different database, otherwise, you could use `vertx-jooq.yml`
         *     configuration. When you want to get `DSLContext` stored, you could access it with
         *     `ZPool.create(Database)` method here.
         */
        ADAPTOR,
        /*
         * 「Connector」
         *  1）This channel is for I_API/I_SERVICE, the component should connect different third-part system with
         *     integration components.
         *  2）In this kind of situation, the component just like a "Uniform Connector" to any kind of third-part
         *     system, that's why the name is Connector
         */
        CONNECTOR,
        /*
         * 「Actor」
         *  1）This name came from `Re-Actor` design pattern, it's async background tasks. In zero system ( vert.x ), this
         *     component could be implemented with Worker only.
         *  2）Must be:
         *    2.1）The server should be active and send message to another position here.
         *    2.2）There are two mode: Once / Scheduled / Triggered
         */
        ACTOR,
        /*
         * 「Director」
         *  1）In default situation, the api is bind to `identifier` of model, but this kind of channel may be combine more than
         *     One model such as relations here.
         *  2）You can access different Dao async in this channel and connect to different `Component` here.
         */
        DIRECTOR,

        DEFINE,
    }

    public enum Https {
        /*
         * TLS in Https
         */
        TLS,
        /*
         * SSL in Https
         */
        SSL,
    }

    /*
     * Here are two positions that will be used
     * 1) @Worker annotation, it's used by @Worker component
     * 2) Origin X Engine, stored into `I_API` table of database.
     *
     * In current version, not all below values are supported by zero.
     */
    public enum Exchange {
        /*
         * Common Http Worker here
         * Request -> Response
         */
        REQUEST_RESPONSE,                   // Origin X Available ( Stored )
        REQUEST_MICRO_WORKER,

        /*
         * Common Publisher
         * Publish -> Subscribe
         */
        PUBLISH_SUBSCRIBE,                  // Origin X Available ( Stored )

        /*
         * Micro Discovery Publisher
         * Discovery -> Publish
         */
        DISCOVERY_PUBLISH,

        /*
         * Common Http Worker here
         * Request -> ( Background Workers without response )
         */
        ONE_WAY                             // Origin X Available ( Stored )
    }

    public enum MimeParser {
        RESOLVER,
        TYPED,
        STANDARD
    }

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
}

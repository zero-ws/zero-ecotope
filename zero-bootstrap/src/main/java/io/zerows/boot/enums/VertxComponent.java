package io.zerows.boot.enums;

/**
 * @author lang : 2025-09-26
 */
public enum VertxComponent {
    //             扫描         启动
    INFUSION,   //  o            o         @Infusion    自定义 Plugins 插件
    QUEUE,      //  o            x         @Queue       自定义 Worker 内置
    ENDPOINT,   //  o            x         @EndPoint    自定义 Agent  内置
    WORKER,     //  o            o         @Worker      Verticle Worker
    AGENT,      //  o            o         @Agent       Verticle Agent
    CODEX,      //  x            o         @Codex       验证规则
    IPC,        //  x            o         @Agent       gRPC 专用 ( type = ServerType.IPC )
}

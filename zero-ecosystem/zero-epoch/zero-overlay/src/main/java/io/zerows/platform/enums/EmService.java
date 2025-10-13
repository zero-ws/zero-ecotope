package io.zerows.platform.enums;

/**
 * @author lang : 2024-07-01
 */
public final class EmService {
    private EmService() {
    }

    public enum Context {
        PLUGIN,         // 普通插件上下文，zero-equip
        MODULE,         // 扩展插件上下文，zero-extension
        APP,            // 应用插件上下文，入口专用，提取 HSetting
    }

    public enum JobType {
        ONCE,       // 「Development」Run once
        FIXED,      // runAt,    Run at timestamp based on simple configuration.
        FORMULA,    // runExpr,  Run Formula  ( Support Multi )
    }

    /*
     * State machine moving:
     * STARTING ------|
     *                v
     *     |------> READY <-------------------|
     *     |          |                       |
     *     |          |                     <start>
     *     |          |                       |
     *     |        <start>                   |
     *     |          |                       |
     *     |          V                       |
     *     |        RUNNING --- <stop> ---> STOPPED
     *     |          |
     *     |          |
     * <resume>   ( error )
     *     |          |
     *     |          |
     *     |          v
     *     |------- ERROR
     *
     * 1) The first time when worker initialized the job, job status will be READY
     *    -- in this situation, the first time will be different when status moving
     * 2) For ONCE, FIXED, the job will not run when STARTING
     *    -- 2.1. ONCE job must be triggered by event;
     *    -- 2.2. FIXED job must run after delay xxx duration;
     * 3) For PLAN, the job will run when STARTING, because it will repeat duration sometime
     */
    public enum JobStatus {
        STARTING,
        READY,      // The job could be started
        RUNNING,        // Job is running
        STOPPED,    // Job is stopped
        ERROR,      // Job met error when ran last time
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum NotifyType {
        QUEUE,
        TOPIC,

        BRIDGE,

        /*
         * This method is configuration for `Remind` ( Related to @Subscribe ), as inner zero framework:
         *
         * - @Address means event bus address and you can publish / subscribe on this address.
         *   「FORMAT」: TOPIC://XXX/XXX
         * - @Subscribe is a new annotation for websocket, it means that you can subscribe the topic in
         *   this address instead.
         *   「FORMAT」: /job/notify or /xxx/xxxx,
         *
         *   this get will be converted to `ws://host:port/api/web-socket/stomp` with the topic = /job/notify
         *   instead of others. Here the structure should be following:
         *
         *   -- WebSocket ( /api/web-socket/stomp )
         *        Input                Topic             Message
         *     -- @Address    -->   @Subscribe   -->    <<Client>>
         *
         * Input Data Came from following Source
         *
         * 1) JavaScript Client: StompJs ( Front-End ) Directly
         *    -- SockJs         ( Non Security )
         *    -- SockBridge     ( Non Rbac )
         *    -- StompJs        ( Rbac Supported with zero-ifx-stomp )
         *    -- EventBus       ( Bridge Only )
         * 2) Back-End of RESTful Api
         *    -- EventBus       ( Bridge Only )
         *    -- Job EventBus   ( Bridge Only )
         */
        REMIND,
    }
}

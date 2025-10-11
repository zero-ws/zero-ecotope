package io.zerows.support;

import io.zerows.component.log.LogO;

/**
 * @author lang : 2024-04-17
 */
class _Log extends _Jackson {
    /**
     * 新版日志架构，替换旧版本的 Annal 模式，Annal 模式不支持 OSGI 环境下的日志输出，所以此处进行了替换
     * <pre><code>
     *     1. 执行模块日志相关信息，可设定模块
     *        - 名称：Zero
     *          OSGI
     *          - Service：OSGI 服务专用日志
     *          - Callback：Bundle 回调组件专用日志
     *          - Bundle：Bundle 专用日志
     *          Other
     *          - KMetadata：元数据定义日志
     *          - Boot：启动组件日志
     *          - Configure：配置专用日志
     *          - Command：命令专用日志
     *          - Plugin：插件专用日志
     *          - UCA：自定义组件专用日志
     *          - Job：任务专用日志
     *          - Exception：异常专用日志
     *          - Security：安全专用日志
     *          - Ux：UtilityX 专用日志
     *      2. 旧版调用流程
     *          Annal LOGGER = Annal.get(Xxx.class);
     *         新版调用流程
     *          Ut.Log.metadata(Xxx.class).info("message");
     *      3. OLog 和 Annal 的父类都是 HLogger，所以二者接口完全一致，方便替换；
     * </code></pre>
     */
    public static class Log {
        // ------------ OSGI 专用
        public static LogO callback(final Class<?> clazz) {
            return LogO.of(clazz, "Callback");
        }

        public static LogO service(final Class<?> clazz) {
            return LogO.of(clazz, "Service");
        }

        public static LogO dependency(final Class<?> clazz) {
            return LogO.of(clazz, "Dependency");
        }

        public static LogO bundle(final Class<?> clazz) {
            return LogO.of(clazz, "Bundle");
        }


        // ------------ 元数据部分
        public static LogO metadata(final Class<?> clazz) {
            return LogO.of(clazz, "KMetadata");
        }

        public static LogO configure(final Class<?> clazz) {
            return LogO.of(clazz, "Config");
        }


        // ------------ 启动、后台、工具
        public static LogO boot(final Class<?> clazz) {
            return LogO.of(clazz, "Boot");
        }

        public static LogO job(final Class<?> clazz) {
            return LogO.of(clazz, "Job");
        }

        public static LogO ux(final Class<?> clazz) {
            return LogO.of(clazz, "Ux.???");
        }


        // ------------ 异常、插件、组件
        // 插件 > 组件，组件只位于 uca 包中


        public static LogO exception(final Class<?> clazz) {
            return LogO.of(clazz, "Exception");
        }

        public static LogO plugin(final Class<?> clazz) {
            return LogO.of(clazz, "Plugin");
        }

        public static LogO vertx(final Class<?> clazz) {
            return LogO.of(clazz, "Vertx");
        }

        public static LogO uca(final Class<?> clazz) {
            return LogO.of(clazz, "UCA");
        }

        public static LogO websocket(final Class<?> clazz) {
            return LogO.of(clazz, "WebSocket");
        }


        // ------------ 职责划分（插件为全局、自定义组件为局部，除此之外的模块日志）
        // Command 命令行
        public static LogO command(final Class<?> clazz) {
            return LogO.of(clazz, "Command");
        }

        // Security 安全日志
        public static LogO security(final Class<?> clazz) {
            return LogO.of(clazz, "Security");
        }

        // Database 数据库日志
        public static LogO database(final Class<?> clazz) {
            return LogO.of(clazz, "Database");
        }

        // Cache 缓存日志
        public static LogO cache(final Class<?> clazz) {
            return LogO.of(clazz, "Cache");
        }

        // Test 测试日志
        public static LogO test(final Class<?> clazz) {
            return LogO.of(clazz, "Test");
        }

        // Data 数据日志
        public static LogO data(final Class<?> clazz) {
            return LogO.of(clazz, "Data");
        }

        // Energy 专用日志
        public static LogO energy(final Class<?> clazz) {
            return LogO.of(clazz, "Energy");
        }

        // Invoke 专用（方法执行）
        public static LogO invoke(final Class<?> clazz) {
            return LogO.of(clazz, "Invoke");
        }

    }
}

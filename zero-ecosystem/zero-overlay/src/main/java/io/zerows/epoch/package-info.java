/**
 * overlay - 含义：覆盖叠加，此包为 Zero 生态的对接层，用于对接底层的 r2mo-rapid 新框架
 * <pre>
 *     项目地址：https://gitee.com/silentbalanceyh/r2mo-rapid
 *     子项目：https://gitee.com/silentbalanceyh/r2mo-rapid/tree/master/r2mo-vertx
 * </pre>
 * 底层对接 vertx 之后就可以使用 vertx 中的各项能力，同时为 Zero 系统提供底层的能力支撑，overlay 项目中主要包含：
 * <pre>
 *     1. Zero 的常量定义
 *     2. Zero 中的核心枚举定义（只限于 Core）
 *     3. 第三方扩展，典型如 Jackson 的扩展
 *     4. 运行时的管理模型
 * </pre>
 * 当前包中的子包说明
 * <pre>
 *     ---- 和业务相关
 *     -  epoch.constant - 常量定义
 *     -  epoch.enums - 核心枚举定义
 *     -  epoch.runtime - 运行时相关的类
 * </pre>
 */
package io.zerows.epoch;
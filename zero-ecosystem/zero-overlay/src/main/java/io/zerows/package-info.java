/**
 * 新版 Zero 分成三部分
 * <pre>
 *     1. 全局共享部分 io.zerows
 *        - component / 和平台以及系统无业务关联的自定义组件信息
 *        - integrated / 和第三方技术框架做集成
 *        - platform -> 底层平台核心库
 *        - specification -> 规范定义
 *        - spi -> Service Provider Interface 核心定义
 *        - Support -> 提供辅助工具
 *          - {@link io.zerows.support.base.FnBase} 函数基础
 *          - {@link io.zerows.support.base.UtBase} 工具基础
 *     2. Epoch 平台共享 io.zerows.platform
 *        - annotations / 平台注解
 *        - constant / 平台常量
 *        - enums / 平台枚举
 *        - exception / 平台异常
 *        - metadata / 平台元数据对象，通常是 DTO
 *     3. Epoch 模块化实现 io.zerows.epoch
 *        - boot / 启动器
 * </pre>
 */
package io.zerows;
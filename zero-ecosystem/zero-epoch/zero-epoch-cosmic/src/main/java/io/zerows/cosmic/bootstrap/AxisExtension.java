package io.zerows.cosmic.bootstrap;

import io.zerows.cortex.AxisFactory;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 扩展路由插件，用于处理扩展路由，扩展路由主要包含两部分路由处理
 * <pre><code>
 * - 1. WebSocket 路由
 * - 2. Dynamic 动态路由
 * </code></pre>
 * 引入新的 Manager 结构来构造不同路由中的 Manager 信息
 * <pre><code>
 * - 1. 非 OSGI 环境中直接从 SPI 中提取
 * - 2. OSGI 环境中走 Service 服务提取
 * </code></pre>
 *
 * @author lang : 2024-06-26
 */
@Slf4j
public class AxisExtension implements Axis {

    // 状态容器：懒加载，第一个线程写入
    private static final ConcurrentMap<String, AtomicReference<AxisResult>> KO_STATUS = new ConcurrentHashMap<>();
    // 日志控制：懒加载
    private static final ConcurrentMap<String, AtomicBoolean> KO_LOG = new ConcurrentHashMap<>();

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        // SPID = Axis/SOCK | Websocket 功能
        final Axis sockAxis = this.mountExtension(server, bundle, EID.SOCK);
        if (Objects.nonNull(sockAxis)) {
            sockAxis.mount(server, bundle);
        }


        // SPID = Axis/MBSE | 动态路由功能
        final Axis mbseAxis = this.mountExtension(server, bundle, EID.MBSE);
        if (Objects.nonNull(mbseAxis)) {
            mbseAxis.mount(server, bundle);
        }


        // SPID = Axis/OPEN | OpenAPI 功能，以及 Swagger UI 功能
        final Axis openAxis = this.mountExtension(server, bundle, EID.OPEN);
        if (Objects.nonNull(openAxis)) {
            openAxis.mount(server, bundle);
        }
    }

    private Axis mountExtension(final RunServer server, final HBundle owner, final String spid) {
        // 1. 获取或初始化状态引用 (第一个到达的线程负责写入，实现 Lazy Load)
        final AtomicReference<AxisResult> statusRef = KO_STATUS.computeIfAbsent(spid,
            k -> new AtomicReference<>(AxisResult.WAIT_FOR));

        // 2. 双重检查锁定 (Double-Checked Locking)
        // 只有状态为 WAIT_FOR 时，才需要抢锁进行校验
        if (statusRef.get() == AxisResult.WAIT_FOR) {
            synchronized (statusRef) {
                // 第二次检查
                if (statusRef.get() == AxisResult.WAIT_FOR) {
                    try {
                        // --- 🔒 校验区开始：仅第一个线程执行 ---
                        final AxisFactory axisFactory = AxisFactory.of(spid);

                        // 2.1 校验工厂是否存在
                        if (Objects.isNull(axisFactory)) {
                            statusRef.set(AxisResult.DISABLED);
                            this.logOnce(spid, false, "⚠️ SPI 组件 {} 未找到，功能已禁用", spid);
                        }
                        // 2.2 校验配置是否启用
                        else if (!axisFactory.isEnabled(owner)) {
                            statusRef.set(AxisResult.DISABLED);
                            this.logOnce(spid, false, "⚠️ 组件 {} 功能被禁用，请检查配置，联系管理员！", spid);
                        }
                        // 2.3 校验通过
                        else {
                            statusRef.set(AxisResult.OK);
                            this.logOnce(spid, true, "✅️ 组件 {} 功能已启用，环境检测通过", spid);
                        }
                        // --- 🔒 校验区结束 ---
                    } catch (final Throwable ex) {
                        log.error("[ ZERO ] ( Axis ) 组件 {} 初始化异常：{}", spid, ex.getMessage());
                        statusRef.set(AxisResult.DISABLED);
                    }
                }
            }
        }

        // 3. 根据最终状态执行分发
        // 状态为 OK：所有线程（包括第一个线程）都会执行此处
        if (statusRef.get() == AxisResult.OK) {
            final AxisFactory axisFactory = AxisFactory.of(spid);
            // 这里不需要判空，因为上面 WAIT_FOR 阶段已经校验过 nonNull 了
            return axisFactory.getAxis();
        }

        // 状态为 DISABLED (或异常)：所有线程返回 null
        return null;
    }

    /**
     * 内部日志辅助方法
     * 确保日志只打印一次，且使用中文 + 占位符格式
     *
     * @param spid   组件ID
     * @param isInfo true=Info级别, false=Warn级别
     * @param format 格式化字符串
     * @param args   参数
     */
    private void logOnce(final String spid, final boolean isInfo, final String format, final Object... args) {
        // 同样使用 lazy load 初始化日志锁
        final AtomicBoolean shouldLog = KO_LOG.computeIfAbsent(spid, k -> new AtomicBoolean(true));

        if (shouldLog.getAndSet(false)) {
            if (isInfo) {
                log.info("[ ZERO ] ( Axis ) " + format, args);
            } else {
                log.warn("[ ZERO ] ( Axis ) " + format, args);
            }
        }
    }
}
package io.zerows.cosmic.bootstrap;

import io.zerows.cortex.AxisSwaggerFactory;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lang : 2025-01-XX
 */
@Slf4j
public class AxisSwagger implements Axis {

    private static final AtomicBoolean LOG_FACTORY_NULL = new AtomicBoolean(false);
    private static final AtomicBoolean LOG_ENABLED = new AtomicBoolean(false);
    private static final AtomicBoolean LOG_DISABLED = new AtomicBoolean(false);
    
    @Override
    public void mount(RunServer server, HBundle bundle) {
        final AxisSwaggerFactory factory = HPI.findOverwrite(AxisSwaggerFactory.class);
        if (Objects.isNull(factory)) {
            // 没有部署，无法找到工厂类（日志只打印一次）
            if (LOG_FACTORY_NULL.compareAndSet(false, true)) {
                log.info("[ ZERO ] ( Swagger ) ⚠️ SPI 组件 AxisSwaggerFactory 为 null，Swagger 功能禁用！");
            }
            return;
        }
        if (factory.isEnabled(bundle, server)) {
            if (LOG_ENABLED.compareAndSet(false, true)) {
                log.info("[ ZERO ] ( Swagger ) Swagger 功能启动成功！,登录地址：http://"+server.name()+"/docs/#/");
            }
        } else {
            if (LOG_DISABLED.compareAndSet(false, true)) {
                log.warn("[ ZERO ] ( Swagger ) 功能被配置禁用，请检查配置或联系管理员！");
            }
        }
    }
}

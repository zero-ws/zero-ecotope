package io.zerows.extension.module.mbseapi.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础日志监控，将旧版的日志器替换
 */
public class JtMonitor {

    private static final Cc<Class<?>, JtMonitor> CC_MONITOR = Cc.open();
    private transient final Logger logger;
    private transient final JtAtomic atomic = new JtAtomic();

    private JtMonitor(final Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static JtMonitor create(final Class<?> clazz) {
        return CC_MONITOR.pick(() -> new JtMonitor(clazz), clazz);
        // return Fn.po?l(Pool.MONITORS, clazz, () -> new JtMonitor(clazz));
    }

    // ---------------- Agent
    public void agentConfig(final JsonObject config) {
        this.atomic.start(this.logger, config);
    }

    // ---------------- Worker
    public void workerStart() {
        this.atomic.worker(this.logger);
    }

    public void workerFailure() {
        this.atomic.workerFailure(this.logger);
    }

    public void workerDeploying(final Integer instances, final String name) {
        this.atomic.workerDeploying(this.logger, instances, name);
    }

    public void workerDeployed(final Integer instances, final String name) {
        this.atomic.workerDeployed(this.logger, instances, name);
    }

    public void receiveData(final String identifier, final JtUri uri) {
        this.logger.info("{} Api 接口：id = {}, method = {}, path = {}", KeConstant.K_PREFIX_WEB, identifier, uri.method(), uri.path());
        this.logger.info("{} ----> API 配置：{}", KeConstant.K_PREFIX_WEB, ((JsonObject) Ut.serializeJson(uri.api())).encode());
        this.logger.info("{} ----> 服务配置：{}", KeConstant.K_PREFIX_WEB, ((JsonObject) Ut.serializeJson(uri.service())).encode());
        this.logger.info("{} ----> Worker 配置：{}", KeConstant.K_PREFIX_WEB, ((JsonObject) Ut.serializeJson(uri.worker())).encode());
    }

    // ---------------- Ingest

    // ---------------- Aim
    public void aimEngine(final HttpMethod method, final String path, final JsonObject data) {
        this.logger.info("{} Web 请求：`{} {}`，参数：{}", KeConstant.K_PREFIX_WEB, method, path, data.encode());
    }

    public void aimSend(final JsonObject data, final String address) {
        this.logger.info("{} 发送数据 `{}` 到 EventBus 地址：{}", JtConstant.K_PREFIX_JET, data.encode(), address);
    }

    // ---------------- Channel
    public void channelHit(final Class<?> clazz) {
        this.logger.info("{} 通道选择器：class = {}", JtConstant.K_PREFIX_JET, null == clazz ? null : clazz.getName());
    }

    public void componentHit(final Class<?> componentClass, final Class<?> recordClass) {
        this.logger.info("{} 通道组件：component = {}, 数据记录类 = {}", JtConstant.K_PREFIX_JET,
            null == componentClass ? null : componentClass.getName(),
            null == recordClass ? null : recordClass.getName());
    }
}

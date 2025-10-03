package io.zerows.epoch.osgi.metadata.service;

import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * 异常管理器，用来管理异常信息，底层对接 OCacheFailure 的实现，具体执行流程
 * <pre><code>
 *     1. 扫描 Bundle 中的 bundle/vertx-error.yml 文件
 *     2. 将扫描到的信息直接注册到 OCacheFailure 中
 *     3. OCacheFailure 中维护了完整的异常定义信息
 * </code></pre>
 *
 * @author lang : 2024-04-22
 */
public interface EnergyFailure {

    static EnergyFailure host() {
        return EnergyFailureService.singleton();
    }

    void install(Bundle bundle);

    void uninstall(Bundle bundle);

    default OLog logger() {
        return Ut.Log.configure(this.getClass());
    }
}

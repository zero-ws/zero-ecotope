package io.zerows.epoch.osgi.metadata.service;

import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * 发布管理流程，用于执行核心发布流，发布流上线之后才可以执行其他操作，此处使用 Object 做参数，为了兼容各种不同的发布流程。
 *
 * @author lang : 2024-07-03
 */
public interface EnergyDeployment {
    /*
     * 使用场景，底层调用 Assembly 服务，服务调用完成之后，会在 OCacheClass 中存储新的相关信息，带有 @Agent / @Worker 标记，这种
     * 模式下，发布管理直接针对新安装的插件执行此操作，典型如：
     *
     * Container Bundle：最后一个启动，它在调用 Assembly 时会直接查找容器，然后将新扫描的 @Agent / @Worker 追加到已经运行的容器中，
     * 这样所有新扫描的 Agent / Worker 组件都可以实现无缝切换直接发布到容器中。
     *
     * 解决问题：Container 先启动，Assembly 再启动
     */
    @SuppressWarnings("all")
    EnergyDeployment runDeploy(Bundle owner);

    void runUndeploy(Bundle owner);


    /*
     * 使用场景，底层调用 Container 服务，服务调用完成之后，由于指定了 RunVertx 容器，此时不做任何容器的提取，且 @Agent / @Worker 相关
     * 组件直接发布到指定容器中，这样可以完成多个容器的管理，这种属于标准化的模式
     *
     * 解决问题：Assembly 先启动，Container 再启动
     */
    @SuppressWarnings("all")
    EnergyDeployment runDeploy(Bundle owner, Object... containers);


    @SuppressWarnings("all")
    EnergyDeployment runDeployPlugins(Bundle owner, Object... containers);

    void runUndeploy(Bundle owner, Object... containers);

    /*
     * 「非启动生命周期」
     * 这种模式通常位于服务完全上线，非启动生命周期中，当系统捕捉到待发布的固定信息，以及容器信息，则直接实现发布流程，将 @Agent / @Worker
     * 固定组件追加到发不流中实现统一管理
     */
    void runComponentDeploy(Bundle owner, Class<?> deployCls, Object... containers);

    void runComponentUndeploy(Bundle owner, Class<?> deployCls, Object... containers);


    default OLog logger() {
        return Ut.Log.boot(this.getClass());
    }
}

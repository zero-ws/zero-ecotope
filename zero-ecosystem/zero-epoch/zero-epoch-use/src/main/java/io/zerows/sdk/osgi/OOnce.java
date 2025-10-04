package io.zerows.sdk.osgi;

import io.zerows.component.log.OLog;
import io.zerows.component.execution.ServiceRunner;
import io.zerows.support.Ut;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

/**
 * 服务执行对象，只执行一次，主要用于服务在启动过程和停止过程中的真实执行操作，对应上层组件的结构如下：
 * <pre><code>
 *     1. OSGI Bundle 入口组件：
 *        {@link DependencyActivatorBase} 的子类
 *     2. OSGI 依赖处理专用组件：
 *        接口: {@link ServiceConnector}
 *        实现: {@link AbstractConnectorBase} 子类
 *                |- {@link AbstractConnectorService} 子类
 *     3. 回调组件：
 *        接口：{@link OCallback.Standard}
 *        实现：{@link CallbackOfBase}
 *                - 异常管理器
 *             {@link CallbackOfService}
 *                - 异常管理器 / 配置管理器 / 服务管理器
 *                - {@see CallbackOfContainer}
 *                    - 异常管理器 / 配置管理器 / 服务管理器
 *                - {@see ...}
 *     4. 两种条件组件
 *        - {@link OOnce} 条件组件
 *          此组件一般由子类提供，内置会调用 Energy 的服务，用于判断当前 Callback 的执行是否符合执行条件，符合执行条件的情况下会检查并
 *          执行，一旦执行之后会有不同的执行结果，以确保启动组件最终的一致性。
 *        - {@link OCallback} 回调组件
 *          - {@link OCallback.Standard} 子接口，T = Object 多路执行子接口
 *          - {@link OCallback.Signal} 子接口，信号专用子接口，会配合主接口中的 signalClass 来完成内部静态类的定制
 *        {@link OOnce} 接口，当所需服务 Energy 依赖信号量时，必须走一个信号量模式来实现完整的信号回调模型
 *            | - Service / Service.OK：当 Service.OK 检查成功之后（{@link DependencyManager} 的二次注册），会构造一个 WaitFor
 *                同时等待 Service / Service.OK 上线
 *     5. 服务执行组件：
 *        1) 执行组件内部会直接被 {@link OCallback.Standard} 调用，调用过程中只执行一次，执行之后不再执行第二次，此处的执行一次有一个
 *           限制就是每个 {@link Bundle} 只执行一次，环境中有多少 {@link Bundle} 就执行多少次，执行过程中不会重复执行。
 *        2) 特殊的服务执行组件 {@link ServiceRunner} 用来搞定三个角色协同的服务乱序启动流程
 *           - Service Provider：服务提供者
 *           - Service Consumer：服务消费者
 *           - Service Context：上下文提供者
 *        3) 服务调用组件：{@link ServiceInvocation}，Provider / Consumer 的内部执行原理，这种调用是在带有 {@link ServiceContext}
 *           的上下文之间执行
 * </code></pre>
 *
 * 使用限制，此处接口的参数必须是 ServiceParameter，并且会构造对应的返回值信息，用于后续的服务调用，当前服务会被以下几个流程调用
 * <pre><code>
 *     1. {@link OOnce} 内置条件检查子流程调用
 *     2. {@link OCallback.Standard} 在 isReady() 之后的子流程调用
 * </code></pre>
 *
 * 当前执行器会和 ServiceContext 绑定，即
 * <pre><code>
 *     构造时候传入的 {@link ServiceContext} 为 provider 专用 context，且执行过程中会调用内置的
 *     Energy 服务，这些所需的服务在 {@link DependencyManager} 的管理中都是已经合法的。
 * </code></pre>
 *
 * @author lang : 2024-07-02
 */
public interface OOnce<ENERGY> {

    void bind(Object reference);

    boolean isReady();

    default OLog logger() {
        return Ut.Log.bundle(this.getClass());
    }

    ENERGY reference();

    interface LifeCycle<T> extends OOnce<T> {
        /**
         * 新版的 start 方法中必须包含返回值，返回值可以为 null，但不可以没有
         *
         * @param context 服务上下文
         * @param <R>     返回值类型
         *
         * @return 返回值
         */
        <R> R start(ServiceContext context);

        void stop(ServiceContext context);
    }
}

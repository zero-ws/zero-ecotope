package io.zerows.extension.module.mbseapi.plugins;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.handler.EndurerCommon;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.extension.module.mbseapi.boot.JtPin;
import io.zerows.extension.module.mbseapi.boot.ServiceEnvironment;
import io.zerows.extension.module.mbseapi.component.JtAim;
import io.zerows.extension.module.mbseapi.component.JtAimEngine;
import io.zerows.extension.module.mbseapi.component.JtAimIn;
import io.zerows.extension.module.mbseapi.component.JtAimPre;
import io.zerows.extension.module.mbseapi.component.JtAimSend;
import io.zerows.extension.module.mbseapi.component.JtMonitor;
import io.zerows.extension.module.mbseapi.metadata.JtConfig;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/*
 * Agent entry of dynamic deployment, this component is mount to router also.
 * 1) The dynamic router could be authorized by zero @Wall class
 * 2) The dynamic router will call connection pool of configuration, will manage all the routers in current system.
 * 3) The dynamic router will registry the routers information when booting
 */
public class JetPollux implements Axis {
    /*
     * Multi EmApp environment here
     */
    private static final ConcurrentMap<String, ServiceEnvironment> AMBIENT = JtPin.serviceEnvironment();
    private static final AtomicBoolean UNREADY = new AtomicBoolean(Boolean.TRUE);

    private final transient JtMonitor monitor;

    public JetPollux() {
        this.monitor = JtMonitor.create(this.getClass());
    }


    @Override
    @SuppressWarnings("all")
    public void mount(final RunServer server, final HBundle owner) {
        /*
         * 先提取配置，由于上层会直接调用 JetAxisManager 来对配置部分做启用 / 禁用的拦截，所以代码执行到这里已经是
         * 整体流程上 configuration 的配置部分过了自检流程，且 ServiceEnvironment 也已经过了检查流程，相关应用
         * 上下文已经全部通过校验。
         */
        final JetPolluxOptions options = JetPolluxOptions.singleton();

        final Router router = server.refRouter();
        Objects.requireNonNull(router);
        final Vertx vertx = server.refVertx();

        final JtConfig config = Ut.deserialize(options.inConfiguration(), JtConfig.class);
        final Set<JtUri> uriSet = options.inUri().stream()


            /*
             * 1. 路由加载顺序设置 order
             * 2. 路由配置绑定，此处直接绑定到 JtConfig 中，新版只做一次反序列化
             * */
            .map(uri -> uri.bind(KWeb.ORDER.DYNAMIC).<JtUri>bind(config))


            /*
             * 注册路由到 Zero Uri 存储管理器中，用于后期做动态接口发布流程
             */
            .map(this::resolveUri)


            /*
             * 路由发布 Deployment
             */
            .map(uri -> this.registryUri(uri, router))
            .collect(Collectors.toSet());


        final JetCastor castor = JetCastor.create(vertx);
        if (Objects.nonNull(castor)) {
            /*
             * 启动 Worker 的发布做后期的 Deployment，此处 Worker 的发布会开启一个新的流程，以防止每个线程都去发布 Worker，
             * 此处执行的 JetPollux 是线程级的，如果
             * - agent x 32
             * - worker 的 instances 配置为 64
             * 不做这种拦截会导致最终发布 worker 数量为 32 x 64，最终引起 block thread 的问题，所以此处设置 UNREADY 标记来
             * 保证 worker 发布的数量的准确性
             */
            if (UNREADY.getAndSet(Boolean.FALSE)) {
                this.monitor.workerStart();
                castor.startWorkers(uriSet);
            }
        }
    }

    private JtUri resolveUri(final JtUri uri) {
        final HttpMethod method = uri.method();
        final String uriPath = uri.path();
        if (Objects.isNull(uriPath)) {
            // 直接返回
            return uri;
        }

        if (uriPath.contains(":")) {
            // 触发存储操作
            OCacheUri.Tool.resolve(uriPath, method);
        }
        return uri;
    }

    private JtUri registryUri(final JtUri uri, final Router router) {
        // 构造新路由
        final Route route = router.route();
        // Uri, Method, Order
        route.path(uri.path()).order(uri.order()).method(uri.method());
        // Consumes / Produces
        uri.consumes().forEach(route::consumes);
        uri.produces().forEach(route::produces);
        /*
         * Major Route: EngineAim
         * 1) Pre-Condition
         *      IN_RULE
         *      IN_MAPPING
         *      IN_PLUG
         *      IN_SCRIPT
         * 2) Major code logical ( Could not be configured )
         * 3) Send logical
         *      3.1) Send current request to worker ( Ha )
         *      3.2) Send message to worker
         *      3.3) Let worker consume component
         */
        final JtAim pre = POOL.CC_AIM.pick(() -> Ut.instance(JtAimPre.class), JtAimPre.class.getName());
        final JtAim in = POOL.CC_AIM.pick(() -> Ut.instance(JtAimIn.class), JtAimIn.class.getName());
        final JtAim engine = POOL.CC_AIM.pick(() -> Ut.instance(JtAimEngine.class), JtAimEngine.class.getName());
        final JtAim send = POOL.CC_AIM.pick(() -> Ut.instance(JtAimSend.class), JtAimSend.class.getName());
        /* Basic parameter validation / 400 Bad Request */
        route.handler(pre.attack(uri))
            /*
             * Four rule here
             * IN_RULE , IN_MAPPING, IN_PLUG, IN_SCRIPT
             */
            .handler(in.attack(uri))
            /*
             * Handler major process and workflow
             */
            .handler(engine.attack(uri))
            /*
             * Message sender, connect to event bus
             */
            .handler(send.attack(uri))
            /*
             * Failure Handler when error occurs
             */
            .failureHandler(EndurerCommon.create());
        return uri;
    }
}

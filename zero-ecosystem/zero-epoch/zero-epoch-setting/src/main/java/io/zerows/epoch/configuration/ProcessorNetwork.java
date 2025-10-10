package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;

import java.util.Objects;

/**
 * 通用处理器，调用各个子处理器
 *
 * @author lang : 2024-04-20
 */
public class ProcessorNetwork implements Processor<NodeNetwork, HSetting> {

    private static final Cc<String, Processor<NodeNetwork, HSetting>> CC_PROCESSOR = Cc.openThread();

    private final transient Processor<SockOptions, HttpServerOptions> sockProcessor;
    private final transient Processor<NodeNetwork, ConfigContainer> instanceProcessor;

    private ProcessorNetwork() {
        this.sockProcessor = new ProcessorWebsocket();
        this.instanceProcessor = new ProcessorInstance();
    }

    public static Processor<NodeNetwork, HSetting> of() {
        return CC_PROCESSOR.pick(ProcessorNetwork::new, ProcessorNetwork.class.getName());
    }

    /**
     * 新版可以直接拿掉底层的序列化、验证、API 注入等种种操作，直接从此处打断构造 {@link NodeNetwork} 即可
     * 这样更加简单的流程表，因为新版的 {@link HSetting} 已经将部分 Options 全部规划好了
     */
    @Override
    public void makeup(final NodeNetwork network, final HSetting setting) {
        final HConfig container = setting.container();
        if (!(container instanceof final ConfigContainer configContainer)) {
            throw new _500ServerInternalException("[ ZERO ] 配置数据异常，无法找到容器配置！");
        }
        // 初始绑定 Settings
        network.setting(setting);
        // ClusterOptions
        network.cluster(configContainer.ref());


        // HttpServerOptions
        final HttpServerOptions serverOptions = this.serverOptions(setting);
        // SockOptions
        final SockOptions sockOptions = this.sockOptions(setting);


        if (Objects.nonNull(sockOptions)) {
            /*
             * 创建 SockOptions 和 HttpServerOptions 的关联，插件方式的启动是必须的，后续将 websocket 插件内置
             */
            this.sockProcessor.makeup(sockOptions, serverOptions);
        }
        network.server(serverOptions).sock(sockOptions);


        this.instanceProcessor.makeup(network, configContainer);
        network.vLog();
    }

    private HttpServerOptions serverOptions(final HSetting setting) {
        final HConfig serverConfig = setting.infix(EmApp.Native.SERVER);
        Objects.requireNonNull(serverConfig, "[ ZERO ] 无法找到服务器配置！");
        final JsonObject options = serverConfig.options();
        return new HttpServerOptions(options);
    }

    private SockOptions sockOptions(final HSetting setting) {
        final HConfig sockConfig = setting.infix(EmApp.Native.WEBSOCKET);
        if (Objects.isNull(sockConfig)) {
            return null;
        }
        final JsonObject options = sockConfig.options();
        final Class<?> executor = sockConfig.executor();
        return new SockOptions(options, executor);
    }
}

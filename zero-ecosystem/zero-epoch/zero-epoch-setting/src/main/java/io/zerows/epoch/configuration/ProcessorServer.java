package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.platform.exception._30001Exception500ServerConfig;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-20
 */
class ProcessorServer implements Processor<NodeVertx, HSetting> {

    private static final ConcurrentMap<ServerType, Supplier<Processor<NodeVertx, JsonArray>>> PROCESSOR_MAP = new ConcurrentHashMap<>() {
        {
            // 这三种处理服务器配置流程一模一样
            this.put(ServerType.HTTP, ProcessorOfHttp::new);
            this.put(ServerType.RX, ProcessorOfRx::new);
            this.put(ServerType.API, ProcessorOfApi::new);
            // IPC 服务器除外 / WebSocket 服务器除外
            this.put(ServerType.SOCK, ProcessorOfSock::new);
            this.put(ServerType.IPC, ProcessorOfIpc::new);
        }
    };

    @Override
    public void makeup(final NodeVertx target, final HSetting setting) {
        // 服务器基础配置提取
        final JsonArray serverData = this.verifyConfig(setting);


        // Deployment 先执行（全局先赋值，后期可在 Vertx 内部再定义赋值）


        // 多服务器模式，叠加处理，按 type 分组，不同分组处理的选项会不一样
        final ConcurrentMap<String, JsonArray> grouped = Ut.elementGroup(serverData, KName.TYPE);
        grouped.forEach((type, serverA) -> {


            // 按类型分组处理，防止多余创建
            final ServerType serverType = ServerType.of(type);
            final Supplier<Processor<NodeVertx, JsonArray>> supplier = PROCESSOR_MAP
                .getOrDefault(serverType, null);
            if (Objects.nonNull(supplier)) {
                supplier.get().makeup(target, serverA);
            } else {
                this.logger().warn("The server type {0} processor could not be found.", type);
            }
        });
    }

    private JsonArray verifyConfig(final HSetting setting) {
        final HConfig config = setting.infix(YmlCore.server.__KEY);

        if (Objects.isNull(config) || Ut.isNil(config.options())) {
            throw new _30001Exception500ServerConfig("[ R2MO ] 服务器配置 null");
        } else {
            final JsonObject serverData = config.options();
            if (!serverData.containsKey(YmlCore.server.__KEY)) {
                throw new _30001Exception500ServerConfig(serverData.encode());
            }
            return Ut.valueJArray(serverData, YmlCore.server.__KEY);
        }
    }
}

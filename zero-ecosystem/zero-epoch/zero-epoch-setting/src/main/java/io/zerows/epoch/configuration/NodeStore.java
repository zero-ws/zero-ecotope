package io.zerows.epoch.configuration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.zerows.platform.metadata.MultiKeyMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 由于上下层的严格区分，此处提供第三存储
 * <pre>
 *     1. 上层 StoreVertx 在执行存储时会调用 NodeStore 移除掉 NodeVertx 相关配置
 *     2. 下层 NodeStore 的写操作只能被 StoreVertx 调用
 *     3. 读取
 *        - 上层负责 RunVertx -> 下层负责 {@link NodeVertx}
 *        - 上层负责 RunServer -> 下层负责 {@link NodeNetwork}
 * </pre>
 * 所有不依赖的配置数据从此处静态获取，保证统一性
 *
 * @author lang : 2025-10-11
 */
@Slf4j
public class NodeStore {

    private static final MultiKeyMap<NodeVertx> RUNNING = new MultiKeyMap<>();

    private static NodeNetwork defaultNetwork;
    private static NodeVertx defaultVertx;

    /**
     * 此处设计比较巧妙，hashCode 是运行中的 {@link Vertx} 实例的 hashCode，保证唯一性，为了方便提取和它相关的所有配置信息
     *
     * @param hashCode  运行中的 {@link Vertx} 实例的 hashCode
     * @param nodeVertx 节点配置
     *
     */
    public static void add(final String hashCode, final NodeVertx nodeVertx) {
        if (Objects.isNull(hashCode) || Objects.isNull(nodeVertx)) {
            log.warn("[ ZERO ] 配置实例添加失败，有传入数据为 null.");
            return;
        }
        RUNNING.put(hashCode, nodeVertx, nodeVertx.name());
        // TODO: 第一个网络是默认网络（暂时不考虑多网络环境）
        if (Objects.isNull(defaultNetwork)) {
            defaultNetwork = nodeVertx.networkRef();
        }
        if (Objects.isNull(defaultVertx)) {
            defaultVertx = nodeVertx;
        }
        log.info("[ ZERO ] \uD83C\uDF89 默认节点：{} / 默认实例：{}", nodeVertx.name(), defaultVertx.name());
    }

    public static void remove(final String name) {
        RUNNING.remove(name);
    }

    // ============ ⚙️ 静态配置方法 ============
    public static DeliveryOptions ofDelivery(final Vertx vertx) {
        Objects.requireNonNull(vertx, "[ ZERO ] Vertx 不能为空！");
        final NodeVertx nodeVertx = RUNNING.get(String.valueOf(vertx.hashCode()));
        if (Objects.isNull(nodeVertx)) {
            return new DeliveryOptions();
        }
        return nodeVertx.deliveryOptions();
    }

    public static DeploymentOptions ofDeployment(final Class<?> clazz) {
        Objects.requireNonNull(defaultVertx, "[ ZERO ] 默认 Vertx 不能为空！");
        return defaultVertx.deploymentOptions(clazz);
    }

    public static NodeNetwork ofNetwork() {
        return defaultNetwork;
    }
}

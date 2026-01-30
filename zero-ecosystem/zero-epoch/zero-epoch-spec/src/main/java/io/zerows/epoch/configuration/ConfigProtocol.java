package io.zerows.epoch.configuration;

import lombok.Getter;

/**
 * 🔌 配置中心协议定义 (Configuration Protocols)
 *
 * <p>
 * 定义系统支持的所有远程配置源协议类型。
 * 每个枚举值绑定了一个特定的前缀字符串，用于在 DSL 解析时识别协议。
 * </p>
 */
@Getter
public enum ConfigProtocol {

    /**
     * 🦢 Nacos (Default)
     */
    NACOS("nacos:"),

    /**
     * 🦓 Apache Zookeeper
     */
    ZOOKEEPER("zookeeper:"),

    /**
     * 🏛️ HashiCorp Consul
     */
    CONSUL("consul:"),

    /**
     * ☁️ Kubernetes ConfigMap
     */
    KUBERNETES("k8s:"),

    /**
     * 📦 Etcd
     */
    ETCD("etcd:");

    private final String prefix;

    ConfigProtocol(final String prefix) {
        this.prefix = prefix;
    }
}
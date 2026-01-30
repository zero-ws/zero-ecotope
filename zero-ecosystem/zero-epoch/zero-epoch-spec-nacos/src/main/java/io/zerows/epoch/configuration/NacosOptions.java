package io.zerows.epoch.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.zerows.epoch.spec.YmSpec;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * {@link YmSpec.vertx.cloud.nacos}
 *
 * @author lang : 2025-10-05
 */
@Data
public class NacosOptions implements Serializable {
    private static final String KEY_SERVER_ADDR = "server-addr";
    private static final String KEY_FILE_EXTENSION = "file-extension";
    private static final String KEY_REFRESH_ENABLED = "refresh-enabled";
    private static final String KEY_REGISTER_ENABLED = "register-enabled";

    @JsonProperty(KEY_SERVER_ADDR)
    private String serverAddr;

    private String username;
    private String password;
    private String name;

    private Config config;

    private Discovery discovery;

    public void applyOption() {
        // 1. [Root] 基础核心校验 (Source of Truth)
        if (Ut.isNil(this.serverAddr)) {
            throw new _501NotSupportException("Nacos 核心配置丢失 / vertx.cloud.nacos.server-addr");
        }

        if (Ut.isNil(this.username) || Ut.isNil(this.password)) {
            throw new _401UnauthorizedException("Nacos 鉴权配置丢失 / vertx.cloud.nacos.username|password");
        }

        if (Ut.isNil(this.name)) {
            throw new _500ServerInternalException("Nacos 服务名称配置丢失 / vertx.cloud.nacos.name");
        }

        // 2. [Config] 自动初始化与 Spring 对齐默认值
        if (Objects.isNull(this.config)) {
            this.config = new Config();
        }
        this.fillConfigDefaults();

        // 3. [Discovery] 自动初始化与 Spring 对齐默认值
        if (Objects.isNull(this.discovery)) {
            this.discovery = new Discovery();
        }
        this.fillDiscoveryDefaults();
    }

    private void fillConfigDefaults() {
        // server-addr: 回退引用 Root
        if (Ut.isNil(this.config.getServerAddr())) {
            this.config.setServerAddr(this.serverAddr);
        }
        // prefix: 默认为服务名
        if (Ut.isNil(this.config.getPrefix())) {
            this.config.setPrefix(this.name);
        }
        // file-extension: 默认为 yaml
        if (Ut.isNil(this.config.getFileExtension())) {
            this.config.setFileExtension(VValue.SUFFIX.YAML);
        }

        // 🟢 [Spring对齐] group: DEFAULT_GROUP
        if (Ut.isNil(this.config.getGroup())) {
            this.config.setGroup("DEFAULT_GROUP");
        }
        // 🟢 [Spring对齐] encode: UTF-8
        if (Ut.isNil(this.config.getEncode())) {
            this.config.setEncode("UTF-8");
        }
        // 🟢 [Spring对齐] timeout: 3000ms
        if (this.config.getTimeout() == null) {
            this.config.setTimeout(3000);
        }
        // 🟢 [Spring对齐] refresh-enabled: true
        if (this.config.getRefreshEnabled() == null) {
            this.config.setRefreshEnabled(true);
        }
    }

    private void fillDiscoveryDefaults() {
        // server-addr: 回退引用 Root
        if (Ut.isNil(this.discovery.getServerAddr())) {
            this.discovery.setServerAddr(this.serverAddr);
        }
        // namespace: 强一致性，如果 discovery 没配，必须跟随 config，防止环境脑裂
        if (Ut.isNil(this.discovery.getNamespace()) && Ut.isNotNil(this.config.getNamespace())) {
            this.discovery.setNamespace(this.config.getNamespace());
        }

        // 🟢 [Spring对齐] group: DEFAULT_GROUP
        if (Ut.isNil(this.discovery.getGroup())) {
            this.discovery.setGroup("DEFAULT_GROUP");
        }
        // 🟢 [Spring对齐] enabled: true
        if (this.discovery.getEnabled() == null) {
            this.discovery.setEnabled(true);
        }
        // 🟢 [Spring对齐] register-enabled: true
        if (this.discovery.getRegisterEnabled() == null) {
            this.discovery.setRegisterEnabled(true);
        }
    }

    @Data
    public static class Discovery implements Serializable {
        @JsonProperty(KEY_SERVER_ADDR)
        private String serverAddr;
        private String namespace;

        // 🟢 [Spring对齐] 分组
        private String group;

        // 🟢 [Spring对齐] 是否启用服务发现
        private Boolean enabled;

        // 🟢 [Spring对齐] 是否注册自己
        @JsonProperty(KEY_REGISTER_ENABLED)
        private Boolean registerEnabled;
    }

    @Data
    public static class Config implements Serializable {
        @JsonProperty(KEY_SERVER_ADDR)
        private String serverAddr;
        private String namespace;
        private String prefix;
        @JsonProperty(KEY_FILE_EXTENSION)
        private String fileExtension;

        // 🟢 [Spring对齐] 分组
        private String group;

        // 🟢 [Spring对齐] 编码
        private String encode;

        // 🟢 [Spring对齐] 超时时间
        private Integer timeout;

        // 🟢 [Spring对齐] 是否动态刷新
        @JsonProperty(KEY_REFRESH_ENABLED)
        private Boolean refreshEnabled;
    }
}
package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 📦 导入元数据 (Import Metadata)
 *
 * <p>
 * 承载解析后的配置规则，包括 DataID、协议类型、可选性以及
 * 通过 URL 参数传递的扩展配置（如 refreshEnabled, group 等）。
 * </p>
 */
@Data
class NacosMeta implements Serializable {

    /**
     * 配置文件的 Data ID (e.g., "app-config.yaml")
     */
    private String dataId;

    /**
     * 是否可选 (optional)。
     * true: 加载失败时忽略；false: 必须加载成功。
     */
    private boolean isOptional = true;

    /**
     * 配置协议源 (NACOS, ZOOKEEPER...)
     */
    private ConfigProtocol protocol = ConfigProtocol.NACOS;

    /**
     * 🔌 扩展参数容器
     * 存储从 DSL 中解析出的 Query Params。
     * * 🌰 示例:
     * DSL: "data-id?refreshEnabled=true&group=DEV"
     * Params: { "refreshEnabled": true, "group": "DEV" }
     */
    private JsonObject params = new JsonObject();
}
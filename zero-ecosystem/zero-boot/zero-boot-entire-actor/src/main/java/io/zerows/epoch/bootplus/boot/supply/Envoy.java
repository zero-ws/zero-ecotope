package io.zerows.epoch.bootplus.boot.supply;

import io.zerows.epoch.bootplus.stellar.owner.OkA;
import io.zerows.epoch.bootplus.stellar.vendor.AbstractPartyB;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.boot.Electy;
import io.zerows.platform.metadata.KIntegration;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KMap;

/**
 * 和内置的 {@link Electy} 形成对比
 * <pre><code>
 *     1. {@link Electy} 主要负责核心框架运行时处理
 *     2. {@link Envoy} 则负责扩展框架中启动时的数据提供，针对不同的入口启动器
 * </code></pre>
 *
 * @author lang : 2023-06-11
 */
public class Envoy {

    /**
     * 在 {@link AbstractPartyB} 中使用的条件部分
     *
     * @param partyA     {@link OkA} 作为入口参数
     * @param identifier 作为入口参数
     *
     * @return {@link MultiMap}
     */
    public static MultiMap input(final OkA partyA, final String identifier) {
        return EInput.input(partyA, identifier);
    }

    /**
     * 读取 {@link OkA} 中的数据
     *
     * @param partyA {@link OkA} 作为入口参数
     *
     * @return {@link JsonObject}
     */
    public static JsonObject inputQr(final OkA partyA) {
        return EInput.inputQr(partyA);
    }

    // --------------- Party B 部分

    public static KDictConfig ofBDict(final KIntegration integration) {
        return EPartyB.partyDict(integration);
    }

    public static KMap ofBMap(final KIntegration integration) {
        return EPartyB.partyMap(integration);
    }

    public static JsonObject ofBOption(final KIntegration integration) {
        return EPartyB.partyOption(integration);
    }
}

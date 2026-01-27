package io.zerows.boot.test;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KIntegration;
import io.zerows.platform.metadata.KMap;

/**
 * 和内置的 {@see Electy} 形成对比
 * <pre><code>
 *     1. {@see Electy} 主要负责核心框架运行时处理
 *     2. {@link EAnvoy} 则负责扩展框架中启动时的数据提供，针对不同的入口启动器
 * </code></pre>
 *
 * @author lang : 2023-06-11
 */
public class EAnvoy {

    /**
     * 在 {@link PartyBBase} 中使用的条件部分
     *
     * @param partyA     {@link PartyA} 作为入口参数
     * @param identifier 作为入口参数
     * @return {@link MultiMap}
     */
    public static MultiMap input(final PartyA partyA, final String identifier) {
        return EAInput.input(partyA, identifier);
    }

    /**
     * 读取 {@link PartyA} 中的数据
     *
     * @param partyA {@link PartyA} 作为入口参数
     * @return {@link JsonObject}
     */
    public static JsonObject inputQr(final PartyA partyA) {
        return EAInput.inputQr(partyA);
    }

    // --------------- Party B 部分

    public static KDictConfig ofBDict(final KIntegration integration) {
        return EAPartyB.partyDict(integration);
    }

    public static KMap ofBMap(final KIntegration integration) {
        return EAPartyB.partyMap(integration);
    }

    public static JsonObject ofBOption(final KIntegration integration) {
        return EAPartyB.partyOption(integration);
    }
}

package io.zerows.epoch.bootplus.stellar.vendor;

import io.zerows.epoch.bootplus.boot.supply.Envoy;
import io.zerows.epoch.bootplus.stellar.owner.OkA;
import io.vertx.core.json.JsonObject;
import io.zerows.metadata.app.KIntegration;
import io.zerows.metadata.datamation.KDictConfig;
import io.zerows.metadata.datamation.KMap;

/**
 * ## 默认组装器实现
 * ### 1. 基本介绍
 * 系统默认组装器，根据运行目录构造UCMDB的专用配置。
 * ### 2. 目录规范
 * 一次配置组装器的配置规范如下：
 * |文件名|含义|
 * |:---|:---|
 * |`[folder]/cmdb-v2/dict-config/[filename].json`|字典定义配置。|
 * |`[folder]/cmdb-v2/dict-epsilon/[filename].json`|字典消费配置。|
 * |`[folder]/cmdb-v2/mapping/[filename].json`|字段映射器配置。|
 * |`[folder]/cmdb-v2/options/[filename].json`|通道专用服务配置，对应`I_SERVICE`中的serviceConfig，构造options。|
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class PartyB extends AbstractPartyB {
    /**
     * {@link KDictConfig}对象，字典翻译器配置。
     */
    private final transient KDictConfig dict;
    /**
     * {@link KMap}对象，字段映射器配置。
     */
    private final transient KMap mapping;
    /**
     * {@link JsonObject}对象，服务配置，对应`ServiceConfig`字段，构造`options`。
     */
    private final transient JsonObject options;
    private final transient KIntegration integration;

    /*
     * 构造函数，CMDB专用配置组装器，配置根目录为`src/main/resources`目录，待使用工厂模式。
     */
    PartyB(final OkA partyA, final KIntegration integration) {
        super(partyA);
        this.integration = integration;
        this.dict = Envoy.ofBDict(integration);
        this.mapping = Envoy.ofBMap(integration);
        this.options = Envoy.ofBOption(integration);
    }

    @Override
    public KIntegration configIntegration() {
        return this.integration;
    }

    /**
     * 对应通道定义`I_SERVICE`中的`serviceConfig`属性，服务配置，构造`options`专用
     *
     * @return {@link JsonObject}
     */
    @Override
    public JsonObject configService() {
        final JsonObject params = this.inputQr();
        this.options.mergeIn(params, true);
        return this.options;
    }

    @Override
    public KMap map() {
        return this.mapping;
    }

    @Override
    public KDictConfig configDict() {
        return this.dict;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends OkB> CHILD copy() {
        final PartyB okB = new PartyB(this.partyA, this.integration.copy());
        okB.options.mergeIn(this.options.copy());
        return (CHILD) okB;
    }
}

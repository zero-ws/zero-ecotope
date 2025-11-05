package io.zerows.extension.module.mbseapi.metadata;

import io.r2mo.base.dbe.Database;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.mbseapi.boot.Jt;
import io.zerows.extension.module.mbseapi.common.JtKey;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.mbse.sdk.Commercial;
import io.zerows.platform.enums.EmWeb;
import io.zerows.platform.enums.Environment;
import io.zerows.platform.metadata.KDictConfig;
import io.zerows.platform.metadata.KIdentity;
import io.zerows.platform.metadata.KIntegration;
import io.zerows.platform.metadata.KMap;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.modeling.HRule;
import io.zerows.support.Ut;

import java.util.Objects;

/*
 * Another data structure for bridge
 * 1) Api + Service
 * 2) Job + Service
 *
 * It means that serviceComponent could be consumed by different entrance such as:
 * 1) Request-Response ( IApi )
 * 2) Publish-Subscribe ( IJob )
 */
@SuppressWarnings("unchecked")
public abstract class JtCommercial implements Commercial {
    /*
     * Environment selection, the default should be `Production`,
     * It means that the code logical is correct.
     */
    private transient Environment environment = Environment.Production;
    private transient IService service;
    /*
     * Shared data structure for
     * 1) JtApp ( application data )
     */
    private transient HArk ark;
    private transient JtConfig config;

    JtCommercial() {
    }

    JtCommercial(final IService service) {
        this.service = service;
    }

    public <T extends JtCommercial> T bind(final JtConfig config) {
        this.config = config;
        return (T) this;
    }

    public <T extends JtCommercial> T bind(final HArk ark) {
        this.ark = ark;
        return (T) this;
    }

    /*
     * Public interface to return `IService` reference
     */
    public IService service() {
        return this.service;
    }

    /*
     * Sub class used method for some processing
     */
    protected HArk ark() {
        return this.ark;
    }

    protected JtConfig getConfig() {
        return this.config;
    }

    /*
     * Each sub class must set implementation of this method here.
     */
    public abstract String key();

    @Override
    public EmWeb.Channel channelType() {
        return Ut.toEnum(this.service::getChannelType, EmWeb.Channel.class, EmWeb.Channel.ADAPTOR);
    }

    @Override
    public Class<?> channelComponent() {
        return Jt.toChannel(this.service::getChannelComponent, this.channelType());
    }

    @Override
    public Class<?> businessComponent() {
        return Ut.clazz(this.service.getServiceComponent());
    }

    @Override
    public Class<?> recordComponent() {
        return Ut.clazz(this.service.getServiceRecord());
    }

    @Override
    public Database database() {
        return Jt.toDatabase(this.service);
    }

    @Override
    public HRule rule() {
        return Jt.toRule(this.service);
    }

    @Override
    public KIntegration integration() {
        final KIntegration integration = Jt.toIntegration(this.service);
        if (Environment.Mockito == this.environment) {
            /*
             * When pre-release here, the integration should be connected to actual
             * environment, in this kind of situation, you can call atom JtJob `mockOn`
             * to turn join `debug` options in integration, but it require the environment
             * to be `Mockito` instead of others.
             * Involve environment concept to split deployment/testing/production
             */
            integration.mockOn();
        }
        return integration;
    }

    public JtCommercial bind(final Environment environment) {
        this.environment = environment;
        return this;
    }

    @Override
    public KDictConfig dict() {
        return Jt.toDict(this.service);
    }

    @Override
    public KMap mapping() {
        return Jt.toMapping(this.service);
    }

    @Override
    public KIdentity identity() {
        return Jt.toIdentity(this.service);
    }


    @Override
    public String app() {
        final HApp app = this.ark.app();
        return app.option(KName.APP_ID);
    }


    /*
     * Non - Interface method here.
     */
    @Override
    public String identifier() {
        return this.service.getIdentifier();
    }

    // ---------- Basic InJson
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final JtUri jtUri)) {
            return false;
        }
        return this.key().equals(jtUri.key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key());
    }

    @Override
    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        /* key data */
        data.put(JtKey.Delivery.KEY, this.key());

        /* service, config */
        final JsonObject serviceJson = Ut.serializeJson(this.service());
        data.put(JtKey.Delivery.SERVICE, serviceJson);
        data.put(JtKey.Delivery.CONFIG, (JsonObject) Ut.serializeJson(this.config));

        /* id */
        final HApp app = this.ark.app();
        data.put(JtKey.Delivery.APP_ID, app.option(KName.APP_ID));
        /* Reflection */
        data.put(KName.__.CLASS, this.getClass().getName());
        return data;
    }

    @Override
    public void fromJson(final JsonObject data) {
        /*
         * service, config
         */
        this.service = Ut.deserialize(data.getJsonObject(JtKey.Delivery.SERVICE), IService.class);
        this.config = Ut.deserialize(data.getJsonObject(JtKey.Delivery.CONFIG), JtConfig.class);
        /*
         * application id
         */
        final String appId = data.getString(JtKey.Delivery.APP_ID);
        this.ark = Ke.ark(appId);
    }
}

package io.zerows.extension.mbse.action.atom;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KWeb;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.zdk.Api;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.extension.mbse.action.eon.JtKey;
import io.zerows.extension.mbse.action.eon.em.ParamMode;
import io.zerows.extension.mbse.action.util.Jt;
import jakarta.ws.rs.core.MediaType;

import java.util.Objects;
import java.util.Set;

/*
 * Uri ( API + SERVICE )
 */
public class JtUri extends JtCommercial implements Api {

    /*
     * Worker
     */
    private transient JtWorker worker;
    private transient IApi api;
    private transient String key;
    /*
     * App / Config
     */
    private transient Integer order;

    /*
     * For deserialization
     */
    public JtUri() {
    }

    public JtUri(final IApi api, final IService service) {
        super(service);
        this.api = api;
        /*
         * Api Key
         *  */
        this.key = api.getKey();

        /*
         * Default Component Value
         * */
        Jt.initApi(api);
        /*
         * JtWorker instance here for future use
         */
        this.worker = Jt.toWorker(api);
    }

    public JtUri bind(final Integer order) {
        this.order = order;
        return this;
    }

    // ----------- Uri Hub
    /*
     * Method in JtUri
     * Uri, Order, Path
     */
    public Integer order() {
        return Objects.nonNull(this.order) ? this.order : KWeb.ORDER.DYNAMIC;
    }

    /*
     * Mime
     * Consumes, Produces
     */
    public Set<String> produces() {
        return Jt.toMimeString(this.api::getProduces);
    }

    public Set<MediaType> producesMime() {
        return Jt.toMime(this.api::getProduces);
    }

    public Set<String> consumes() {
        return Jt.toMimeString(this.api::getConsumes);
    }

    // ------------ param
    /*
     * Param mode, default BODY
     */
    public ParamMode paramMode() {
        return Ut.toEnum(this.api::getParamMode, ParamMode.class, ParamMode.BODY);
    }

    public Set<String> paramRequired() {
        return Jt.toSet(this.api::getParamRequired);
    }

    public Set<String> paramContained() {
        return Jt.toSet(this.api::getParamContained);
    }

    // ------------- api & service
    @Override
    public String key() {
        return this.key;
    }

    // ------------- worker
    public JtWorker worker() {
        return this.worker;
    }

    // ------------- Api / Service
    public IApi api() {
        return this.api;
    }

    // ------------- Overwrite

    @Override
    public String path() {
        return Jt.toPath(this.ark(), this.api::getUri, this.api.getSecure(), this.getConfig());
    }

    @Override
    public HttpMethod method() {
        // return Ut.toEnum(this.api::getMethod, HttpMethod.class, HttpMethod.GET);
        return Ut.toMethod(this.api::getMethod);
    }

    @Override
    public JsonObject options() {
        return Jt.toOptions(this.service(), this.api);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject data = super.toJson();
        /* Append Api data only */
        data.put(JtKey.Delivery.ORDER, this.order);
        data.put(JtKey.Delivery.API, Ut.serializeJson(this.api));
        return data;
    }

    @Override
    public void fromJson(final JsonObject data) {
        super.fromJson(data);
        /*
         * Basic attributes
         */
        this.key = data.getString(JtKey.Delivery.KEY);
        this.order = data.getInteger(JtKey.Delivery.ORDER);
        /*
         * api
         */
        this.api = Ut.deserialize(data.getJsonObject(JtKey.Delivery.API), IApi.class);
        /*
         * worker
         */
        this.worker = Jt.toWorker(this.api);
    }
}

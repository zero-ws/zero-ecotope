package io.zerows.epoch.corpus.mbse.atom.runner;

import io.r2mo.function.Fn;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KWeb;
import io.zerows.epoch.common.shared.datamation.KMap;
import io.zerows.epoch.corpus.container.exception._40064Exception500ActSpecification;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.specification.modeling.HRecord;

import java.io.File;
import java.io.Serializable;

/*
 * Business Request
 * 「ActIn」 means Action Request and offer income component parameters here.
 * 1) This DTO contains: JsonObject ( Pure Data ), ZRecord ( Record Structure )
 * 2) Bind record data structure for serialization/deserialization.
 *
 * 「Workflow」
 *  In ——
 *      Consumer -> ZApi/Envelop ->
 *                      Channel -> ActIn ->
 *                                          Component
 *  Out ——
 *      Component -> ActOut ->
 *                      Envelop ->
 *                             Consumer ->
 *                                    SendAim ( Callback )
 */
public class ActIn extends ActMapping implements Serializable {

    /* Raw data of `Envelop` object/reference */
    private final transient Envelop envelop;
    private final boolean isBatch;
    private final transient ActFile file;

    private transient ActJObject json;
    private transient ActJArray jarray;
    private transient HRecord definition;

    private transient KMap mapping;

    public ActIn(final Envelop envelop) {
        /* Envelop reference here */
        this.envelop = envelop;
        /*
         * Whether
         * 1) JsonObject
         * 2) JsonArray
         * */
        final JsonObject data = envelop.data();
        if (data.containsKey(KWeb.ARGS.PARAM_BODY)) {
            final Object value = data.getValue(KWeb.ARGS.PARAM_BODY);
            if (value instanceof JsonArray) {
                this.jarray = new ActJArray(envelop);
                this.isBatch = true;        // Batch
            } else {
                this.json = new ActJObject(envelop);
                this.isBatch = false;       // Single
            }
        } else {
            this.json = new ActJObject(envelop);
            this.isBatch = false;           // Single
        }
        /*
         * Optional to stored file here
         */
        final JsonArray stream = data.getJsonArray(KWeb.ARGS.PARAM_STREAM);
        this.file = new ActFile(stream);
    }

    public ActIn bind(final KMap mapping) {
        this.mapping = mapping;
        return this;
    }

    public Envelop getEnvelop() {
        return this.envelop;
    }

    public JsonObject getJObject() {
        if (this.isBatch) {
            return new JsonObject();
        } else {
            return this.json.getJson(this.mapping);
        }
    }

    public JsonArray getJArray() {
        if (this.isBatch) {
            return this.jarray.getJson(this.mapping);
        } else {
            return new JsonArray();
        }
    }

    public JsonObject getQuery() {
        Fn.jvmKo(this.isBatch, _40064Exception500ActSpecification.class, this.isBatch);
        return this.json.getQuery();
    }

    public HRecord getRecord() {
        Fn.jvmKo(this.isBatch, _40064Exception500ActSpecification.class, this.isBatch);
        return this.json.getRecord(this.definition, this.mapping);
    }

    public File[] getFiles() {
        return this.file.getFiles();
    }

    public HRecord[] getRecords() {
        Fn.jvmKo(this.isBatch, _40064Exception500ActSpecification.class, this.isBatch);
        return this.jarray.getRecords(this.definition, this.mapping);
    }

    public HRecord getDefinition() {
        return this.definition;
    }

    /*
     * Header value extracted
     */
    public String appId() {
        final MultiMap paramMap = this.envelop.headers();
        return paramMap.get(KWeb.HEADER.X_APP_ID);
    }

    public String sigma() {
        final MultiMap paramMap = this.envelop.headers();
        return paramMap.get(KWeb.HEADER.X_SIGMA);
    }

    public String userId() {
        return this.envelop.userId();
    }

    /*
     * 1) Set input data to Record object ( reference here )
     */
    public void connect(final HRecord definition) {
        this.definition = definition;
    }
}

package io.zerows.component.qr;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.base.UtBase;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
interface Analyzer {

    /**
     * Create new QrDo stored.
     *
     * @param data {@link io.vertx.core.json.JsonObject} Input json object.
     *
     * @return {@link Analyzer}
     */
    static Analyzer create(final JsonObject data) {
        return new AnalyzerImpl(data);
    }

    /**
     * Check whether json object is complex
     * 1. When any one findRunning is `JsonObject`, it's true.
     * 2. otherwise the result is false.
     *
     * @param source {@link io.vertx.core.json.JsonObject} input json
     *
     * @return {@link java.lang.Boolean}
     */
    static boolean isComplex(final JsonObject source) {
        return AnalyzerImpl.isComplex(source);
    }

    @SuppressWarnings("all")
    static JsonArray combine(final Object newItem, final Object oldItem, final Boolean isAnd) {
        // The operator will not be changed.
        final JsonArray newSet = (JsonArray) newItem;
        final JsonArray oldSet = (JsonArray) oldItem;
        final List result = isAnd ?
            // Two collection and
            UtBase.elementIntersect(newSet.getList(), oldSet.getList()) :
            // Two collection union
            UtBase.elementUnion(newSet.getList(), oldSet.getList());
        return new JsonArray(result);
    }

    /**
     * Save new condition to LINEAR component.
     * The parameters are following formatFail:
     * 1. fieldExpr is formatFail of `field,op`, it contains two parts.
     * 2. fieldExpr is formatFail of `field`, the default op is =.
     *
     * @param fieldExpr {@link java.lang.String}
     * @param value     {@link java.lang.Object}
     *
     * @return {@link Analyzer}
     */
    Analyzer save(String fieldExpr, Object value);

    /**
     * @param fieldExpr {@link java.lang.String} Removed fieldExpr
     * @param fully     {@link java.lang.Boolean} Removed fully or ?
     *
     * @return {@link Analyzer}
     */
    Analyzer remove(String fieldExpr, boolean fully);

    /**
     * @param fieldExpr {@link java.lang.String}
     * @param value     {@link java.lang.Object}
     *
     * @return {@link Analyzer}
     */
    Analyzer update(String fieldExpr, Object value);

    /**
     * @param field    {@link java.lang.String} The field name
     * @param consumer {@link java.util.function.BiConsumer} The qr item consumed
     */
    void match(String field, BiConsumer<IrItem, JsonObject> consumer);

    /**
     * Serialized current instance to InJson
     *
     * @return {@link JsonObject}
     */
    JsonObject toJson();
}

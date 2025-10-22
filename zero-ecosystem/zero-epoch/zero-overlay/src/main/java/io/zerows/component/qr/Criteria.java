package io.zerows.component.qr;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._60024Exception500QueryMetaNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * ## 「Pojo」Criteria Object
 *
 * ### 1. Intro
 *
 * Criteria for condition set, the connector is and Advanced criteria will use tree mode, the flat mode is "AND"
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class Criteria implements Serializable {
    /**
     * The mode of current criteria condition.
     * Here are two modes:
     * 1. LINEAR - The linear condition.
     * 2. TREE - The tree condition ( Complex query tree data structure ).
     */
    private final Ir.Mode mode;
    /**
     * The LINEAR/TREE analyzer.
     */
    private final transient Analyzer analyzer;

    /**
     * Create criteria based on json object.
     *
     * @param data {@link io.vertx.core.json.JsonObject}
     */
    private Criteria(final JsonObject data) {
        Fn.jvmKo(Objects.isNull(data), _60024Exception500QueryMetaNull.class);
        assert data != null : "If null dot, the exception will be thrown out.";
        if (Analyzer.isComplex(data)) {
            this.mode = Ir.Mode.TREE;
        } else {
            this.mode = Ir.Mode.LINEAR;
        }
        this.analyzer = Analyzer.create(data);
    }

    /**
     * Create new instance of criteria.
     *
     * @param data {@link io.vertx.core.json.JsonObject}
     *
     * @return {@link Criteria}
     */
    public static Criteria create(final JsonObject data) {
        return new Criteria(data);
    }

    /**
     * Get the json query condition mode.
     *
     * @return {@link Ir.Mode}
     */
    public Ir.Mode mode() {
        return this.mode;
    }

    /**
     * @param fieldExpr {@link java.lang.String}
     * @param value     {@link java.lang.Object}
     *
     * @return {@link Criteria}
     */
    public Criteria save(final String fieldExpr, final Object value) {
        this.analyzer.save(fieldExpr, value);
        return this;
    }

    /**
     * @param fieldExpr {@link java.lang.String}
     * @param value     {@link java.lang.Object}
     *
     * @return {@link Criteria}
     */
    public Criteria update(final String fieldExpr, final Object value) {
        this.analyzer.update(fieldExpr, value);
        return this;
    }

    /**
     * @param fieldExpr {@link java.lang.String} Removed fieldExpr
     *
     * @return {@link Criteria}
     */
    public Criteria remove(final String fieldExpr) {
        this.analyzer.remove(fieldExpr, false);
        return this;
    }

    /**
     * @param field    {@link java.lang.String} The field name
     * @param consumer {@link java.util.function.BiConsumer} The qr item consumed
     */
    public void match(final String field, final BiConsumer<IrItem, JsonObject> consumer) {
        this.analyzer.match(field, consumer);
    }

    /**
     * @param fieldExpr {@link java.lang.String} Removed fieldExpr
     *
     * @return {@link Criteria}
     */
    @SuppressWarnings("all")
    public Criteria removeBy(final String fieldExpr) {
        this.analyzer.remove(fieldExpr, true);
        return this;
    }

    /**
     * Return to condition of json object.
     *
     * @return {@link JsonObject}
     */
    public JsonObject toJson() {
        return this.analyzer.toJson();
    }
}

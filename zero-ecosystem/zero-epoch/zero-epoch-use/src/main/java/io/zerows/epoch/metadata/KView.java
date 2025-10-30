package io.zerows.epoch.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;

/**
 * Vis means `View`, the spelling is Denmark language instead of English
 * to avoid View word in future critical usage, also this word is simple
 * for using.
 *
 * The data structure is
 *
 * <pre><code class="json">
 *     {
 *        "position": "The position of current view",
 *        "view": "The view name of current"
 *     }
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KView extends JsonObject {
    /*
     * Private Constructor
     * This data could be put into PointParam here
     */
    private KView(final String view, final String position) {
        super();
        this.put(KName.VIEW, view);
        this.put(KName.POSITION, position);
    }

    private KView(final JsonObject json) {
        super();
        if (Ut.isNotNil(json)) {
            this.mergeIn(json);
        }
    }

    public static KView create(final JsonArray input) {
        final String view;
        final String position;
        if (Ut.isNil(input)) {
            /* Empty */
            view = VValue.DFT.V_VIEW;
            position = VValue.DFT.V_POSITION;
        } else {
            final String v = input.getString(VValue.IDX);
            view = Ut.isNil(v) ? VValue.DFT.V_VIEW : v;
            if (1 < input.size()) {
                final String p = input.getString(VValue.ONE);
                position = Ut.isNil(p) ? VValue.DFT.V_POSITION : p;
            } else {
                position = VValue.DFT.V_POSITION;
            }
        }
        return new KView(view, position);
    }

    /*
     * [view, position]
     * Here the sequence is reverted.
     */
    public static KView create(final String literal) {
        if (Ut.isNil(literal)) {
            return create(new JsonArray());
        } else {
            /*
             * Normalized
             * If encoded the literal here, the literal should contains one of
             * [ - %5B
             * , - %2C
             * ] - %5D
             * This Fix should resolve the bug of `view` parameters
             */
            final String normalized;
            if (literal.contains("%5B") ||
                literal.contains("%2C") ||
                literal.contains("%5D")) {
                normalized = Ut.decryptUrl(literal);
            } else {
                normalized = literal;
            }
            final String detected = Ut.aiJArray(normalized);
            final JsonArray data = Ut.toJArray(detected);
            return create(data);
        }
    }

    public static KView smart(final Object json) {
        switch (json) {
            case final KView entries -> {
                // Vis object, convert directly
                return entries;
                // Vis object, convert directly
            }
            case final JsonObject entries -> {
                // InJson object convert to Vis ( sub class )
                return new KView(entries);
                // InJson object convert to Vis ( sub class )
            }
            case final String viewJson -> {
                if (Ut.isJObject(viewJson)) {
                    // The json is literal
                    return new KView(Ut.toJObject(viewJson));
                } else if (Ut.isJArray(viewJson)) {
                    // String literal
                    return create(viewJson);
                } else {
                    // Single view with default position
                    return new KView((String) json, VValue.DFT.V_POSITION);
                }
            }
            case final JsonArray jsonArray -> {
                // JsonArray
                return create(jsonArray);
                // JsonArray
            }
            case null, default -> {
                // Default findRunning
                return new KView(VValue.DFT.V_VIEW, VValue.DFT.V_POSITION);
            }
        }
    }

    public String view() {
        return this.getString(KName.VIEW, VValue.DFT.V_VIEW);
    }

    public String position() {
        return this.getString(KName.POSITION, VValue.DFT.V_POSITION);
    }
}

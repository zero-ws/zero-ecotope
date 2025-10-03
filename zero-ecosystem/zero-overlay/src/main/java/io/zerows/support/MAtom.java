package io.zerows.support;

import io.vertx.core.json.JsonObject;
import io.zerows.constant.VString;
import io.zerows.spi.modeler.AtomNs;
import io.zerows.specification.modeling.HAtom;

import java.util.Objects;

/**
 * @author lang : 2023-05-08
 */
class MAtom {
    private MAtom() {
    }

    static String namespace(final String appName, final String identifier) {
        // 严格模式处理
        final AtomNs atomNs = UtBase.service(AtomNs.class, true);
        if (UtBase.isNil(identifier)) {
            return atomNs.ns(appName);
        } else {
            return atomNs.ns(appName, identifier);
        }
    }

    static String keyAtom(final HAtom atom, final JsonObject options) {
        Objects.requireNonNull(atom);
        final String hashCode = UtBase.isNil(options) ? VString.EMPTY : String.valueOf(options.hashCode());
        return atom.identifier() + "-" + hashCode;
    }
}

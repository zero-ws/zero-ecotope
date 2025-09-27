package io.zerows.extension.mbse.basement.osgi.spi.robin;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.atom.data.DataGroup;

import java.util.Set;

/*
 * OnOff for DataAtom
 */
public interface Switcher {
    /*
     * Single record switching
     */
    Future<DataAtom> atom(JsonObject data, DataAtom defaultAtom);

    /*
     * Multi record switching
     */
    Future<Set<DataGroup>> atom(JsonArray data, DataAtom atom);
}

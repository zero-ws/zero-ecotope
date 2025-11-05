package io.zerows.extension.module.mbsecore.plugins;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.extension.module.mbsecore.metadata.data.DataGroup;

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

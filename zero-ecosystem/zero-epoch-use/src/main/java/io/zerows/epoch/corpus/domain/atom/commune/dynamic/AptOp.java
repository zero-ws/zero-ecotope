package io.zerows.epoch.corpus.domain.atom.commune.dynamic;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.typed.ChangeFlag;

/*
 * Package scope
 */
interface AptOp<T> {
    /* Old */
    T dataO();

    /* New */
    T dataN();

    /* Replace, Save */
    T dataS();

    /* Append */
    T dataA();

    /* Current data ( Maybe update ), Capture default value */
    T dataI();

    /* Return current type of Change */
    ChangeFlag type();

    T set(T dataArray);

    /* Update data based on `current`. */
    AptOp<T> update(JsonObject data);
}

package io.zerows.epoch.corpus.container.uca.gateway;

import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.epoch.corpus.model.Event;

/**
 * Different type for worklow building
 *
 * @param <Context>
 */
public interface Differ<Context> {

    Aim<Context> build(Event event);
}

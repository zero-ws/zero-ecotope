package io.zerows.epoch.corpus.container.uca.gateway;

import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.zdk.Aim;

/**
 * Different type for worklow building
 *
 * @param <Context>
 */
public interface Differ<Context> {

    Aim<Context> build(ActorEvent event);
}

package io.zerows.core.web.container.uca.gateway;

import io.zerows.core.web.io.zdk.Aim;
import io.zerows.core.web.model.atom.Event;

/**
 * Different type for worklow building
 *
 * @param <Context>
 */
public interface Differ<Context> {

    Aim<Context> build(Event event);
}

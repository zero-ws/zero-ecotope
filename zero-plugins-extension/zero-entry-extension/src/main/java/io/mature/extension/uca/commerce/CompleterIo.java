package io.mature.extension.uca.commerce;

import io.vertx.core.Future;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;

/*
 * 批量 / 单量分流器
 */
public interface CompleterIo<T> {

    CompleterIo<T> bind(final Switcher switcher);

    Future<T> create(T input);

    Future<T> update(T input);

    Future<T> remove(T input);

    Future<T> find(T input);
}

package io.zerows.specification.configuration;

/**
 * @author lang : 2025-10-10
 */
public interface HLife {

    void whenStart(HSetting setting);

    default void whenStop(final HSetting setting) {
    }

    default void whenRemote(final HSetting setting) {
    }
}

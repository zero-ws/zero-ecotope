package io.zerows.support;

/**
 * @author lang : 2025-09-27
 */
@FunctionalInterface
public interface TiConsumer<I1, I2, I3> {
    void accept(I1 i1, I2 i2, I3 i3);
}

package io.zerows.epoch.jigsaw;

import io.zerows.epoch.boot.ZeroFs;

/**
 * @author lang : 2025-12-16
 */
abstract class MakerIoBase<T> implements MakerIo<T> {
    private final ZeroFs io;

    protected MakerIoBase(final ZeroFs io) {
        this.io = io;
    }

    protected ZeroFs io() {
        return this.io;
    }
}

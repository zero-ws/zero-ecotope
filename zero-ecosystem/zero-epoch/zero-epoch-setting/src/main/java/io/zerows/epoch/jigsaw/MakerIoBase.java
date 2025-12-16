package io.zerows.epoch.jigsaw;

import io.zerows.epoch.boot.ZeroOr;

/**
 * @author lang : 2025-12-16
 */
abstract class MakerIoBase<T> implements MakerIo<T> {
    private final ZeroOr io;

    protected MakerIoBase(final ZeroOr io) {
        this.io = io;
    }

    protected ZeroOr io() {
        return this.io;
    }
}

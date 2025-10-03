package io.zerows.epoch.bootplus.extension.uca.graphic;

import io.zerows.epoch.bootplus.extension.refine.Ox;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.operation.HDao;

public abstract class AbstractPlotter implements Plotter {

    protected transient HArk ark;

    @Override
    public Plotter bind(final HArk ark) {
        this.ark = ark;
        return this;
    }

    protected HDao dao(final String identifier) {
        final HApp app = this.ark.app();
        return Ox.toDao(app.option(KName.APP_ID), identifier);
    }
}

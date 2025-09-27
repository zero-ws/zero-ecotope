package io.mature.extension.uca.graphic;

import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.mature.extension.refine.Ox;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.core.constant.KName;

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

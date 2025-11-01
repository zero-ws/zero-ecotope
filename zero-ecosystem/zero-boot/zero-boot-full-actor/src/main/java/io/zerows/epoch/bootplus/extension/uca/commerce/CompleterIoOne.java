package io.zerows.epoch.bootplus.extension.uca.commerce;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.util.Ko;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowAdd;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowDelete;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowEdit;
import io.zerows.epoch.bootplus.extension.uca.concrete.ArrowFind;
import io.zerows.epoch.bootplus.extension.uca.plugin.SwitcherAgile;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;
import io.zerows.program.Ux;
import io.zerows.specification.modeling.operation.HDao;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class CompleterIoOne implements CompleterIo<JsonObject> {
    private final transient SwitcherAgile switcher;

    protected CompleterIoOne(final HDao dao, final DataAtom atom) {
        this.switcher = new SwitcherAgile().initialize(atom, dao);
    }

    @Override
    public CompleterIo<JsonObject> bind(final Switcher switcher) {
        this.switcher.bind(switcher);
        return this;
    }

    @Override
    public Future<JsonObject> create(final JsonObject input) {
        return this.switcher.switchAsync(input, ArrowAdd::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> update(final JsonObject input) {
        return this.switcher.switchAsync(input, ArrowEdit::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> remove(final JsonObject input) {
        return this.switcher.switchAsync(input, ArrowDelete::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> find(final JsonObject input) {
        return this.switcher.switchAsync(input, ArrowFind::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    protected Future<JsonObject> failure(final Throwable error) {
        if (Objects.nonNull(error)) {
            error.printStackTrace();
        }
        Ko.db(this.getClass(), this.switcher.atom(), error);
        return Ux.futureJ();
    }
}

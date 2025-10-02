package io.zerows.epoch.bootplus.extension.uca.commerce;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.uca.concrete.AgileAdd;
import io.zerows.epoch.bootplus.extension.uca.concrete.AgileDelete;
import io.zerows.epoch.bootplus.extension.uca.concrete.AgileEdit;
import io.zerows.epoch.bootplus.extension.uca.concrete.AgileFind;
import io.zerows.epoch.bootplus.extension.uca.log.Ko;
import io.zerows.epoch.bootplus.extension.uca.plugin.AgileSwitcher;
import io.zerows.epoch.corpus.Ux;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.osgi.spi.robin.Switcher;
import io.zerows.specification.modeling.operation.HDao;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class CompleterIoOne implements CompleterIo<JsonObject> {
    private final transient AgileSwitcher switcher;

    protected CompleterIoOne(final HDao dao, final DataAtom atom) {
        this.switcher = new AgileSwitcher().initialize(atom, dao);
    }

    @Override
    public CompleterIo<JsonObject> bind(final Switcher switcher) {
        this.switcher.bind(switcher);
        return this;
    }

    @Override
    public Future<JsonObject> create(final JsonObject input) {
        return this.switcher.switchAsync(input, AgileAdd::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> update(final JsonObject input) {
        return this.switcher.switchAsync(input, AgileEdit::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> remove(final JsonObject input) {
        return this.switcher.switchAsync(input, AgileDelete::new)
            .compose(arrow -> arrow.processAsync(input), this::failure);
    }

    @Override
    public Future<JsonObject> find(final JsonObject input) {
        return this.switcher.switchAsync(input, AgileFind::new)
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

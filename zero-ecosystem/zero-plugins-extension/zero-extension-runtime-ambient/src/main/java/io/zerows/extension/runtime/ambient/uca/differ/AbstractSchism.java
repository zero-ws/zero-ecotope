package io.zerows.extension.runtime.ambient.uca.differ;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.normalize.KMarkAtom;
import io.zerows.epoch.common.shared.program.KRef;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.enums.modeling.EmAttribute;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityChangeDao;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XActivityDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivity;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XActivityChange;
import io.zerows.extension.runtime.ambient.exception._80304Exception409TrackableConflict;
import io.zerows.specification.modeling.HAtom;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractSchism implements Schism {
    protected transient HAtom atom;

    @Override
    public Schism bind(final HAtom atom) {
        Objects.requireNonNull(atom);
        final KMarkAtom marker = atom.marker();
        final boolean trackable;
        if (Objects.isNull(marker)) {
            trackable = true;
        } else {
            trackable = marker.trackable();
        }
        if (!trackable) {
            throw new _80304Exception409TrackableConflict(atom.identifier());
        }
        this.atom = atom;
        // Second Checking
        final Set<String> trackFields = this.onTrack();
        if (trackFields.isEmpty()) {
            throw new _80304Exception409TrackableConflict(atom.identifier());
        }
        return this;
    }

    // ---------------------- Re-Use the Attribute -----------------------
    /*
     * Tracking field
     * isTrack = true
     */
    protected Set<String> onTrack() {
        Objects.requireNonNull(this.atom);
        final KMarkAtom marker = this.atom.marker();
        return marker.enabled(EmAttribute.Marker.track);
    }

    protected Future<JsonObject> createActivity(final XActivity activity, final List<XActivityChange> changes) {
        final KRef responseJ = new KRef();
        return Ux.Jooq.on(XActivityDao.class).insertJAsync(activity)
            .compose(responseJ::future)
            .compose(nil -> Ux.Jooq.on(XActivityChangeDao.class).insertAsync(changes))
            .compose(nil -> Ux.future(responseJ.get()));
    }
    // ---------------------- Provide the default operation to throw 501 ---------------------

    @Override
    public Future<JsonObject> diffAsync(final JsonObject recordO, final JsonObject recordN, final Supplier<Future<XActivity>> activityFn) {
        // Default should be 501
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

}

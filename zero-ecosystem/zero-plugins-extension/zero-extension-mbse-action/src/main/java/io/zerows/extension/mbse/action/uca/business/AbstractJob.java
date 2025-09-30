package io.zerows.extension.mbse.action.uca.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.common.app.KIntegration;
import io.zerows.common.datamation.KDictConfig;
import io.zerows.common.datamation.KFabric;
import io.zerows.common.datamation.KMap;
import io.zerows.common.normalize.KIdentity;
import io.zerows.common.program.KRef;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.core.database.atom.Database;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.zdk.Service;
import io.zerows.core.web.scheduler.atom.Mission;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.extension.mbse.action.util.Jt;
import io.zerows.specification.modeling.HRule;
import io.zerows.unity.Ux;

import java.util.Objects;

/*
 * Abstract Service
 */
public abstract class AbstractJob implements Service {

    /*
     * dict
     * - dictConfig
     * - dictComponent
     * - dictEpsilon
     */
    protected transient KFabric fabric;

    /*
     * The four reference source came from Service instance here
     * dict
     * - dictConfig
     * - dictComponent
     * - dictEpsilon
     *
     * identity
     * - identityComponent
     * - identity
     *
     * options
     * - serviceConfig
     *
     * mapping
     * - mappingConfig
     * - mappingMode
     * - mappingComponent
     */
    protected KDictConfig dict() {
        final KDictConfig dict = Jt.toDict(this.service());
        if (Objects.isNull(this.fabric)) {
            this.fabric = KFabric.create().epsilon(dict.configUse());
        }
        return dict;
    }

    @Override
    public KMap mapping() {
        return Jt.toMapping(this.service());
    }

    @Override
    public KIdentity identity() {
        return Jt.toIdentity(this.service());
    }

    @Override
    public HRule rule() {
        return Jt.toRule(this.service());
    }

    @Override
    public JsonObject options() {
        return Jt.toOptions(this.service());
    }

    /*
     * Get `IService` reference here.
     */
    protected IService service() {
        final JsonObject metadata = this.mission().getMetadata();
        return Ut.deserialize(metadata.getJsonObject(KName.SERVICE), IService.class);
    }

    /*
     * All `Job` sub-class must implement this method to get `Mission` object
     * This component configuration are all created by `Mission` instead of
     * channel @Contract.
     */
    protected abstract Mission mission();

    // ----------- Database / KIntegration --------

    /*
     * 1. Get database reference ( Database )
     * 2. Get dao reference ( OxDao )
     * 3. Get data argument reference ( DataAtom )
     */
    protected Database database() {
        return Jt.toDatabase(this.service());
    }

    protected KIntegration integration() {
        return Jt.toIntegration(this.service());
    }

    /*
     * Under way processing based on `identifier`
     */
    protected Future<KRef> underway(final String identifier) {
        /*
         * Parameters
         */
        final String key = this.service().getSigma();
        return Jt.toDictionary(key, KWeb.CACHE.JOB_DIRECTORY, identifier, this.dict()).compose(dictionary -> {
            this.fabric.dictionary(dictionary);
            /*
             * Chain 引用
             */
            final KRef refer = new KRef();
            refer.add(dictionary);
            return Ux.future(refer);
        });
    }

    // ----------- Logger component --------------
    protected Annal logger() {
        return Annal.get(this.getClass());
    }
}

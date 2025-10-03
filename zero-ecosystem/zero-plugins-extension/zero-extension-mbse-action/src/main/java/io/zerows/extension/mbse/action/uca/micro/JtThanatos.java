package io.zerows.extension.mbse.action.uca.micro;

import io.vertx.core.Future;
import io.zerows.epoch.annotations.On;
import io.zerows.platform.metadata.KRef;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.zdk.AbstractMission;
import io.zerows.epoch.sdk.modeling.Commercial;
import io.zerows.extension.mbse.action.uca.monitor.JtMonitor;

/**
 * 「Job Instance」
 * Configured in database, it's not need @Job
 * When the job has been stored into database, it's not need @Job, in this situation
 * zero system will ignore scanned this job instead of other implemention
 * <p>
 * The job scan
 * - CodeStore -> @Job
 * - ExtensionStore -> JtHypnos ( Database stored ) -> ( Job + Service )
 */
// @Job(value = JobType.CONTAINER)
public class JtThanatos extends AbstractMission {
    private transient final JtMonitor monitor = JtMonitor.create(this.getClass());

    /*
     * Data example
     * {
     *     "key" : "f723d571-39c8-4823-b0d6-82e15e463906",
     *     "service" : {
     *          "key" : "c77b751b-5b71-418d-abcc-6ed328f8e900",
     *          "namespace" : "io.extension.vie.app.ex",
     *          "name" : "bastion.data-sync",
     *          "comment" : "堡垒机同步专用",
     *          "isWorkflow" : false,
     *          "isGraphic" : false,
     *          "channelType" : "ACTOR",
     *          "serviceRecord" : "io.extension.bastion.data.BastionRecord",
     *          "serviceComponent" : "io.extension.bastion.component.SyncComponent",
     *          "identifier" : "ci.bastion",
     *          "sigma" : "HGyhDZ5p96jaNFbpZMl6s6SJWD4PNzAm",
     *          "language" : "cn",
     *          "active" : true
     *     },
     *     "config" : null,
     *     "appId" : "86db806c-97a0-4173-9a6d-632cb1a82ac7",
     *     "_class" : "io.vertx.mod.jet.io.vertx.up.argument.JtJob",
     *     "job" : {
     *          "key" : "f723d571-39c8-4823-b0d6-82e15e463906",
     *          "namespace" : "io.extension.vie.app.ex",
     *          "name" : "bastion.sync",
     *          "code" : "bastion.sync",
     *          "type" : "PLAN",
     *          "duration" : 5000,
     *          "proxy" : "io.vertx.mod.micro.operation.jet.JtThanatos",
     *          "serviceId" : "c77b751b-5b71-418d-abcc-6ed328f8e900",
     *          "sigma" : "HGyhDZ5p96jaNFbpZMl6s6SJWD4PNzAm"
     *     }
     * }
     */
    @On
    public Future<Envelop> start(final Envelop envelop,
                                 final Mission mission,
                                 final Commercial commercial,
                                 final KRef refer) {
        /*
         * Here provide another reference for Mission injection
         */
        return JtPandora.async(envelop, commercial, mission, refer, this.monitor);
    }
}

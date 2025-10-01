package io.zerows.extension.mbse.action.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.web.scheduler.atom.Mission;
import io.zerows.epoch.corpus.web.scheduler.plugins.JobClient;
import io.zerows.epoch.corpus.web.scheduler.plugins.JobInfix;
import io.zerows.epoch.enums.EmJob;
import io.zerows.extension.mbse.action.atom.JtJob;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.bootstrap.JtPin;
import io.zerows.extension.mbse.action.bootstrap.ServiceEnvironment;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.domain.tables.pojos.IJob;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;

import java.util.Objects;

public class AmbientService implements AmbientStub {

    @Override
    public Future<JsonObject> updateJob(final IJob job, final IService service) {
        final String sigma = job.getSigma();
        final HArk ark = Ke.ark(sigma);
        if (Objects.isNull(ark)) {
            /*
             * 500 XHeader Exception, could not be found
             */
            return null; // Fn.outWeb(true, CombineAppException.class, this.getClass(), sigma);
        }


        /*
         * JtJob combining
         */
        final JtJob instance = new JtJob(job, service).bind(ark);
        /*
         * XHeader Flush Cache
         */
        final HApp app = ark.app();
        final String appId = app.option(KName.APP_ID);
        final ServiceEnvironment environment = JtPin.serviceEnvironment().get(appId);
        environment.flushJob(instance);
        /*
         * Mission here for JobPool updating
         */
        final Mission mission = instance.toJob();
        /*
         * Reset `Status`
         */
        mission.setStatus(EmJob.Status.STOPPED);
        final JobClient client = JobInfix.getClient();
        client.save(mission);
        return Ux.future(JobKit.toJson(mission));
    }

    @Override
    public Future<JsonObject> updateUri(final IApi api, final IService service) {
        final String sigma = api.getSigma();
        final HArk ark = Ke.ark(sigma);
        if (Objects.isNull(ark)) {
            /*
             * 500 XHeader Exception, could not be found
             */
            return null; // Fn.outWeb(CombineAppException.class, this.getClass(), sigma);
        }

        /*
         * JtUri combining
         */
        final JtUri instance = new JtUri(api, service).bind(ark);
        /*
         * XHeader Flush Cache
         */
        final HApp app = ark.app();
        final String appId = app.option(KName.APP_ID);
        final ServiceEnvironment environment = JtPin.serviceEnvironment().get(appId);
        environment.flushUri(instance);
        /*
         * Response web
         */
        return Ux.future(instance.toJson());
    }
}

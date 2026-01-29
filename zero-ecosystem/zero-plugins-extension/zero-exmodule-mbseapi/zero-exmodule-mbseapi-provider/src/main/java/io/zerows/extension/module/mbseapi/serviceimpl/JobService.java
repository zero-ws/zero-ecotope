package io.zerows.extension.module.mbseapi.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.qr.Ir;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.mbseapi.boot.Jt;
import io.zerows.extension.module.mbseapi.domain.tables.daos.IJobDao;
import io.zerows.extension.module.mbseapi.domain.tables.daos.IServiceDao;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IJob;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;
import io.zerows.extension.module.mbseapi.servicespec.AmbientStub;
import io.zerows.extension.module.mbseapi.servicespec.JobStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JobService implements JobStub {
    @Inject
    private transient AmbientStub ambient;

    @Override
    public Future<JsonObject> searchJobs(final String sigma, final JsonObject body, final boolean grouped) {
        final Ir qr = Ir.create(body);
        qr.getCriteria().save("sigma", sigma);
        final JsonObject condition = qr.toJson();
        log.info("[ ZERO ] ( Job ) 任务条件：{}", condition);
        return DB.on(IJobDao.class)
            .searchJAsync(condition)
            .compose(jobs -> {
                /*
                 * Result for all jobs that are belong to current sigma here.
                 */
                final List<IJob> jobList = Ut.fromPage(jobs, IJob.class);
                final Set<String> codes = jobList.stream()
                    .filter(Objects::nonNull)
                    /*
                     * Job name calculation for appending namespace
                     */
                    .map(Jt::jobCode)
                    .collect(Collectors.toSet());
                log.info("[ ZERO ] ( Job ) 任务编码：{}, 输入 Sigma：{}", codes.size(), sigma);
                return JobKit.fetchMission(codes).compose(normalized -> {
                    jobs.put("list", normalized);
                    /*
                     * count group
                     * */
                    if (grouped) {
                        final JsonObject criteria = qr.getCriteria().toJson();
                        return DB.on(IJobDao.class).countByAsync(criteria, "group")
                            .compose(aggregation -> {
                                final JsonObject aggregationJson = new JsonObject();
                                aggregation.forEach(aggregationJson::put);
                                jobs.put("aggregation", aggregationJson);
                                return Ux.future(jobs);
                            });
                    } else {
                        return Ux.future(jobs);
                    }
                });
            });
    }

    @Override
    public Future<JsonObject> fetchByKey(final String key) {
        return DB.on(IJobDao.class)
            .<IJob>fetchByIdAsync(key)
            /*
             * 1) Supplier here for `JsonObject` generated
             * 2) Mission conversation here to JsonObject directly
             */
            .compose(job -> JobKit.fetchMission(Jt.jobCode(job)))
            .map(item -> Objects.isNull(item) ? new JsonObject() : item);
    }

    @Override
    public Future<JsonObject> update(final String key, final JsonObject data) {
        /*
         * 1. Service / Job Split
         */
        JsonObject serviceJson = data.getJsonObject(KName.SERVICE);
        if (Ut.isNil(serviceJson)) {
            serviceJson = new JsonObject();
        } else {
            serviceJson = serviceJson.copy();
            data.remove(KName.SERVICE);
        }
        /*
         * 2. Upsert by Key for Job instance
         */
        final IJob job = Ux.fromJson(data, IJob.class);
        final IService service = JobKit.fromJson(serviceJson);
        return DB.on(IJobDao.class)
            /*
             * 3. Upsert by Key for Service instance
             */
            .upsertAsync(job.getId(), job)
            .compose(updatedJob -> DB.on(IServiceDao.class)
                .upsertAsync(service.getId(), service)
                /*
                 * 4. Merge updatedJob / updatedService
                 * -- Call `AmbientService` to updateJob cache
                 * -- Cache updating ( IJob / IService )
                 */
                .compose(updatedSev ->
                    this.ambient.updateJob(updatedJob, updatedSev)));
    }
}

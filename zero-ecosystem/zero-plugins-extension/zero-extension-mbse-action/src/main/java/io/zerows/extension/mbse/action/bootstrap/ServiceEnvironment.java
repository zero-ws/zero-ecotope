package io.zerows.extension.mbse.action.bootstrap;

import io.r2mo.function.Fn;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.constant.KName;
import io.zerows.component.log.Annal;
import io.zerows.platform.metadata.KDS;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.Database;
import io.zerows.epoch.corpus.database.cp.zdk.DataPool;
import io.zerows.platform.exception._40103Exception500ConnectAmbient;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import io.zerows.extension.mbse.action.atom.JtJob;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.domain.tables.daos.IApiDao;
import io.zerows.extension.mbse.action.domain.tables.daos.IJobDao;
import io.zerows.extension.mbse.action.domain.tables.daos.IServiceDao;
import io.zerows.extension.mbse.action.domain.tables.pojos.IApi;
import io.zerows.extension.mbse.action.domain.tables.pojos.IJob;
import io.zerows.extension.mbse.action.domain.tables.pojos.IService;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.mbse.action.util.Jt.LOG;

/*
 * Cross Data Object
 * 1) App / Source information here
 * 2) Pool ( DSLContext, Connection, DataSource )
 */
public class ServiceEnvironment {

    private static final Annal LOGGER = Annal.get(ServiceEnvironment.class);
    /* Pool of Jobs, it will be consumed by each application */
    private final transient ConcurrentMap<String, JtJob> jobs
        = new ConcurrentHashMap<>();
    /* Pool of JtUri */
    private final transient ConcurrentMap<String, JtUri> uris
        = new ConcurrentHashMap<>();

    /* XApp application, class JtApp */
    private final transient HArk ark;
    private final transient Set<String> condition = new HashSet<>();
    private final transient DataPool poolMeta;
    /*
     * Service Map
     */
    private final ConcurrentMap<String, IService> serviceMap = new ConcurrentHashMap<>();
    /* Data source, DSLContext, DataSource */
    private transient DataPool pool;

    /**
     * 包内初始化
     *
     * @param ark 应用配置容器
     */
    ServiceEnvironment(final HArk ark) {
        this.ark = ark;
        final HApp app = ark.app();

        final String sigma = app.option(KName.SIGMA);
        if (Ut.isNil(sigma)) {
            throw new _40103Exception500ConnectAmbient();
        }
        this.condition.add(sigma);

        // 是否配置了动态数据源
        // 1. 动态数据库         dynamic
        // 2. 静态元数据库       database
        final KDS<Database> kds = ark.database();
        if (Objects.nonNull(kds.dynamic())) {
            this.pool = DataPool.create(kds.dynamic());
        }
        this.poolMeta = DataPool.create(kds.database());
    }

    @Fluent
    public Future<ServiceEnvironment> init(final Vertx vertx) {

        return this.initService(vertx).compose(nil -> {

            final List<Future<Boolean>> futures = new ArrayList<>();
            /*
             * IApi + IService
             */
            futures.add(this.initUris(vertx));
            /*
             * IJob + IService
             */
            futures.add(this.initJobs(vertx));
            return FnBase.combineT(futures).compose(res -> Ux.future(this));
        });
    }

    private Future<Boolean> initService(final Vertx vertx) {
        final IServiceDao serviceDao = new IServiceDao(this.poolMeta.getExecutor().configuration(), vertx);
        return serviceDao.findManyBySigma(this.condition).compose(services -> {
            this.serviceMap.putAll(Ut.elementZip(services, IService::getKey, service -> service));
            LOG.Init.info(LOGGER, "AE ( {0} ) Service initialized !!!",
                String.valueOf(this.serviceMap.keySet().size()));
            return Ux.future(Boolean.TRUE);
        });
    }

    private Future<Boolean> initJobs(final Vertx vertx) {
        final IJobDao jobDao = new IJobDao(this.poolMeta.getExecutor().configuration(), vertx);
        if (this.jobs.isEmpty()) {
            /*
             * Map for JOB + Service
             * serviceKey -> job
             * serviceKey -> service ( Cached )
             */
            return jobDao.findManyBySigma(this.condition).compose(jobList -> {
                final ConcurrentMap<String, IJob> jobMap = Ut.elementZip(jobList, IJob::getServiceId, job -> job);
                /* Job / Service Bind into data here */
                jobMap.keySet().stream()
                    .map(serviceId -> new JtJob(jobMap.get(serviceId), this.serviceMap.get(serviceId))
                        /* Job Bind app id directly */
                        .<JtJob>bind(this.ark)
                    )
                    .forEach(entry -> this.jobs.put(entry.key(), entry));
                LOG.Init.info(LOGGER, "AE ( {0} ) Jobs initialized !!!",
                    String.valueOf(this.jobs.keySet().size()));
                return Ux.future(Boolean.TRUE);
            });
        } else {
            return Ux.future(Boolean.TRUE);
        }
    }

    private Future<Boolean> initUris(final Vertx vertx) {
        final IApiDao apiDao = new IApiDao(this.poolMeta.getExecutor().configuration(), vertx);
        if (this.uris.isEmpty()) {
            /*
             * Map for API + Service
             * serviceKey -> api
             * serviceKey -> service ( Cached )
             */
            return apiDao.findManyBySigma(this.condition).compose(apiList -> {
                final ConcurrentMap<String, IApi> apiMap = Ut.elementZip(apiList, IApi::getServiceId, api -> api);
                /* Uri / Service Bind into data here */
                apiMap.keySet().stream()
                    .map(serviceId -> new JtUri(apiMap.get(serviceId), this.serviceMap.get(serviceId))
                        /* Job Bind app id directly */
                        .<JtUri>bind(this.ark))
                    .forEach(entry -> this.uris.put(entry.key(), entry));
                LOG.Init.info(LOGGER, "AE ( {0} ) Api initialized !!!",
                    String.valueOf(this.uris.keySet().size()));
                return Ux.future(Boolean.TRUE);
            });
        } else {
            return Ux.future(Boolean.TRUE);
        }
    }

    public Connection getConnection() {
        if (Objects.isNull(this.pool)) {
            return null;
        }
        return Fn.jvmOr(() -> this.pool.getDataSource().getConnection());
    }

    public DataPool getPool() {
        return this.pool;
    }

    public Set<JtUri> routes() {
        return new HashSet<>(this.uris.values());
    }

    public Set<JtJob> jobs() {
        return new HashSet<>(this.jobs.values());
    }

    /*
     * Cache flush for Job
     */
    public void flushJob(final JtJob job) {
        /*
         * serviceKey -> service (Cached)
         */
        final IService service = job.service();
        this.serviceMap.put(service.getKey(), service);
        /*
         * serviceKey -> job (JtJob)
         */
        this.jobs.put(service.getKey(), job);
    }

    /*
     * Cache flush for Uri
     */
    public void flushUri(final JtUri uri) {
        /*
         * serviceKey -> service (Cached)
         */
        final IService service = uri.service();
        this.serviceMap.put(service.getKey(), service);
        /*
         * serviceKey -> uri (JtUri)
         */
        this.uris.put(service.getKey(), uri);
    }
}

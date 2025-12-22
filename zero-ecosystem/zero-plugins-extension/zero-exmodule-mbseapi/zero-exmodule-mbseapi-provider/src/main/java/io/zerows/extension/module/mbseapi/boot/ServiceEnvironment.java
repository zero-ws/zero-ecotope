package io.zerows.extension.module.mbseapi.boot;

import io.r2mo.base.dbe.DBS;
import io.r2mo.dbe.jooq.core.domain.JooqDatabase;
import io.r2mo.function.Fn;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.mbseapi.domain.tables.daos.IApiDao;
import io.zerows.extension.module.mbseapi.domain.tables.daos.IJobDao;
import io.zerows.extension.module.mbseapi.domain.tables.daos.IServiceDao;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IApi;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IJob;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.extension.module.mbseapi.metadata.JtJob;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.platform.exception._40103Exception500ConnectAmbient;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 启动环境中的服务环境，此服务环境对接动态接口模型，针对每一个应用环境，会包含一套独立的发布结构
 * <pre>
 *     1. 一个 {@link HArk} 环境 / 应用和租户基本信息
 *     2. 当前默认数据源 {@link DBS}，内置辅助访问对象 {@link JooqDatabase}
 *     3. 系统动态环境中主要包含两种业务逻辑模型
 *        - 接口模型：
 *          映射成 HTTP 中常用的 RESTful 接口请求，拆分成接口定义和业务组件定义
 *          {@link JtUri} = {@link IApi} x 1 + {@link IService} x 1
 *        - 任务模型：
 *          映射成 JOB 中的后台任务，若存在客户端交互可直接转换成 WebSocket 主动模型，拆分成任务定义和业务组件定义
 *          {@link JtJob} = {@link IJob} x 1 + {@link IService} x 1
 *      4. 不论是接口还是任务，底层都会对应 {@link IService} 业务组件定义。
 * </pre>
 *
 * @author lang
 */
@Slf4j
public class ServiceEnvironment {

    /**
     * key = ns + job code
     * 逻辑对应 {@link JtJob} / {@link IService} / {@link IJob}
     */
    private final transient ConcurrentMap<String, JtJob> jobs = new ConcurrentHashMap<>();

    /**
     * key = ns + uri code
     * 逻辑对应 {@link JtUri} / {@link IService} / {@link IApi}
     */
    private final transient ConcurrentMap<String, JtUri> uris = new ConcurrentHashMap<>();

    /**
     * Ref: XApp
     * 应用程序配置容器：{@link HArk} / {@link HApp}
     */
    private final transient HArk ark;
    private final transient Set<String> condition = new HashSet<>();

    /**
     * key = ns + service code
     * 服务组件处理：{@link IService}，对应核心服务组件
     */
    private final ConcurrentMap<String, IService> serviceMap = new ConcurrentHashMap<>();

    /**
     * Ref: XSource
     * 数据源对象：{@link DBS}，内置服务对象 {@link JooqDatabase} 访问核心数据源
     */
    private final DBS dbs;
    private JooqDatabase database;

    /**
     * 包内初始化
     *
     * @param ark 应用配置容器
     */
    ServiceEnvironment(final HArk ark) {
        this.ark = ark;
        this.dbs = ark.datasource().findRunning();
        if (Objects.nonNull(this.dbs) && this.dbs.getDatabase() instanceof final JooqDatabase jooqDatabase) {
            this.database = jooqDatabase;
        }
        final HApp app = ark.app();

        final String sigma = app.option(KName.SIGMA);
        if (Ut.isNil(sigma)) {
            throw new _40103Exception500ConnectAmbient();
        }
        this.condition.add(sigma);

        // 是否配置了动态数据源
        // 1. 动态数据库         dynamic
        // 2. 静态元数据库       database
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
        final IServiceDao serviceDao = new IServiceDao(this.database.getConfiguration(), vertx);
        return serviceDao.findManyBySigma(this.condition).compose(services -> {
            this.serviceMap.putAll(Ut.elementZip(services, IService::getKey, service -> service));
            log.info("{} ---> 服务环境初始化完成！！！数量 = {}", JtConstant.K_PREFIX_BOOT, this.serviceMap.size());
            return Ux.future(Boolean.TRUE);
        });
    }

    private Future<Boolean> initJobs(final Vertx vertx) {
        final IJobDao jobDao = new IJobDao(this.database.getConfiguration(), vertx);
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
                log.info("{} -> 作业环境初始化完成！！！数量 = {}", JtConstant.K_PREFIX_BOOT, this.jobs.size());
                return Ux.future(Boolean.TRUE);
            });
        } else {
            return Ux.future(Boolean.TRUE);
        }
    }

    private Future<Boolean> initUris(final Vertx vertx) {
        final IApiDao apiDao = new IApiDao(this.database.getConfiguration(), vertx);
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
                log.info("{} -> 接口环境初始化完成！！！数量 = {}", JtConstant.K_PREFIX_BOOT, this.uris.size());
                return Ux.future(Boolean.TRUE);
            });
        } else {
            return Ux.future(Boolean.TRUE);
        }
    }

    public Connection getConnection() {
        return Fn.jvmOr(this.dbs::getConnection);
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

    public boolean isOk() {
        return Objects.nonNull(this.ark)
            && Objects.nonNull(this.dbs);
    }
}

package io.zerows.extension.module.ambient.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.boot.At;
import io.zerows.extension.module.ambient.boot.MDAmbientManager;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.servicespec.AppStub;
import io.zerows.extension.module.ambient.servicespec.InitStub;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.extension.skeleton.spi.ExPrerequisite;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class InitService implements InitStub {
    private static final MDAmbientManager MANAGER = MDAmbientManager.of();
    /**
     * Injection for {@link AppStub}
     */
    @Inject
    private transient AppStub stub;

    /**
     * 「Async」( Creation ) This api is for application initialization at first time.
     * Related Interface: {@link ExInit}
     *
     * @param appId {@link java.lang.String} The application primary key that stored in `KEY` field of `X_APP`.
     * @param data  {@link io.vertx.core.json.JsonObject} The data that will create application instance.
     * @return {@link io.vertx.core.Future}<{@link io.vertx.core.json.JsonObject}>
     */
    @Override
    public Future<JsonObject> initCreation(final String appId,
                                           final JsonObject data) {
        /* Default Future */
        return Ux.future(data.put(KName.KEY, appId))
            /* X_APP initialization */
            .compose(At.initApp().apply())
            /* X_SOURCE initialization */
            .compose(At.initSource().apply())
            /* Database initialization */
            .compose(At.initDatabase().apply())
            /* Extension initialization */
            .compose(this::initDefined)
            /* Data Loading */
            .compose(At.initData().apply())
            /* Image */
            .map(item -> Ut.valueToJObject(item, KName.App.LOGO));
    }

    @Override
    public Future<JsonObject> initEdition(final String appName) {
        return this.initModeling(appName)
            /* Data Loading */
            .compose(At.initData().apply());
    }

    @Override
    public Future<JsonObject> prerequisite(final String appName) {
        /* Prerequisite Extension */
        final ExPrerequisite prerequisite = Objects.requireNonNull(MANAGER.config()).ofPre();
        if (Objects.isNull(prerequisite)) {
            log.info("{} `ExPrerequisite` 组件未配置：null", AtConstant.K_PREFIX_AMB);
            return Ux.future(new JsonObject());
        }

        return prerequisite.prepare(appName);
    }

    @Override
    public Future<JsonObject> initModeling(final String appName) {
        return this.initModeling(appName, null);
    }

    @Override
    public Future<JsonObject> initModeling(final String appName, final String outPath) {
        /* Fetch App */
        return DB.on(XAppDao.class)
            /* X_APP Fetching */
            .fetchOneAsync(KName.NAME, appName)
            .compose(Ux::futureJ)
            /* X_SOURCE fetching, Fetching skip Database initialization */
            .compose(this::initCombine)
            /* Output Path Injection */
            .compose(appJson -> this.initOutput(appJson, outPath))
            .compose(this::initDefined)
            /* Image */
            .map(item -> Ut.valueToJObject(item, KName.App.LOGO));
    }

    private Future<JsonObject> initOutput(final JsonObject combined, final String outPath) {
        if (Ut.isNotNil(outPath)) {
            combined.put(KName.OUTPUT, outPath);
        }
        return Ux.future(combined);
    }

    private Future<JsonObject> initCombine(final JsonObject appJson) {
        return this.stub.fetchSource(appJson.getString(KName.KEY))
            .compose(source -> UObject.create(appJson).append(KName.SOURCE, source).toFuture());
    }

    private Future<JsonObject> initDefined(final JsonObject input) {
        final ExInit initializer = Objects.requireNonNull(MANAGER.config()).ofInit();
        if (Objects.isNull(initializer)) {
            log.info("{} `Init` 组件未配置，null", AtConstant.K_PREFIX_AMB);
            return Ux.future(input);
        }
        return initializer.apply().apply(input);
    }
}

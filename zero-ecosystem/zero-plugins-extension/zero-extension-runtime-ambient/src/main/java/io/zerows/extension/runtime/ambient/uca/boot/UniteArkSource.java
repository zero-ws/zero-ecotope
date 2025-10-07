package io.zerows.extension.runtime.ambient.uca.boot;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.KDS;
import io.zerows.platform.metadata.KDatabase;
import io.zerows.platform.metadata.KArk;
import io.zerows.platform.constant.VOption;
import io.zerows.epoch.database.Database;
import io.zerows.platform.enums.EmDS;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XSource;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-07-08
 */
public class UniteArkSource implements UniteArk<List<XSource>> {

    @Override
    public HArk compile(final XApp app, final List<XSource> sources) {
        // 1. HArk 创建
        final HArk ark = KArk.of(app.getName());
        final JsonObject normalized = new JsonObject();
        /* ID */
        {
            /*
             * 3 Fields of identifiers
             * sigma - Cross module to identify application / container here.
             * id - Inner ambient environment to identify application.
             * appKey - Ox engine used as dynamic identifier here.
             */
            normalized.put(KName.KEY, app.getKey());        // `key` fixed when `api` and `non-api` configuration
            normalized.put(KName.APP_ID, app.getKey());
            normalized.put(KName.APP_KEY, app.getAppKey());
            normalized.put(KName.SIGMA, app.getSigma());
        }
        /* Unique */
        {
            /*
             * System information of application
             * 「Front App」
             * name - the unique name that will be used in front environment variable.  Z_APP
             * code - the application system code here that could be parsed by system.
             * language - language of this application
             * active - Whether enabled ( it's for future )
             */
            normalized.put(KName.NAME, app.getName());
            normalized.put(KName.CODE, app.getCode());
            normalized.put(KName.LANGUAGE, app.getLanguage());
            normalized.put(KName.ACTIVE, app.getActive());
        }
        /* Business information */
        {
            /* Major: Logo and Title */
            normalized.put(KName.App.LOGO, app.getLogo());
            normalized.put(KName.App.TITLE, app.getTitle());
            /*
             * Business information
             * title - display the information on front app
             * logo - display the logo on front app here.
             * icp - icp number of this application.
             * copyRight - copy right of this application.
             * email - administrator email that could be contacted
             */
            final JsonObject business = new JsonObject();
            business.put(KName.App.ICP, app.getIcp());
            business.put(KName.App.EMAIL, app.getEmail());
            business.put(KName.App.COPY_RIGHT, app.getCopyRight());
            normalized.put("income", business);
        }
        /* Deployment information */
        {
            /*
             * Deployment information
             * Back-End
             * domain - application domain information that will be deployed
             * port - application port information that will be exposed.
             * route - application sub routing information
             */
            final JsonObject backend = new JsonObject();
            backend.put(KName.App.DOMAIN, app.getDomain());
            backend.put(KName.App.PORT, app.getPort());
            backend.put(KName.App.ENDPOINT, app.getEndpoint());
            normalized.put("backend", backend);
            // Fix Dynamic Route
            normalized.put(KName.App.ENDPOINT, app.getEndpoint());
            normalized.put(KName.App.ENTRY, app.getEntry());
            /*
             * Front-End
             * path - front end application information
             * urlEntry - Url Entry of Login Home
             * urlMain - Url Entry of Admin Home
             *
             */
            final JsonObject frontend = new JsonObject();
            frontend.put(KName.App.CONTEXT, app.getContext());
            frontend.put(KName.App.URL_LOGIN, app.getUrlLogin());
            frontend.put(KName.App.URL_ADMIN, app.getUrlAdmin());
            normalized.put("frontend", frontend);
            // Fix Dynamic Route
            normalized.put(KName.App.CONTEXT, app.getContext());
        }
        /* Auditor information */
        {
            /*
             * Auditor information of current application.
             * createdAt, createdBy
             * updatedAt, updatedBy
             */
            final JsonObject auditor = new JsonObject();
            auditor.put(KName.CREATED_BY, app.getCreatedBy());
            auditor.put(KName.CREATED_AT, Ut.parse(app.getCreatedAt()).toInstant());
            auditor.put(KName.UPDATED_BY, app.getUpdatedBy());
            auditor.put(KName.UPDATED_AT, Ut.parse(app.getUpdatedAt()).toInstant());
            normalized.put("auditor", auditor);
        }
        // Database
        final KDS<KDatabase> kds = ark.database();
        {
            kds.registry(EmDS.Stored.PRIMARY, Database.getCurrent());
            kds.registry(EmDS.Stored.WORKFLOW, Database.getCamunda());
            kds.registry(EmDS.Stored.HISTORY, Database.getHistory());
        }
        if (Objects.nonNull(sources) && !sources.isEmpty()) {
            final JsonArray sourceArray = new JsonArray();
            final Set<KDatabase> databaseSet = new LinkedHashSet<>();
            sources.forEach(source -> {
                final JsonObject sourceJson = new JsonObject();
                sourceJson.put(VOption.database.HOSTNAME, source.getHostname());
                sourceJson.put(VOption.database.INSTANCE, source.getInstance());
                sourceJson.put(VOption.database.PORT, source.getPort());
                sourceJson.put(VOption.database.CATEGORY, source.getCategory());
                sourceJson.put(VOption.database.JDBC_URL, source.getJdbcUrl());
                sourceJson.put(VOption.database.USERNAME, source.getUsername());
                sourceJson.put(VOption.database.PASSWORD, source.getPassword());
                sourceJson.put(VOption.database.DRIVER_CLASS_NAME, source.getDriverClassName());
                final String jdbcConfig = source.getJdbcConfig();
                if (Ut.isNotNil(jdbcConfig)) {
                    sourceJson.put(VOption.database.OPTIONS, Ut.toJObject(jdbcConfig));
                }
                /*
                 * {
                 *     "source": [
                 *         {
                 *             "hostname": "xx",
                 *             "instance": "instance",
                 *             "port": "",
                 *             "category": "",
                 *             "jdbcUrl": "",
                 *             "username": "",
                 *             "password": "",
                 *             "driverClassName": "",
                 *             "options": {
                 *             }
                 *         }
                 *     ]
                 * }
                 */
                sourceArray.add(sourceJson);
                final Database database = new Database();
                database.fromJson(sourceJson);
                databaseSet.add(database);
            });
            kds.registry(databaseSet);
        }
        // App InJson
        final HApp appRef = ark.app();
        appRef.option(normalized, true);
        return ark;
    }
}

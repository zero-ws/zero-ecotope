package io.zerows.extension.module.ambient.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.platform.metadata.KArk;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Objects;

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
             * title - display the information join front app
             * logo - display the logo join front app here.
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
            if (Objects.nonNull(app.getCreatedAt())) {
                auditor.put(KName.CREATED_AT, Ut.parse(app.getCreatedAt()).toInstant());
            }
            auditor.put(KName.UPDATED_BY, app.getUpdatedBy());
            if (Objects.nonNull(app.getUpdatedAt())) {
                auditor.put(KName.UPDATED_AT, Ut.parse(app.getUpdatedAt()).toInstant());
            }
            normalized.put("auditor", auditor);
        }
        // UPD-004: Remove Database Configuration Here
        // Database
        //        final OldKDS<KDatabase> oldKds = ark.database();
        //        {
        //            oldKds.registry(EmDS.DB.PRIMARY, OldDatabase.getCurrent());
        //            oldKds.registry(EmDS.DB.WORKFLOW, OldDatabase.getCamunda());
        //            oldKds.registry(EmDS.DB.HISTORY, OldDatabase.getHistory());
        //        }
        //        if (Objects.nonNull(sources) && !sources.isEmpty()) {
        //            final JsonArray sourceArray = new JsonArray();
        //            final Set<KDatabase> databaseSet = new LinkedHashSet<>();
        //            sources.forEach(source -> {
        //                final JsonObject sourceJson = new JsonObject();
        //                sourceJson.put(YmlOption.database.HOSTNAME, source.getHostname());
        //                sourceJson.put(YmlOption.database.INSTANCE, source.getInstance());
        //                sourceJson.put(YmlOption.database.PORT, source.getPort());
        //                sourceJson.put(YmlOption.database.CATEGORY, source.getCategory());
        //                sourceJson.put(YmlOption.database.JDBC_URL, source.getJdbcUrl());
        //                sourceJson.put(YmlOption.database.USERNAME, source.getUsername());
        //                sourceJson.put(YmlOption.database.PASSWORD, source.getPassword());
        //                sourceJson.put(YmlOption.database.DRIVER_CLASS_NAME, source.getDriverClassName());
        //                final String jdbcConfig = source.getJdbcConfig();
        //                if (Ut.isNotNil(jdbcConfig)) {
        //                    sourceJson.put(YmlOption.database.OPTIONS, Ut.toJObject(jdbcConfig));
        //                }
        //                /*
        //                 * {
        //                 *     "source": [
        //                 *         {
        //                 *             "hostname": "xx",
        //                 *             "instance": "instance",
        //                 *             "port": "",
        //                 *             "category": "",
        //                 *             "jdbcUrl": "",
        //                 *             "username": "",
        //                 *             "password": "",
        //                 *             "driverClassName": "",
        //                 *             "options": {
        //                 *             }
        //                 *         }
        //                 *     ]
        //                 * }
        //                 */
        //                sourceArray.add(sourceJson);
        //                final OldDatabase oldDatabase = new OldDatabase();
        //                oldDatabase.fromJson(sourceJson);
        //                databaseSet.add(oldDatabase);
        //            });
        //            oldKds.registry(databaseSet);
        //        }
        // App InJson
        final HApp appRef = ark.app();
        appRef.option(normalized);
        return ark;
    }
}

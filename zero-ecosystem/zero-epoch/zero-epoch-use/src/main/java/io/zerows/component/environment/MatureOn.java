package io.zerows.component.environment;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.MMVariable;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.EmDS;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.Objects;

/**
 * 专用环境变量统一入口，可直接提取核心环境变量，根据不同维度对环境变量进行分类
 * immutable on
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MatureOn implements EnvironmentVariable {
    private static final Cc<String, Mature> CC_MATURE = Cc.openThread();

    // Cloud Connected
    public static JsonObject envPlot(final JsonObject plot) {
        final MMVariable.Set set = MMVariable.Set.of()                                 // AEON_APP
            .save(KName.NAME, EnvironmentVariable.Z_APP)                                        // Z_APP
            .save(KName.NAMESPACE, EnvironmentVariable.Z_NS)                                    // Z_NS
            .saveWith(KName.LANGUAGE, EnvironmentVariable.Z_LANG, KWeb.ARGS.V_LANGUAGE)   // Z_LANG
            .save(KName.SIGMA, EnvironmentVariable.Z_SIGMA);                                    // Z_SIGMA
        // 创建拷贝
        final JsonObject plotJ = Ut.valueJObject(plot, true);
        final Mature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        return mature.configure(plotJ, set);
    }

    // Restful Connected ( Multi Support )
    public static JsonObject envApi(final JsonObject api, final Integer index) {
        final MMVariable.Set set = envServer(EnvironmentVariable.API_HOST, EnvironmentVariable.API_PORT, index);
        // 创建拷贝
        final JsonObject apiJ = Ut.valueJObject(api, true);
        final Mature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        return mature.configure(apiJ, set);
    }

    // Socket Connected ( Multi Support )
    public static JsonObject envSock(final JsonObject sock, final Integer index) {
        final MMVariable.Set set = envServer(EnvironmentVariable.SOCK_HOST, EnvironmentVariable.SOCK_PORT, index);
        // 创建拷贝
        final JsonObject sockJ = Ut.valueJObject(sock, true);
        final Mature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        // 重写规则Z_API_PORTX 重写 Z_SOCK_PORTX
        final JsonObject normJ = mature.configure(sockJ, set);
        final JsonObject apiJ = envApi(new JsonObject(), index);
        if (Objects.isNull(normJ.getValue(KName.PORT))) {
            normJ.put(KName.PORT, apiJ.getValue(KName.PORT));
        }
        return normJ;
    }

    // Database Connected ( Multi Support )
    public static JsonObject envDatabase(final JsonObject database, final EmDS.Stored mode) {
        final MMVariable.Set set;
        if (EmDS.Stored.WORKFLOW == mode) {
            // Workflow
            set = envDatabase(EnvironmentVariable.DB_HOST, EnvironmentVariable.DB_PORT, EnvironmentVariable.DBW_INSTANCE);
        } else if (EmDS.Stored.HISTORY == mode) {
            // History
            set = envDatabase(EnvironmentVariable.DB_HOST, EnvironmentVariable.DB_PORT, EnvironmentVariable.DBH_INSTANCE);
        } else {
            // Database
            set = envDatabase(EnvironmentVariable.DB_HOST, EnvironmentVariable.DB_PORT, EnvironmentVariable.DBS_INSTANCE);
        }
        // 创建拷贝
        final JsonObject databaseJ = Ut.valueJObject(database, true);
        final Mature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        final JsonObject normJ = mature.configure(databaseJ, set);
        // JdbcUrl
        final String jdbcUrl = Ut.valueString(normJ, "jdbcUrl");
        final String replaced;
        if (Objects.nonNull(jdbcUrl) && jdbcUrl.contains(VString.DOLLAR)) {
            final JsonObject parameters = normJ.copy();
            replaced = Ut.fromExpression("`" + jdbcUrl + "`", parameters);
        } else {
            replaced = jdbcUrl;
        }
        normJ.put("jdbcUrl", replaced);
        return normJ;
    }

    // Domain Connected
    public static JsonArray envDomain(final JsonArray domainA) {
        final String domain = ENV.of().get(EnvironmentVariable.CORS_DOMAIN);
        if (Ut.isNil(domain)) {
            return domainA;
        }
        final String[] domainL = domain.split(VString.COMMA);
        if (0 == domainL.length) {
            return domainA;
        }
        // Replaced
        final JsonArray replaced = new JsonArray();
        Arrays.stream(domainL).forEach(replaced::add);
        return replaced;
    }

    private static MMVariable.Set envDatabase(final String host, final String port, final String instance) {
        return MMVariable.Set.of()
            .save(KName.HOSTNAME, host)
            .save(KName.PORT, port, Integer.class)
            .save(KName.INSTANCE, instance);
    }

    private static MMVariable.Set envServer(final String host, final String port, final Integer index) {
        final String envHost;
        final String envPort;
        if (Objects.isNull(index) || VValue.IDX == index) {
            envHost = host;
            envPort = port;
        } else {
            // HOST1, HOST2
            // PORT1, PORT2
            envHost = EnvironmentVariable.API_HOST + index;
            envPort = EnvironmentVariable.API_PORT + index;
        }
        return MMVariable.Set.of()
            .saveWith(KName.HOST, envHost, KWeb.DEPLOY.HOST)       // Z_API_HOSTX
            .save(KName.PORT, envPort, Integer.class);                     // Z_API_PORTX
    }
}

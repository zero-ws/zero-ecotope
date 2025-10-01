package io.zerows.module.metadata.uca.environment;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.program.KVarSet;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.core.util.Ut;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.constant.VValue;
import io.zerows.epoch.enums.EmDS;
import io.zerows.epoch.runtime.HMacrocosm;
import io.zerows.specification.configuration.boot.HMature;

import java.util.Arrays;
import java.util.Objects;

/**
 * 专用环境变量统一入口，可直接提取核心环境变量，根据不同维度对环境变量进行分类
 * immutable on
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MatureOn implements HMacrocosm {
    private static final Cc<String, HMature> CC_MATURE = Cc.openThread();

    // Cloud Connected
    public static JsonObject envPlot(final JsonObject plot) {
        final KVarSet set = KVarSet.of()
            .save(KName.CLOUD, HMacrocosm.AEON_CLOUD)                                  // AEON_CLOUD
            .save(KName.CHILD, HMacrocosm.AEON_APP)                                    // AEON_APP
            .save(KName.NAME, HMacrocosm.Z_APP)                                        // Z_APP
            .save(KName.NAMESPACE, HMacrocosm.Z_NS)                                    // Z_NS
            .saveWith(KName.LANGUAGE, HMacrocosm.Z_LANG, KWeb.ARGS.V_LANGUAGE)   // Z_LANG
            .save(KName.SIGMA, HMacrocosm.Z_SIGMA);                                    // Z_SIGMA
        // 创建拷贝
        final JsonObject plotJ = Ut.valueJObject(plot, true);
        final HMature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        return mature.configure(plotJ, set);
    }

    // Restful Connected ( Multi Support )
    public static JsonObject envApi(final JsonObject api, final Integer index) {
        final KVarSet set = envServer(HMacrocosm.API_HOST, HMacrocosm.API_PORT, index);
        // 创建拷贝
        final JsonObject apiJ = Ut.valueJObject(api, true);
        final HMature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
        return mature.configure(apiJ, set);
    }

    // Socket Connected ( Multi Support )
    public static JsonObject envSock(final JsonObject sock, final Integer index) {
        final KVarSet set = envServer(HMacrocosm.SOCK_HOST, HMacrocosm.SOCK_PORT, index);
        // 创建拷贝
        final JsonObject sockJ = Ut.valueJObject(sock, true);
        final HMature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
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
        final KVarSet set;
        if (EmDS.Stored.WORKFLOW == mode) {
            // Workflow
            set = envDatabase(HMacrocosm.DBW_HOST, HMacrocosm.DBW_PORT, HMacrocosm.DBW_INSTANCE);
        } else if (EmDS.Stored.HISTORY == mode) {
            // History
            set = envDatabase(HMacrocosm.DBH_HOST, HMacrocosm.DBH_PORT, HMacrocosm.DBH_INSTANCE);
        } else {
            // Database
            set = envDatabase(HMacrocosm.DBS_HOST, HMacrocosm.DBS_PORT, HMacrocosm.DBS_INSTANCE);
        }
        // 创建拷贝
        final JsonObject databaseJ = Ut.valueJObject(database, true);
        final HMature mature = CC_MATURE.pick(MatureEnv::new, MatureEnv.class.getName());
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
        final String domain = Ut.env(HMacrocosm.CORS_DOMAIN);
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

    private static KVarSet envDatabase(final String host, final String port, final String instance) {
        return KVarSet.of()
            .save(KName.HOSTNAME, host)
            .save(KName.PORT, port, Integer.class)
            .save(KName.INSTANCE, instance);
    }

    private static KVarSet envServer(final String host, final String port, final Integer index) {
        final String envHost;
        final String envPort;
        if (Objects.isNull(index) || VValue.IDX == index) {
            envHost = host;
            envPort = port;
        } else {
            // HOST1, HOST2
            // PORT1, PORT2
            envHost = HMacrocosm.API_HOST + index;
            envPort = HMacrocosm.API_PORT + index;
        }
        return KVarSet.of()
            .saveWith(KName.HOST, envHost, KWeb.DEPLOY.HOST)       // Z_API_HOSTX
            .save(KName.PORT, envPort, Integer.class);                     // Z_API_PORTX
    }
}

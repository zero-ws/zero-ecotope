package io.zerows.extension.module.mbseapi.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.job.JobClient;
import io.zerows.cosmic.plugins.job.JobClientActor;
import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.extension.module.mbseapi.common.JtKey;
import io.zerows.extension.module.mbseapi.domain.tables.pojos.IService;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/*
 *  Job kit here for configuration
 */
public class JobKit {
    private static JobClient client() {
        try {
            return JobClientActor.ofClient();
        } catch (final Throwable ex) {
            return null;
        }
    }

    /*
     * Could not use old code here
     *
     * private static final List<Mission> MISSION_LIST = JobPool.findRunning();
     */
    static Future<JsonArray> fetchMission(final Set<String> codes) {
        final JobClient client = client();
        if (Objects.isNull(client)) {
            return Ux.futureA();
        } else {
            return client.fetchAsync(codes).compose(missionList -> {
                final JsonArray response = new JsonArray();
                missionList.forEach(item -> response.add(JobKit.toJson(item)));
                return Ux.future(response);
            });
        }
    }

    static Future<JsonArray> fetchMission() {
        final JobClient client = client();
        if (Objects.isNull(client)) {
            return Ux.futureA();
        } else {
            return client.fetchAsync().compose(missionList -> {
                final JsonArray response = new JsonArray();
                missionList.forEach(item -> response.add(JobKit.toJson(item)));
                return Ux.future(response);
            });
        }
    }

    static JsonArray filter(final JsonArray missions, final JsonObject criteria) {
        if (Objects.isNull(missions) || Ut.isNil(criteria)) {
            return Objects.isNull(missions) ? new JsonArray() : missions;
        }

        final JsonArray filtered = new JsonArray();
        Ut.itJArray(missions).forEach(item -> {
            if (matches(item, criteria)) {
                filtered.add(item);
            }
        });
        return filtered;
    }

    static JsonArray merge(final JsonArray... arrays) {
        final JsonArray merged = new JsonArray();
        final Set<String> codes = new LinkedHashSet<>();
        for (final JsonArray array : arrays) {
            if (Objects.isNull(array)) {
                continue;
            }
            Ut.itJArray(array).forEach(item -> {
                final String code = item.getString(KName.CODE);
                if (Ut.isNil(code) || codes.add(code)) {
                    merged.add(item);
                }
            });
        }
        return merged;
    }

    private static boolean matches(final JsonObject item, final JsonObject criteria) {
        final Set<Boolean> matched = new HashSet<>();
        Ut.<Object>itJObject(criteria, (value, fieldExpr) -> {
            if (Ut.isNil(fieldExpr) || Objects.equals(KName.SIGMA, fieldExpr)) {
                return;
            }
            if (fieldExpr.startsWith(VString.DOLLAR)) {
                if (value instanceof final JsonObject child) {
                    matched.add(matches(item, child));
                }
                return;
            }
            matched.add(matchesField(item, fieldExpr, value));
        });
        return matched.stream().allMatch(Boolean::booleanValue);
    }

    private static boolean matchesField(final JsonObject item, final String fieldExpr, final Object expected) {
        final String[] segments = fieldExpr.split(VString.COMMA, 2);
        final String field = segments[0];
        if (Ut.isNil(field)) {
            return true;
        }
        final Object actual = item.getValue(field);
        if (Objects.isNull(actual)) {
            return false;
        }

        final String operator = segments.length > 1 ? segments[1] : "=";
        if ("i".equals(operator) && expected instanceof final JsonArray expectedArray) {
            return expectedArray.contains(actual);
        }
        if ("=".equals(operator)) {
            return Objects.equals(actual, expected);
        }
        return Objects.equals(actual, expected);
    }

    static Future<JsonObject> fetchMission(final String code) {
        final JobClient client = client();
        if (Objects.isNull(client)) {
            return Ux.futureJ();
        } else {
            return client.fetchAsync(code).compose(found -> {
                if (Objects.isNull(found)) {
                    return Ux.future(new JsonObject());
                } else {
                    return Ux.future(toJson(found));
                }
            });
        }
    }

    public static IService fromJson(final JsonObject serviceJson) {
        //        Ke.mountString(serviceJson, KName.METADATA);
        //        Ke.mountString(serviceJson, KName.RULE_UNIQUE);
        //
        //        Ke.mountString(serviceJson, KName.Api.CONFIG_INTEGRATION);
        //        Ke.mountString(serviceJson, KName.Api.CONFIG_DATABASE);
        //
        //        Ke.mountString(serviceJson, KName.Api.CHANNEL_CONFIG);
        //        Ke.mountString(serviceJson, KName.Api.SERVICE_CONFIG);
        //        Ke.mountString(serviceJson, KName.Api.MAPPING_CONFIG);
        //        Ke.mountString(serviceJson, KName.Api.DICT_EPSILON);
        //        Ke.mountString(serviceJson, KName.Api.DICT_CONFIG);
        Ut.valueToString(serviceJson,
            KName.METADATA,
            KName.RULE_UNIQUE,
            /*
             * Zero standard configuration
             * 1) KIntegration
             * 2) Database
             * Here should be configuration for `Database` & `KIntegration`
             */
            KName.Api.CONFIG_INTEGRATION,
            KName.Api.CONFIG_DATABASE,
            /*
             * 1) channelConfig - Channel Component configuration
             * 2) serviceConfig - Service Component configuration
             * 3) dictConfig = Dict Component configuration
             * 4) mappingConfig = Mapping Component configuration
             */
            KName.Api.CHANNEL_CONFIG,
            KName.Api.SERVICE_CONFIG,
            KName.Api.MAPPING_CONFIG,
            KName.Api.DICT_EPSILON,
            KName.Api.DICT_CONFIG
        );
        return Ux.fromJson(serviceJson, IService.class);
    }

    public static JsonObject toJson(final Mission mission) {
        final JsonObject serialized = Ut.serializeJson(mission);
        final JsonObject metadata;
        if (serialized.containsKey(KName.METADATA)) {
            metadata = serialized.getJsonObject(KName.METADATA);
        } else {
            metadata = new JsonObject();
            serialized.put(KName.METADATA, metadata);
        }
        serialized.put(KName.TYPE, Objects.nonNull(mission.getType()) ? mission.getType().name() : null);
        serialized.put(KName.READ_ONLY, mission.isReadOnly());
        serialized.put("editable", !mission.isReadOnly());
        serialized.put("jobSource", mission.isReadOnly() ? "CODE" : "CONFIG");
        JobKit.ensureJob(metadata, mission);
        if (Ut.isNotNil(metadata)) {
            final JsonObject service = metadata.getJsonObject(KName.SERVICE);
            if (Ut.isNotNil(service)) {
                Ut.valueToJObject(service,
                    KName.METADATA,
                    KName.RULE_UNIQUE,
                    /*
                     * Zero standard configuration
                     * 1) KIntegration
                     * 2) Database
                     * Here should be configuration for `Database` & `KIntegration`
                     */
                    KName.Api.CONFIG_INTEGRATION,
                    KName.Api.CONFIG_DATABASE,
                    /*
                     * 1) channelConfig - Channel Component configuration
                     * 2) serviceConfig - Service Component configuration
                     * 3) dictConfig = Dict Component configuration
                     * 4) mappingConfig = Mapping Component configuration
                     */
                    KName.Api.CHANNEL_CONFIG,
                    KName.Api.SERVICE_CONFIG,
                    KName.Api.MAPPING_CONFIG,
                    KName.Api.DICT_EPSILON,
                    KName.Api.DICT_CONFIG
                );
                /*
                Ke.mount(service, KName.METADATA);
                Ke.mount(service, KName.RULE_UNIQUE);


                Ke.mount(service, KName.Api.CONFIG_INTEGRATION);
                Ke.mount(service, KName.Api.CONFIG_DATABASE);

                Ke.mount(service, KName.Api.CHANNEL_CONFIG);
                Ke.mount(service, KName.Api.SERVICE_CONFIG);
                Ke.mount(service, KName.Api.MAPPING_CONFIG);
                Ke.mount(service, KName.Api.DICT_EPSILON);
                Ke.mountArray(service, KName.Api.DICT_CONFIG);
                */
            }
        }
        return serialized;
    }

    private static void ensureJob(final JsonObject metadata, final Mission mission) {
        if (Ut.isNil(metadata.getJsonObject(JtKey.Delivery.JOB))) {
            final JsonObject job = new JsonObject();
            job.put(KName.KEY, mission.getCode());
            job.put(KName.CODE, mission.getCode());
            job.put(KName.NAME, mission.getName());
            job.put(KName.TYPE, Objects.nonNull(mission.getType()) ? mission.getType().name() : null);
            job.put(KName.COMMENT, mission.getComment());
            job.put(KName.READ_ONLY, mission.isReadOnly());
            job.put("editable", !mission.isReadOnly());
            job.put("jobSource", mission.isReadOnly() ? "CODE" : "CONFIG");
            if (Ut.isNotNil(mission.getIncomeAddress())) {
                job.put("incomeAddress", mission.getIncomeAddress());
            }
            if (Objects.nonNull(mission.getIncome())) {
                job.put("incomeComponent", mission.getIncome().getName());
            }
            if (Ut.isNotNil(mission.getOutcomeAddress())) {
                job.put("outcomeAddress", mission.getOutcomeAddress());
            }
            if (Objects.nonNull(mission.getOutcome())) {
                job.put("outcomeComponent", mission.getOutcome().getName());
            }
            if (Objects.nonNull(mission.getProxy())) {
                job.put("proxy", mission.getProxy().getClass().getName());
            }
            metadata.put(JtKey.Delivery.JOB, job);
        } else {
            final JsonObject job = metadata.getJsonObject(JtKey.Delivery.JOB);
            job.put(KName.TYPE, Objects.nonNull(mission.getType()) ? mission.getType().name() : null);
            job.put(KName.READ_ONLY, mission.isReadOnly());
            job.put("editable", !mission.isReadOnly());
            job.put("jobSource", mission.isReadOnly() ? "CODE" : "CONFIG");
        }
    }
}

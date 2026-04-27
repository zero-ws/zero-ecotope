package io.zerows.extension.module.mbseapi.serviceimpl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JobKitTest {

    @Test
    void shouldFilterRuntimeMissionsByTypeInCriteria() {
        final JsonArray missions = new JsonArray()
            .add(new JsonObject().put("code", "job-formula").put("type", "FORMULA"))
            .add(new JsonObject().put("code", "job-fixed").put("type", "FIXED"));
        final JsonObject criteria = new JsonObject()
            .put("type,i", new JsonArray().add("FORMULA"))
            .put("", true)
            .put("sigma", "ignored-for-runtime");

        final JsonArray filtered = JobKit.filter(missions, criteria);

        Assertions.assertEquals(1, filtered.size());
        Assertions.assertEquals("job-formula", filtered.getJsonObject(0).getString("code"));
    }

    @Test
    void shouldKeepRuntimeMissionsWhenCriteriaIsEmpty() {
        final JsonArray missions = new JsonArray()
            .add(new JsonObject().put("code", "job-formula").put("type", "FORMULA"))
            .add(new JsonObject().put("code", "job-fixed").put("type", "FIXED"));

        final JsonArray filtered = JobKit.filter(missions, new JsonObject());

        Assertions.assertEquals(2, filtered.size());
    }
}

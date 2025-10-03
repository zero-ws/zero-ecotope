package io.vertx.up.unity;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.typed.UArray;
import io.zerows.epoch.metadata.typed.UObject;
import io.zerows.epoch.testsuite.EpicBase;
import org.junit.Assert;
import org.junit.Test;

public class D10046FirstTc extends EpicBase {
    @Test
    public void testInput() {
        final JsonObject input = this.ioJObject("d10046.json");
        // Uson usage
        final JsonObject ret = UObject.create(input)
            .convert("password", "updated").to();
        System.err.println(ret.encodePrettily());
        Assert.assertEquals("111111", ret.getString("updated"));
    }

    @Test
    public void testInputArr() {
        final JsonArray input = this.ioJArray("d10046-arr.json");
        // Uson usage
        final JsonArray ret = UArray.create(input)
            .convert("password", "updated").to();
        System.err.println(ret.encodePrettily());
        Assert.assertEquals("111111", ret.getJsonObject(0).getString("updated"));
        Assert.assertEquals("222222", ret.getJsonObject(1).getString("updated"));
    }
}

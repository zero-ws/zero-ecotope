package io.vertx.up.commune;

import io.vertx.core.json.JsonObject;
import io.zerows.core.testing.EpicBase;
import io.zerows.core.web.mbse.atom.runner.ActIn;
import io.zerows.core.web.model.commune.Envelop;
import org.junit.Test;

public class ActInTc extends EpicBase {

    @Test
    public void testJson() {
        final JsonObject data = this.ioJObject("request.json");
        final Envelop envelop = Envelop.success(data);

        final ActIn request = new ActIn(envelop);
        final JsonObject actual = request.getJObject();
        System.err.println(actual.encodePrettily());
    }
}

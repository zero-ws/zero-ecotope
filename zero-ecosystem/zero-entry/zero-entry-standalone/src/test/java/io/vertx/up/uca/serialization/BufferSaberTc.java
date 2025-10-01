package io.vertx.up.uca.serialization;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.zerows.epoch.component.serialization.ZeroType;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.testsuite.ZeroBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BufferSaberTc extends ZeroBase {
    @Test
    public void testContext(final TestContext context) {
        final Buffer buffer = this.ioBuffer("data");
        final JsonObject data = new JsonObject()
            .put("k", ZeroType.valueSupport(buffer))
            .put("b", buffer);
        // System.out.println(data.getJsonObject("b").getBinary("bytes"));

        final Envelop envelop = Envelop.success(buffer);
        Assert.assertNotNull(envelop.outBuffer());
    }
}

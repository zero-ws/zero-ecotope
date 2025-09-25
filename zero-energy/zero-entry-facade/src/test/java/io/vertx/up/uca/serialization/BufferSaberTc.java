package io.vertx.up.uca.serialization;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.zerows.core.testing.ZeroBase;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.module.domain.uca.serialization.ZeroType;
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

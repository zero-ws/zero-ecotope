package io.vertx.up.unity;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.core.testing.ZeroBase;
import io.zerows.core.util.Ut;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public class IrAndTc extends ZeroBase {

    @Test
    public void testQr() {
        final JsonObject left = this.ioJObject("qr-left.json");
        final JsonObject right = this.ioJObject("qr-right.json");

        final JsonObject combine = Ut.irAndH(left, right);
        final JsonObject expected = left.copy().mergeIn(right);
        Assert.assertFalse(combine.equals(expected));
        expected.put(VString.EMPTY, Boolean.TRUE);
        Assert.assertTrue(combine.equals(expected));
    }
}

package io.vertx.up.unity;

import io.vertx.core.json.JsonObject;
import io.zerows.constant.VString;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.testsuite.ZeroBase;
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

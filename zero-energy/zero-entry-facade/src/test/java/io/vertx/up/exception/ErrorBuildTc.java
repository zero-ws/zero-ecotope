package io.vertx.up.exception;

import io.zerows.core.exception.WebException;
import io.zerows.core.exception.web._403ForbiddenException;
import org.junit.Assert;
import org.junit.Test;

public class ErrorBuildTc {

    @Test
    public void testError() {
        final WebException failure = new _403ForbiddenException(this.getClass());
        Assert.assertNotNull(failure);
    }
}

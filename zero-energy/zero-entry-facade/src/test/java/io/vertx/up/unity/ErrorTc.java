package io.vertx.up.unity;

import io.zerows.core.exception.WebException;
import io.zerows.core.exception.web._500InternalServerException;
import org.junit.Assert;

public class ErrorTc {

    public void buildError() {
        final WebException error =
            new _500InternalServerException(this.getClass(), "Error Internal");
        System.out.println(error);
        Assert.assertNotNull(error);
    }
}

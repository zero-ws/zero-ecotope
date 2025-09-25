package io.zerows.extension.runtime.ambient.osgi.spi.environment;

import io.vertx.core.Future;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.ams.constant.VValue;
import io.zerows.core.spi.modeler.Indent;
import io.zerows.extension.runtime.ambient.agent.service.DatumService;
import io.zerows.extension.runtime.ambient.agent.service.DatumStub;
import io.zerows.extension.runtime.skeleton.exception._400SigmaMissingException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class NormIndent implements Indent {

    private static final DatumStub stub = Ut.singleton(DatumService.class);

    @Override
    public Future<String> indent(final String code, final String sigma) {
        if (Ut.isNil(sigma)) {
            return Ut.Bnd.failOut(_400SigmaMissingException.class, this.getClass());
        }
        return stub.numberSigma(sigma, code, 1).compose(item -> {
            if (item.isEmpty()) {
                return Ux.future(null);
            } else {
                return Ux.future(item.getString(VValue.IDX));
            }
        });
    }

    @Override
    public Future<Boolean> reset(final String code, final String sigma, final Long defaultValue) {
        if (Ut.isNil(sigma)) {
            return Ut.Bnd.failOut(_400SigmaMissingException.class, this.getClass());
        }
        return stub.numberSigmaR(sigma, code, defaultValue);
    }

    @Override
    @SuppressWarnings("all")
    public Future<Queue<String>> indent(final String code, final String sigma, final int size) {
        if (Ut.isNil(sigma)) {
            return Ut.Bnd.failOut(_400SigmaMissingException.class, getClass());
        }
        return stub.numberSigma(sigma, code, size).compose(item -> {
            if (item.isEmpty()) {
                return Ux.future(new ConcurrentLinkedQueue<>());
            } else {
                return Ux.future(new ConcurrentLinkedQueue<>(item.getList()));
            }
        });
    }
}

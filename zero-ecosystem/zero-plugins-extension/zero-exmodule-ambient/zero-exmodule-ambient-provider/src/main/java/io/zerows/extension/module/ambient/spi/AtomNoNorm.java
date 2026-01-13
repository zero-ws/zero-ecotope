package io.zerows.extension.module.ambient.spi;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.zerows.extension.module.ambient.serviceimpl.DatumService;
import io.zerows.extension.module.ambient.servicespec.DatumStub;
import io.zerows.extension.skeleton.exception._60045Exception400SigmaMissing;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.spi.modeler.AtomNo;
import io.zerows.support.Ut;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AtomNoNorm implements AtomNo {

    private static final DatumStub stub = Ut.singleton(DatumService.class);

    @Override
    public Future<String> indent(final String code, final String sigma) {
        if (Ut.isNil(sigma)) {
            return FnVertx.failOut(_60045Exception400SigmaMissing.class);
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
            return FnVertx.failOut(_60045Exception400SigmaMissing.class);
        }
        return stub.numberSigmaR(sigma, code, defaultValue);
    }

    @Override
    @SuppressWarnings("all")
    public Future<Queue<String>> indent(final String code, final String sigma, final int size) {
        if (Ut.isNil(sigma)) {
            return FnVertx.failOut(_60045Exception400SigmaMissing.class);
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

package io.zerows.epoch.bootplus.extension.uca.code;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.ambient.agent.service.DatumService;
import io.zerows.extension.runtime.ambient.agent.service.DatumStub;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
abstract class AbstractSeq<T> implements Seq<T> {

    private final transient DatumStub stub = Ut.singleton(DatumService.class);
    private final transient String sigma;
    private final transient JsonObject options = new JsonObject();

    AbstractSeq(final String sigma) {
        this.sigma = sigma;
    }

    @Override
    public Seq<T> bind(final JsonObject options) {
        this.options.mergeIn(options, true);
        return this;
    }

    // ---------------------- Sub-Class Api -------------------
    protected DatumStub stub() {
        return this.stub;
    }

    protected String sigma() {
        return this.sigma;
    }

    protected JsonObject options() {
        return this.options;
    }

    // ---------------------- Response Building -------------------
    protected Future<String> single(final JsonArray numbers) {
        Objects.requireNonNull(numbers);
        final Object result = numbers.getList().get(VValue.IDX);
        return Ux.future(Objects.isNull(result) ? VString.EMPTY : result.toString());
    }

    @SuppressWarnings("all")
    protected Future<Queue<String>> batch(final JsonArray numbers) {
        return Ux.future(new ConcurrentLinkedQueue<>(numbers.getList()));
    }
}

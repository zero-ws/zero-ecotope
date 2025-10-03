package io.zerows.extension.runtime.crud.uca.op;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.corpus.database.jooq.operation.UxJooq;
import io.zerows.epoch.corpus.metadata.specification.KField;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.extension.runtime.crud.bootstrap.IxPin;
import io.zerows.extension.runtime.crud.eon.em.QrType;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;
import io.zerows.extension.runtime.crud.uca.input.Pre;
import io.zerows.extension.runtime.crud.util.Ix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class StepImport implements Agonic {
    @Override
    public Future<JsonArray> runAAsync(final JsonArray input, final IxMod in) {
        final KField fieldConfig = in.module().getField();
        final JsonArray matrix = Ix.onMatrix(fieldConfig);
        /*
         * Header And Compress
         */
        final UxJooq jooq = IxPin.jooq(in);
        return Ix.pass(input, in,
            Pre.head()::inAAsync,        /* Header Value */
            Pre.serial()::inAAsync,      /* Serial/Number */
            this::runCompress            /* Compress */
        ).compose(processed -> Pre.qr(QrType.BY_UK).inAJAsync(processed, in)
            .compose(jooq::fetchJAsync)
            /* Compared Data */
            .compose(original -> Ux.future(Ux.compareJ(original, processed, matrix)))
        ).compose(compared -> this.runSave(compared, in));
    }

    private Future<JsonArray> runSave(final ConcurrentMap<ChangeFlag, JsonArray> compared,
                                      final IxMod in) {
        final List<Future<JsonArray>> combine = new ArrayList<>();
        final JsonArray inserted = compared.getOrDefault(ChangeFlag.ADD, new JsonArray());
        if (!inserted.isEmpty()) {
            // 「AOP」Internal Call to Trigger
            combine.add(Agonic.write(ChangeFlag.ADD).runAAsync(inserted, in));
        }
        final JsonArray updated = compared.getOrDefault(ChangeFlag.UPDATE, new JsonArray());
        if (!updated.isEmpty()) {
            // 「AOP」Internal Call to Trigger
            combine.add(Agonic.write(ChangeFlag.UPDATE).runAAsync(updated, in));
        }
        return Fx.compressA(combine);
    }

    private Future<JsonArray> runCompress(final JsonArray source, final IxMod in) {
        final KField fieldConfig = in.module().getField();
        final JsonArray matrix = fieldConfig.getUnique();   // Here the `key` should be ignored
        final JsonArray normalized = Ux.ruleJReduce(source, matrix);
        return Ux.future(normalized);
    }
}

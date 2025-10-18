package io.zerows.extension.mbse.basement.uca.acc;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.OldDatabase;
import io.zerows.epoch.metadata.Apt;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.domain.tables.daos.MAccDao;
import io.zerows.extension.mbse.basement.domain.tables.pojos.MAcc;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.platform.enums.modeling.EmAttribute;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.program.Ux;
import io.zerows.specification.app.HArk;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class RiseRapid implements Rise {
    private transient OldDatabase oldDatabase;

    @Override
    public Rise bind(final OldDatabase oldDatabase) {
        this.oldDatabase = oldDatabase;
        return this;
    }

    @Override
    public Future<Apt> fetchBatch(final JsonObject criteria, final DataAtom atom) {
        return FnBase.combineT(
            this.inputData(criteria, atom),
            this.inputAcc(criteria, atom),
            (data, acc) -> this.combineAcc(data, acc, atom)
        );
    }

    private Future<Apt> combineAcc(final JsonArray data, final JsonArray acc, final DataAtom atom) {
        final Apt apt = Apt.create(acc, data);
        final ConcurrentMap<ChangeFlag, JsonArray> compared = Ao.diffPure(acc, data, atom, atom.marker().disabled(EmAttribute.Marker.syncOut));
        return apt.comparedAsync(compared);
    }

    private Future<JsonArray> inputData(final JsonObject criteria, final DataAtom atom) {
        final HDao dao = Ao.toDao(atom, this.oldDatabase);
        return dao.fetchAsync(criteria)
            .compose(records -> Ux.future(Ut.toJArray(records)));
    }

    @Override
    public Future<Boolean> writeData(final String key, final JsonArray data, final DataAtom atom) {
        return this.fetchAcc(key, atom).compose(queried -> {
            if (Objects.isNull(queried)) {
                // Add
                final MAcc acc = new MAcc();
                acc.setKey(UUID.randomUUID().toString());
                acc.setModelId(atom.identifier());
                acc.setModelKey(key);

                acc.setRecordJson(data.encode());
                acc.setActive(Boolean.TRUE);

                final HArk ark = atom.ark();
                acc.setLanguage(ark.language());
                acc.setSigma(ark.sigma());

                acc.setCreatedAt(LocalDateTime.now());
                acc.setUpdatedAt(LocalDateTime.now());
                return Ux.Jooq.on(MAccDao.class).insertAsync(acc);
            } else {
                // Update
                queried.setRecordJson(data.encode());
                queried.setUpdatedAt(LocalDateTime.now());
                return Ux.Jooq.on(MAccDao.class).updateAsync(queried);
            }
        }).compose(nil -> Ux.future(Boolean.TRUE));
    }

    private Future<JsonArray> inputAcc(final JsonObject criteria, final DataAtom atom) {
        final String modelKey = Ut.keyAtom(atom, criteria);
        return this.fetchAcc(modelKey, atom).compose(acc -> {
            if (Objects.isNull(acc)) {
                return Ux.futureA();
            } else {
                final JsonArray data = Ut.toJArray(acc.getRecordJson());
                return Ux.future(data);
            }
        });
    }

    private Future<MAcc> fetchAcc(final String modelKey, final DataAtom atom) {
        final JsonObject condition = new JsonObject();
        condition.put(KName.MODEL_KEY, modelKey);
        final HArk ark = atom.ark();
        condition.put(KName.SIGMA, ark.sigma());
        return Ux.Jooq.on(MAccDao.class).fetchOneAsync(condition);
    }
}

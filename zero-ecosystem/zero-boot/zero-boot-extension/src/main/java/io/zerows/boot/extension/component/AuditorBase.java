package io.zerows.boot.extension.component;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.support.Ut;

import java.time.Instant;
import java.util.UUID;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AuditorBase implements Auditor {
    protected transient final JsonObject options = new JsonObject();
    protected transient DataAtom atom;

    public AuditorBase(final JsonObject options) {
        if (Ut.isNotNil(options)) {
            this.options.mergeIn(options, true);
        }
    }

    @Override
    public Auditor bind(final DataAtom atom) {
        this.atom = atom;
        return this;
    }

    protected JsonObject initialize(final JsonObject record) {
        final JsonObject data = new JsonObject();
        // 模型信息填充
        final Model model = this.atom.model();
        data.put(KName.KEY, UUID.randomUUID().toString());
        data.put(KName.MODEL_ID, model.identifier());
        final String modelKey = Ao.toKey(record, this.atom);
        data.put(KName.MODEL_KEY, modelKey);

        // 附加信息
        data.put(KName.ACTIVE, Boolean.TRUE);
        data.put(KName.SIGMA, this.atom.ark().sigma());
        data.put(KName.LANGUAGE, model.dbModel().getLanguage());
        final Instant now = Instant.now();
        data.put(KName.CREATED_AT, now);
        data.put(KName.UPDATED_AT, now);
        data.put(KName.CREATED_BY, record.getValue(KName.CREATED_BY));
        data.put(KName.UPDATED_BY, record.getValue(KName.UPDATED_BY));
        return data;
    }
}

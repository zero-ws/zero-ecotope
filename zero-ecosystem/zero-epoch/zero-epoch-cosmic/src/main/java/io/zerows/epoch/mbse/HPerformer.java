package io.zerows.epoch.mbse;

import io.vertx.core.Future;
import io.zerows.specification.modeling.HModel;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HPerformer<T extends HModel> {
    /* 「Read Model」Async */
    default Future<T> fetchAsync(final String identifier) {
        return Ut.future(this.fetch(identifier));
    }

    /* 「Read Model」Sync */
    T fetch(String identifier);
}

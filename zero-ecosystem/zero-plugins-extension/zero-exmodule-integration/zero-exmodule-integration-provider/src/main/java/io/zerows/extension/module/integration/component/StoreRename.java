package io.zerows.extension.module.integration.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.integration.domain.tables.pojos.IDirectory;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class StoreRename implements StoreUp {
    @Override
    public Future<IDirectory> migrate(final IDirectory directory, final JsonObject directoryJ) {
        final String storePath = directoryJ.getString(KName.STORE_PATH);
        final Fs fs = Ut.instance(directory.getRunComponent());
        return fs.rename(directory.getStorePath(), storePath)
            .compose(renamed -> Ux.future(directory));
    }
}

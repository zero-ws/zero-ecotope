package io.zerows.extension.mbse.action.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.mbse.metadata.KCredential;
import io.zerows.extension.skeleton.spi.ScCredential;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;

import java.util.Objects;

public class ExAmbientCredit implements ScCredential {
    @Override
    public Future<KCredential> fetchAsync(final String sigma) {
        final HArk ark = Ke.ark(sigma);
        final KCredential idc = new KCredential();
        if (Objects.nonNull(ark)) {
            final HApp app = ark.app();
            final JsonObject credential = new JsonObject();
            credential.put(KName.SIGMA, sigma);
            credential.put(KName.APP_ID, app.option(KName.KEY));
            credential.put(KName.REALM, app.name());
            credential.put(KName.LANGUAGE, app.option(KName.LANGUAGE));
            credential.put(KName.GRANT_TYPE, "authorization_code");
            idc.fromJson(credential);
        }
        return Ux.future(idc);
    }
}

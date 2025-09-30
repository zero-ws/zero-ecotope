package io.zerows.extension.mbse.ui.osgi.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.ui.agent.service.FieldService;
import io.zerows.extension.mbse.ui.agent.service.FormService;
import io.zerows.extension.mbse.ui.agent.service.FormStub;
import io.zerows.extension.runtime.skeleton.osgi.spi.ui.Form;
import io.zerows.unity.Ux;

import static io.zerows.extension.mbse.ui.util.Ui.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class UiFormFetcher implements Form {
    private final static Annal LOGGER = Annal.get(UiFormFetcher.class);

    @Override
    public Future<JsonObject> fetchUi(final JsonObject params) {
        final Boolean dynamic = params.getBoolean(KName.DYNAMIC, Boolean.FALSE);
        final String code = params.getString(KName.CODE);
        LOG.Ui.info(LOGGER, "( Form ) parameters: {0}", params.encode());
        if (dynamic) {
            final FormStub formStub = Ut.singleton(FormService.class);
            Ut.field(formStub, "fieldStub", Ut.singleton(FieldService.class));
            final String sigma = params.getString(KName.SIGMA);
            return formStub.fetchByCode(code, sigma);
        } else {
            final JsonObject formData = Ut.ioJObject(code);
            return Ux.future(formData);
        }
    }
}

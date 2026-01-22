package io.zerows.extension.crud.uca.input;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.crud.common.Ix;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class PreQrKeyView implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        final Kv<String, HttpMethod> impactUri = Ix.onImpact(in);
        final String sessionKey = Ke.keyView(
            impactUri.value().name(),
            impactUri.key(),
            KView.smart(data.getValue(KName.VIEW))
        );
        log.info("{} 更新缓存键 `{}` 中的列过滤信息！", IxConstant.K_PREFIX, sessionKey);
        data.put(KName.DATA_KEY, sessionKey);
        return Ux.future(data);
    }
}

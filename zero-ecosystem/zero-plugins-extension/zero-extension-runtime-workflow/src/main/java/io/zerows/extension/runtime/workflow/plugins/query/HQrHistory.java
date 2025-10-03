package io.zerows.extension.runtime.workflow.plugins.query;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExUser;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.specification.vital.HQR;
import io.zerows.support.Ut;

/**
 * 本组专用查询组件
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HQrHistory implements HQR {
    /*
     * openGroup 包含本组
     */
    @Override
    public Future<JsonObject> compile(final JsonObject data, final JsonObject qr) {
        final String userKey = Ut.valueString(data, KName.USER);
        // 默认条件:  openGroup is null
        final JsonObject defaultQr = Ux.whereAnd()
            .put("openGroup,n", VString.EMPTY);

        return Ux.channel(ExUser.class, () -> defaultQr, stub -> stub.userGroup(userKey).compose(groups -> {
            // groups information
            if (groups.isEmpty()) {
                return Ux.future(defaultQr);
            }
            final JsonObject combineQr = new JsonObject();
            combineQr.put("$DFT$", defaultQr);
            // openGroup
            combineQr.put("openGroup,i", groups);

            return Ux.future(combineQr);
        }));
    }
}

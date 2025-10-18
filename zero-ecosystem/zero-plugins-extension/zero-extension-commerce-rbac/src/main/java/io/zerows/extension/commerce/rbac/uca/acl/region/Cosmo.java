package io.zerows.extension.commerce.rbac.uca.acl.region;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.Envelop;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 * For Data Region enhancement module for `visitant` extension
 */
public interface Cosmo {
    /*
     * When before happened join data region
     * The cosmo should process join Envelop
     */
    Future<Envelop> before(Envelop request, JsonObject matrix);

    /*
     * When after happened join data region
     * The cosmo should process join Envelop based join visitant
     */
    Future<Envelop> after(Envelop response, JsonObject matrix);
}

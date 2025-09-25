package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.core.running.HMacrocosm;
import io.zerows.extension.commerce.documentation.eon.Addr;
import org.primeframework.jwt.Signer;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;

import java.util.Map;

/**
 * @author lang : 2023-09-15
 */
@Queue
public class TokenActor {

    @Address(Addr.TOKEN_REQUEST)
    public Future<JsonObject> tokenRequest(final JsonObject config) {
        final String secret = Ut.envWith(HMacrocosm.DOC_SECRET, null);
        if (Ut.isNil(secret)) {
            return Ux.futureJ();
        }

        final JsonObject response = new JsonObject();
        /*
         * https://api.onlyoffice.com/editors/signature/
         * 如何使用 Java 对配置进行编码生成 token
         */
        try {
            final Signer signer = HMACSigner.newSHA256Signer(secret);
            final JWT jwt = new JWT();
            final Map<String, Object> map = config.getMap();
            for (final String field : map.keySet()) {
                jwt.addClaim(field, map.get(field));
            }
            final String encoded = JWT.getEncoder().encode(jwt, signer);
            response.put(KName.ACCESS_TOKEN, encoded);
        } catch (final Throwable ex) {
            response.put(KName.ERROR, ex.getMessage());
        }
        return Future.succeededFuture(response);
    }
}

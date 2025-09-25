package io.zerows.extension.commerce.rbac.agent.service.login.pre;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * @author lang : 2024-09-16
 */
public interface ImageStub {

    Future<Buffer> generate(String session);

    Future<Boolean> verify(String session, String imageCode);
}

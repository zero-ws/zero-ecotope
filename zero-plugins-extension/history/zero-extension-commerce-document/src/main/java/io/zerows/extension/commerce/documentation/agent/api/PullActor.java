package io.zerows.extension.commerce.documentation.agent.api;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.documentation.eon.Addr;
import io.zerows.extension.runtime.skeleton.osgi.spi.environment.Permit;
import io.zerows.extension.runtime.skeleton.osgi.spi.feature.Attachment;

/**
 * @author lang : 2023-09-15
 */
@Queue
public class PullActor {

    @Address(Addr.DOC_DOWNLOAD)
    public Future<Buffer> download(final JsonObject data) {
        final String fileKey = Ut.valueString(data, KName.KEY);
        final String token = Ut.valueString(data, KName.TOKEN);
        return this.verifyAsync(token).compose(verified -> {
            if (!verified) {
                /*
                 * 验证没通过，直接返回空，此处的验证包括：
                 * 1. token 必须存在（有值）
                 * 2. 数据库中存储的 token 必须是合法的，401/403
                 */
                return Ux.future(Buffer.buffer());
            }
            /*
             * 验证通过，返回文件内容（调用下载接口）
             */
            return Ux.channel(Attachment.class, Buffer::buffer,
                attachment -> attachment.downloadAsync(fileKey));
        });
    }

    // 验证 token
    private Future<Boolean> verifyAsync(final String token) {
        return Ux.channel(Permit.class, () -> Boolean.FALSE, permit -> permit.token(token));
    }
}

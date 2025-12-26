package io.zerows.plugins.integration.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;
import io.zerows.plugins.integration.sms.exception._20004Exception424MessageSend;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Defer
public class SmsClientImpl implements SmsClient {

    private transient final Vertx vertxRef;
    private transient SmsConfig config;
    private transient IAcsClient client;

    SmsClientImpl(final Vertx vertx, final SmsConfig config) {
        this.vertxRef = vertx;
        this.config = config;
        final IClientProfile profile = DefaultProfile.getProfile(SmsConfig.DFT_REGION,
                this.config.getAccessId(), this.config.getAccessSecret());
        this.client = new DefaultAcsClient(profile);
    }


    @Override
    public SmsClient send(final String mobile, final String tplCode, final JsonObject params,
                          final Handler<AsyncResult<JsonObject>> handler) {
        final SendSmsRequest request = this.getRequest(mobile, this.config.getTpl(tplCode), params);
        handler.handle(this.getResponse(request));
        return this;
    }

    private Future<JsonObject> getResponse(final SendSmsRequest request) {
        try {
            final SendSmsResponse response = this.client.getAcsResponse(request);
            final JsonObject data = new JsonObject();
            data.put(SmsConfig.RESPONSE_REQUEST_ID, response.getRequestId());
            data.put(SmsConfig.RESPONSE_BUSINESS_ID, response.getBizId());
            data.put(SmsConfig.RESPONSE_CODE, response.getCode());
            data.put(SmsConfig.RESPONSE_MESSAGE, response.getMessage());
            this.logger().info("Remove response, code = {}, message = {}",
                    response.getCode(), response.getMessage());
            return Future.succeededFuture(data);
        } catch (final ClientException ex) {
            this.logger().fatal(ex);
            return FnVertx.failOut(_20004Exception424MessageSend.class, ex);
        }
    }

    private SendSmsRequest getRequest(final String mobile, final String tplCode, final JsonObject params) {
        final SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(mobile);
        request.setSignName(this.config.getSignName());
        request.setTemplateCode(tplCode);
        request.setTemplateParam(params.encode());
        return request;
    }
}
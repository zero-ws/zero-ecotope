package io.zerows.plugins.security.weco;

import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.r2mo.xync.weco.wechat.WeChatType;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

import java.util.Objects;

@Queue
@Slf4j
public class ApiWeMpActor {
    @Inject
    private WeChatStub weChatStub;

    @Address(ApiAddr.API_AUTH_WEMP_QRCODE)
    public Future<JsonObject> qrCode(final JsonObject required) {
        return this.weChatStub.getQrCode();
    }


    @Address(ApiAddr.API_AUTH_WEMP_STATUS)
    public Future<JsonObject> checkStatus(final JsonObject params) {
        final String uuid = Ut.valueString(params, "uuid");
        return this.weChatStub.checkStatus(uuid);
    }


    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_GET)
    public Future<String> callbackGet(final JsonObject agentJ) {
        final String signature = Ut.valueString(agentJ, "signature");
        final String timestamp = Ut.valueString(agentJ, "timestamp");
        final String nonce = Ut.valueString(agentJ, "nonce");
        final String echostr = Ut.valueString(agentJ, "echostr");
        // 参数准备
        final WeArgsSignature params = WeArgsSignature.builder()
            .signature(signature)
            .timestamp(timestamp)
            .nonce(nonce)
            .build();

        // 签名检查
        return this.weChatStub.checkEcho(params).compose(checked -> {
            if (checked) {
                log.info("[ R2MO ] 签名检查通过：{}", echostr);
                return Future.succeededFuture(echostr);
            }
            return Future.succeededFuture("");
        });
    }


    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_POST)
    public Future<String> callbackPost(final JsonObject agentJ) {
        final String requestBody = Ut.valueString(agentJ, "body");
        final String signature = Ut.valueString(agentJ, "signature");
        final String timestamp = Ut.valueString(agentJ, "timestamp");
        final String nonce = Ut.valueString(agentJ, "nonce");
        final String encType = Ut.valueString(agentJ, "encrypt_type");
        final String msgSignature = Ut.valueString(agentJ, "msg_signature");
        // 1. 解析 XML
        final WxMpXmlMessage message = WxMpXmlMessage.fromXml(requestBody);
        final String event = message.getEvent(); // SUBSCRIBE 或 SCAN
        final String eventKey = message.getEventKey(); // 可能带 qrscene_ 前缀
        final String openid = message.getFromUser();

        // 2. 提取 UUID
        String uuid = null;
        if ("subscribe".equalsIgnoreCase(event)) {
            // 关注事件：Key 是 "qrscene_UUID"
            if (eventKey != null && eventKey.startsWith("qrscene_")) {
                uuid = eventKey.replace("qrscene_", "");
            }
        } else if ("SCAN".equalsIgnoreCase(event)) {
            // 已关注扫码：Key 就是 "UUID"
            uuid = eventKey;
        }

        // 3. 只有提取了 UUID 才能把人和浏览器对上号
        if (Objects.nonNull(uuid)) {
            // 参数准备
            final WeArgsCallback params = WeArgsCallback.builder()
                .signature(signature)
                .timestamp(timestamp)
                .nonce(nonce)
                .openid(openid)
                .msgSignature(msgSignature)
                .encType(encType)
                .type(WeChatType.MP)
                .build();

            // 构造登录专用请求
            return this.weChatStub.extract(uuid, params).compose(response -> {
                final String id = response.getString("id");
                final String token = response.getString("token");
                log.info("[ ZERO ] ( WeChat ) 用户关注/扫描成功，Token 已就绪，ID = {}, Token = {}", id, token);
                return Future.succeededFuture("success");
            });
        }
        return Future.succeededFuture();
    }
}

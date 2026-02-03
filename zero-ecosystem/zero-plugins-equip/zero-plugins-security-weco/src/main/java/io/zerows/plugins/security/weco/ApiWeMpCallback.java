package io.zerows.plugins.security.weco;

import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public class ApiWeMpCallback {

    @Inject
    private WeChatService weChatService;

    @GET
    @Path("/auth/wechat-callback")
    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_GET)
    @Operation(
        summary = DescAuth._auth_wechat_callback_get_summary,
        description = DescAuth._auth_wechat_callback_get_desc,
        parameters = {
            @Parameter(name = "signature", description = DescAuth.P.signature, in = ParameterIn.QUERY, required = true, example = "450537d97c55..."),
            @Parameter(name = "timestamp", description = DescAuth.P.timestamp, in = ParameterIn.QUERY, required = true, example = "1678888888"),
            @Parameter(name = "nonce", description = DescAuth.P.nonce, in = ParameterIn.QUERY, required = true, example = "123456789"),
            @Parameter(name = "echostr", description = DescAuth.P.echostr, in = ParameterIn.QUERY, required = true, example = "592472342342")
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_string,
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN, // 👈 强制指定为纯文本
                    schema = @Schema(type = "string", example = "592472342342")
                )
            )
        }
    )
    public JsonObject callbackGet(@QueryParam("signature") final String signature,
                                  @QueryParam("timestamp") final String timestamp,
                                  @QueryParam("nonce") final String nonce,
                                  @QueryParam("echostr") final String echostr) {
        final JsonObject exchangeJ = new JsonObject();
        exchangeJ.put("signature", signature);
        exchangeJ.put("timestamp", timestamp);
        exchangeJ.put("nonce", nonce);
        exchangeJ.put("echostr", echostr);
        return exchangeJ;
    }

    /**
     * 接收微信回调事件 (扫码、关注等)
     * <p>POST /auth/wechat-callback</p>
     * 注意：
     * 1. 微信发送的是 XML，不能用 JObject 接收
     * 2. 必须返回 "success" 纯文本，不能返回 JSON
     */
    @POST
    @Path("/auth/wechat-callback")
    @Address(ApiAddr.API_AUTH_WEMP_CALLBACK_POST)
    @Operation(
        summary = DescAuth._auth_wechat_callback_post_summary,
        description = DescAuth._auth_wechat_callback_post_desc,
        // 定义 Query 参数 (安全签名用)
        parameters = {
            @Parameter(name = "signature", description = DescAuth.P.signature, in = ParameterIn.QUERY, example = "450537d9..."),
            @Parameter(name = "timestamp", description = DescAuth.P.timestamp, in = ParameterIn.QUERY, example = "1678888888"),
            @Parameter(name = "nonce", description = DescAuth.P.nonce, in = ParameterIn.QUERY, example = "123456"),
            @Parameter(name = "encrypt_type", description = DescAuth.P.encrypt_type, in = ParameterIn.QUERY, example = "aes"),
            @Parameter(name = "msg_signature", description = DescAuth.P.msg_signature, in = ParameterIn.QUERY, example = "ab123...")
        },
        // 定义 Body：微信推过来的是 XML，不是 JSON
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.TEXT_XML, // 👈 声明输入为 XML
                schema = @Schema(type = "string", format = "xml",
                    example = "<xml><ToUserName><![CDATA[gh_abcdef]]></ToUserName><FromUserName><![CDATA[oABCD]]></FromUserName><CreateTime>123456789</CreateTime><MsgType><![CDATA[event]]></MsgType><Event><![CDATA[subscribe]]></Event></xml>"
                )
            )
        ),
        // 定义 Response：返回 success 纯文本
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_v_success,
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN, // 👈 声明输出为纯文本
                    schema = @Schema(type = "string", example = "success")
                )
            )
        }
    )
    public JsonObject callbackPost(@BodyParam final String body,
                                   @QueryParam("signature") final String signature,
                                   @QueryParam("timestamp") final String timestamp,
                                   @QueryParam("nonce") final String nonce,
                                   @QueryParam("encrypt_type") final String encType,
                                   @QueryParam("msg_signature") final String msgSignature
    ) {
        final JsonObject exchangeJ = new JsonObject();
        exchangeJ.put("body", body);
        exchangeJ.put("signature", signature);
        exchangeJ.put("timestamp", timestamp);
        exchangeJ.put("nonce", nonce);
        exchangeJ.put("encrypt_type", encType);
        exchangeJ.put("msg_signature", msgSignature);
        return exchangeJ;
    }
}

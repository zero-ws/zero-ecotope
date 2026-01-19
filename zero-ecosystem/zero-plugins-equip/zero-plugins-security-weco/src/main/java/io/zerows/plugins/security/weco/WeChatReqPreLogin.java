package io.zerows.plugins.security.weco;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatReqPreLogin extends WeChatReqAccount {
    /**
     * 临时授权码 (前端传递)
     */
    private String code;
    // --- 构造函数 ---

    public WeChatReqPreLogin() {
    }

    public WeChatReqPreLogin(final JsonObject request) {
        super(request);
        // 临时授权码
        this.setCode(request.getString("code"));
    }

    // --- Setter 联动逻辑 ---

    public void setCode(final String code) {
        this.code = code;
        // Code 即临时凭证
        this.setCredential(code);
    }
}

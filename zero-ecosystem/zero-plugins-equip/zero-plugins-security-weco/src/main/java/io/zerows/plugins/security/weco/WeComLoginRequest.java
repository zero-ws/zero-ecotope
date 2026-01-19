package io.zerows.plugins.security.weco;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeComLoginRequest extends LoginRequest {
    /**
     * 临时授权码 (前端传递)
     */
    private String code;
    private String state;

    /**
     * 企业成员 UserID (后端换取后填充)
     */
    private String userId;

    // --- 构造函数 ---

    public WeComLoginRequest() {
    }

    public WeComLoginRequest(final JsonObject request) {
        this.setCode(request.getString("code"));
        this.state = Ut.valueString(request, "state");
    }

    // --- Setter 联动逻辑 ---

    public void setCode(final String code) {
        this.code = code;
        // Code 即临时凭证
        this.setCredential(code);
    }

    public void setUserId(final String userId) {
        this.userId = userId;
        // UserID 即用户身份标识
        this.setId(userId);
    }

    // --- 核心方法 ---

    @Override
    public TypeLogin type() {
        return TypeLogin.ID_WECOM;
    }
}

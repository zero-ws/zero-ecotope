package io.zerows.plugins.security.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatReqAccount extends LoginRequest {

    /**
     * 用户唯一标识 (后端换取后填充)
     */
    private String openId;

    /**
     * 开放平台统一标识 (后端换取后填充，可选)
     */
    private String unionId;

    public WeChatReqAccount() {
    }

    public WeChatReqAccount(final JsonObject request) {
        this.unionId = Ut.valueString(request, "unionId");
        this.openId = Ut.valueString(request, "openId");
        if (StrUtil.isEmpty(this.unionId)) {
            // Union Id 优先
            this.setId(this.openId);
        } else {
            // Open Id 次之
            this.setId(this.unionId);
        }
    }

    public void setUnionId(final String unionId) {
        this.unionId = unionId;
        // OpenID 即用户身份标识
        this.setId(unionId);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.ID_WECHAT;
    }
}

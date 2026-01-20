package io.zerows.plugins.oauth2.metadata;

import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * OAuth2 客户端凭证元数据
 * 承载从认证路径（Header/Body）中解析出的身份信息
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
@NoArgsConstructor
@Accessors(chain = true) // 开启链式调用，让 setter 返回 this
public class OAuth2Credential implements Serializable {

    private String clientId;
    private String clientSecret;

    // 是否通过 Header (Basic Auth) 认证
    // 注意：Lombok 对 boolean isX 会生成 setX() 方法
    private boolean basic = false;

    public OAuth2Credential(final String clientId, final String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // 手动补一个 isBasic(boolean) 适配你的 Extractor 逻辑
    public OAuth2Credential isBasic(final boolean basic) {
        this.basic = basic;
        return this;
    }

    // --- 逻辑校验 ---

    /**
     * 基础验证：至少要有 ClientId
     */
    public boolean isValid() {
        return Ut.isNotNil(this.clientId);
    }

    /**
     * 严格验证：ClientId 和 ClientSecret 都不能为空
     */
    public boolean isConfidential() {
        return Ut.isNotNil(this.clientId) && Ut.isNotNil(this.clientSecret);
    }

    @Override
    public String toString() {
        return OAuth2Constant.K_PREFIX + " Credential: clientId = " + this.clientId
            + ", isBasic = " + this.basic;
    }
}
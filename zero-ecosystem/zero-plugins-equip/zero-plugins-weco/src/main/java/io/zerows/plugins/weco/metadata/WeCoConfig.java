package io.zerows.plugins.weco.metadata;

import io.r2mo.base.exchange.NormProxy;
import io.r2mo.xync.weco.wechat.WeChatCredential;
import io.r2mo.xync.weco.wecom.WeComCredential;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信体系统一配置映射
 * <pre>
 *   weco:
 *     # ==========================================
 *     # 1. 全局网络代理 (可选)
 *     # ==========================================
 *     proxy:
 *       host:
 *       port:
 *       username:
 *       password:
 *
 *     # ==========================================
 *     # 2. 微信公众号 (WeChat Official Account)
 *     # ==========================================
 *     wechat:
 *       app-id:
 *       secret:
 *       token:      可选
 *       aes-key:    可选
 *       proxy:      # 可选，独立代理配置，优先级高于全局 proxy
 *
 *     # ==========================================
 *     # 3. 企业微信 (WeCom / Work WeChat)
 *     # ==========================================
 *     wecom:
 *       corp-id:
 *       secret:
 *       agent-id:
 *       proxy:      # 可选，独立代理配置，优先级高于全局 proxy
 * </pre>
 *
 * @author lang : 2025-12-09
 */
@Data
public class WeCoConfig implements Serializable {

    /**
     * 全局代理配置 (可选)
     * 如果子模块（wechat/wecom）未配置独立代理，则默认使用此配置
     */
    private NormProxy proxy;

    /**
     * 微信公众号配置域
     */
    private WeChatMp wechatMp;

    /**
     * 微信开放平台配置域
     */
    private WeChatOpen wechatOpen;

    /**
     * 企业微信配置域
     */
    private WeComCp wecomCp;

    public boolean isWeChatMp() {
        return this.wechatMp != null;
    }

    public boolean isWeChatOpen() {
        return this.wechatOpen != null;
    }

    public boolean isWeComCp() {
        return this.wecomCp != null;
    }


    // --- 内部静态配置类 ---

    /**
     * 微信公众号 (WeChat MP) 配置
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class WeChatMp extends WeCoApp {
        /* 回调验证的 Token */
        private String token;
        /* 回调加密 Key（可选）*/
        private String aesKey;
        /* 二维码过期时间 */
        private Integer expireSeconds = 300;

        /**
         * 快捷转换为底层凭证对象
         */
        public WeChatCredential credential() {
            return new WeChatCredential()
                .appId(this.getAppId())
                .secret(this.getSecret())
                .token(this.token)
                .aesKey(this.aesKey);
        }
    }

    /**
     * 微信开放平台 (WeChat Open Platform) 配置
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class WeChatOpen extends WeCoApp {
        /**
         * 网站应用回调地址
         **/
        private String redirectUri;

        public WeChatCredential credential() {
            return new WeChatCredential()
                .appId(this.getAppId())
                .secret(this.getSecret());
        }
    }

    /**
     * 企微登录 (WeCom) 配置
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Slf4j
    public static class WeComCp extends WeCoApp implements Serializable {
        /**
         * 企业ID
         */
        private String corpId;
        /**
         * 应用ID
         */
        private Integer agentId;
        private String token;
        private String aesKey;
        private Integer expireSeconds = 300;
        /**
         * 企微回调地址
         **/
        private String urlCallback;
        /**
         * 黑名单地址
         **/
        private List<String> blockDomains = new ArrayList<>();

        /**
         * 快捷转换为底层凭证对象
         */
        public WeComCredential credential() {
            return new WeComCredential()
                .corpId(this.corpId)
                .secret(this.getSecret())
                .agentId(this.agentId)
                .token(this.token)
                .aesKey(this.aesKey);
        }

        @Override
        public String getAppId() {
            // log.warn("[ R2MO ] 企微配置中的 getAppId() 方法已被废弃，请使用 corpId 字段代替。");
            return this.corpId;
        }

        @Override
        public void setAppId(final String appId) {
            // log.warn("[ R2MO ] 企微配置中的 setAppId() 方法已被废弃，请使用 corpId 字段代替。");
            this.corpId = appId;
        }
    }
}
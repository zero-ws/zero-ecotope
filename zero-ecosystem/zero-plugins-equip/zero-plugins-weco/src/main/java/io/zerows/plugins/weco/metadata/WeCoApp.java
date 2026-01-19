package io.zerows.plugins.weco.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.base.exchange.NormProxy;
import lombok.Data;

import java.io.Serializable;

@Data
public abstract class WeCoApp implements Serializable {
    @JsonProperty("app-id")
    protected String appId;
    private String secret;
    /**
     * 独立代理（优先级高于全局代理）
     **/
    private NormProxy proxy;
}

package io.zerows.extension.module.finance.common.em;

/**
 * PKG-FM-102
 *
 * @author lang : 2023-11-27
 */
public final class EmPay {
    private EmPay() {
    }

    /*
     * 支付模式，对应 OBJECT_TYPE
     * - DELAY：延迟支付
     * - AT：立即支付
     */
    public enum Type {
        DELAY,      // 延迟支付（对应 DEBT 付款/退款）
        AT,         // 现结

        DEBT,       // 直接转应收
    }
}

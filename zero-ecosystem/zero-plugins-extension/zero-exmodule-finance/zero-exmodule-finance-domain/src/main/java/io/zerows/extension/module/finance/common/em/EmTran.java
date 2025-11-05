package io.zerows.extension.module.finance.common.em;

/**
 * @author lang : 2024-01-24
 */
public final class EmTran {
    public enum Type {
        SETTLEMENT, // 针对结算的交易
        DEBT,       // 针对应收的交易
        REFUND,     // 针对退款的交易
    }

    public enum Status {
        FINISHED,   // 已完成的交易
        CANCELLED,  // 已作废的交易
    }
}

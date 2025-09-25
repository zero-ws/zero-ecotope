package io.zerows.extension.commerce.finance.eon;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface FmConstant {
    String BUNDLE_SYMBOLIC_NAME = "zero-extension-commerce-finance";
    
    String[] SEQ = new String[]{"A", "B", "C", "D", "E", "F", "G"};

    interface Status {
        String PENDING = "Pending";

        String FINISHED = "Finished";
        String INVALID = "InValid";

        String FIXED = "Fixed";
        String VALID = "Valid";
    }

    interface Type {
        String TRANSFER_FROM = "TransferFrom";
        String CANCEL = "Cancel";
    }

    interface ID {
        String SETTLEMENT_ID = "settlementId";
        String DEBT_ID = "debtId";
        String PAYMENT = "payment";
    }

    interface NUM {
        String DEBT = "NUM.DEBT";
        String REFUND = "NUM.REFUND";
        String TRANS = "NUM.TRANS";
    }
}

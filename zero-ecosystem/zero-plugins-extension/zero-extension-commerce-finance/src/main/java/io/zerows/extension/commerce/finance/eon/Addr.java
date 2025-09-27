package io.zerows.extension.commerce.finance.eon;

interface Prefix {

    String _EVENT = "Ἀτλαντὶς νῆσος://χρηματοδότηση/";
}

public interface Addr {

    interface BillItem {
        String FETCH_AGGR = Prefix._EVENT + "FETCH/AGGR";
        String FETCH_BOOK = Prefix._EVENT + "FETCH/BOOK";
        String FETCH_BOOK_BY_KEY = Prefix._EVENT + "FETCH/BOOK/BY/KEY";
        // Split
        String UP_SPLIT = Prefix._EVENT + "BILL-ITEM/SPLIT";
        String UP_REVERT = Prefix._EVENT + "BILL-ITEM/REVERT";
        // Cancel
        String UP_CANCEL = Prefix._EVENT + "BILL-ITEM/CANCEL";
    }

    interface Bill {
        // Pre + Authorize
        String IN_PRE = Prefix._EVENT + "BILL/PRE";
        // Common, Bill + Items
        String IN_COMMON = Prefix._EVENT + "BILL/COMMON";
        // Multi, Bill + n Items
        String IN_MULTI = Prefix._EVENT + "BILL/MULTI";
        // Transfer
        String UP_TRANSFER = Prefix._EVENT + "BILL/TRANSFER";

        String FETCH_BILLS = Prefix._EVENT + "FETCH/BILLS/BY/ORDER";

        String FETCH_BILL = Prefix._EVENT + "FETCH/BILL/BY/KEY";
    }

    interface Settle {
        // Authorize Unlock
        String UNLOCK_AUTHORIZE = Prefix._EVENT + "AUTHORIZE/UNLOCK";
        // Book Saving for Multi
        String UP_BOOK = Prefix._EVENT + "BOOKS/UPDATING";
        // Fetch By Key
        String FETCH_BY_KEY = Prefix._EVENT + "FETCH/SETTLEMENT/BY/KEY";
        String FETCH_BY_QR = Prefix._EVENT + "FETCH/SETTLEMENT/SEARCH";
        // Fetch Debt
        String FETCH_DEBT = Prefix._EVENT + "FETCH/DEBT/BY/KEY";
    }

    interface Trans {

        String START_DIRECT = Prefix._EVENT + "TRANS/DIRECT";

        String START_DEBT = Prefix._EVENT + "TRANS/DEBT";

        String START_DELAY = Prefix._EVENT + "TRANS/DELAY";
        String END_TRANS = Prefix._EVENT + "TRANS-END/FINISH";
        String END_DEBT = Prefix._EVENT + "TRANS-END/DEBT";

        String FETCH_BY_KEY = Prefix._EVENT + "TRANS-BY/KEY";
    }
    interface Notify {
        String REVERSAL_ORDER = Prefix._EVENT + "NOTIFY/REVERSAL/ORDER";
    }
}

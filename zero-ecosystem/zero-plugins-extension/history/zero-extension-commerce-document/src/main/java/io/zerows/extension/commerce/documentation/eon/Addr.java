package io.zerows.extension.commerce.documentation.eon;

/**
 * 申请 ONLYOFFICE 令牌
 *
 * @author lang : 2023-09-14
 */
interface Prefix {

    String _EVENT = "Δομή εγγράφου://περιβάλλων/";
}

public interface Addr {

    String TOKEN_REQUEST = Prefix._EVENT + "DC-TOKEN/REQUEST";

    String DOC_DOWNLOAD = Prefix._EVENT + "DC-PUB/DOWNLOAD";

    interface Clause {
        String SAVE = Prefix._EVENT + "DC-CLAUSE/SAVE";

        String BY_DOC = Prefix._EVENT + "DC-CLAUSE/GET/BY-DOC";
    }

    interface Comment {
        String BY_MODEL = Prefix._EVENT + "DC-COMMENT/BY/MODEL";
    }
}

package io.zerows.extension.module.report.api;

interface Prefix {

    String _EVENT = "Ἀτλαντὶς νῆσος://Έντυπο αναφοράς/";
}

/**
 * @author lang : 2024-10-08
 */
interface Addr {

    interface Report {
        String QUERY_ALL = Prefix._EVENT + "KP-REPORT/QUERY-ALL";

        String QUERY_PAGE = Prefix._EVENT + "KP-REPORT/QUERY-PAGE";

        String SINGLE_GENERATE = Prefix._EVENT + "KP-REPORT/SINGLE-GENERATE";

        String SINGLE_EXPORT = Prefix._EVENT + "KP-REPORT/SINGLE-EXPORT";

        String SINGLE_SAVE = Prefix._EVENT + "KP-REPORT/SINGLE-SAVE";

        String SINGLE_DELETE = Prefix._EVENT + "KP-REPORT/SINGLE-DELETE";

        String SINGLE_FETCH = Prefix._EVENT + "KP-REPORT/SINGLE_FETCH";
    }
}

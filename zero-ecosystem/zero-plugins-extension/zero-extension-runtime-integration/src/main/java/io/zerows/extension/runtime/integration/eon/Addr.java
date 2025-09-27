package io.zerows.extension.runtime.integration.eon;

interface Prefix {

    String _EVENT = "Ἀτλαντὶς νῆσος://Ολοκλήρωση/";
}

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Addr {

    interface Directory {

        String ADD = Prefix._EVENT + "I-DIRECTORY/ADD";

        String UPDATE = Prefix._EVENT + "I-DIRECTORY/UPDATE";

        String DELETE = Prefix._EVENT + "I-DIRECTORY/DELETE";

        String DELETE_TRASH = Prefix._EVENT + "I-DIRECTORY/DELETE-TRASH";
    }

    interface Message {

        String UPDATE_STATUS = Prefix._EVENT + "I-MESSAGE/UPDATE/STATUS";

        String FETCH_TYPED = Prefix._EVENT + "I-MESSAGE/FETCH/BY/TYPED";

        String ADD = Prefix._EVENT + "I-MESSAGE/ADD";

        String DELETE_BATCH = Prefix._EVENT + "I-MESSAGE/BATCH/DELETE";
    }
}

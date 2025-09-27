package io.zerows.extension.mbse.modulat.eon;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
interface Prefix {

    String _EVENT = "Ἀτλαντὶς νῆσος://μορφάτης/";
}

public interface Addr {

    interface Module {

        String FETCH = Prefix._EVENT + "X-BAG/FETCH-ALL";

        String BY_EXTENSION = Prefix._EVENT + "X-BAG/FETCH/BY-EXTENSION";

        String UP_PROCESS = Prefix._EVENT + "X-BAG/PROCESS";

        String UP_AUTHORIZE = Prefix._EVENT + "X-BAG/AUTHORIZE";
    }

    interface Argument {

        String BAG_ARGUMENT = Prefix._EVENT + "X-BAG/ARGUMENT/FETCH";

        String BAG_ARGUMENT_VALUE = Prefix._EVENT + "X-BAG/ARGUMENT/FETCH/VALUE";

        String BAG_CONFIGURE = Prefix._EVENT + "X-BAG/CONFIGURE/SYNC";

        String BLOCK_ARGUMENT = Prefix._EVENT + "X-BLOCK/ARGUMENT/FETCH";

        String BLOCK_ARGUMENT_VALUE = Prefix._EVENT + "X-BLOCK/ARGUMENT/FETCH/VALUE";

        String BLOCK_CONFIGURE = Prefix._EVENT + "X-BLOCK/CONFIGURE/SYNC";
    }

    interface Notify {
        String BLOCK_CONFIGURE_UP = Prefix._EVENT + "NOTIFY/X-BLOCK/CONFIGURE/UP";
    }
}

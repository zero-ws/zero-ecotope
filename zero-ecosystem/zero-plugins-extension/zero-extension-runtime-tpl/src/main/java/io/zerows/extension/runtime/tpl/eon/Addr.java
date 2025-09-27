package io.zerows.extension.runtime.tpl.eon;

interface Prefix {

    String _EVENT = "Ἀτλαντὶς νῆσος://Πρότυπο/";
}

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Addr {
    interface Menu {

        String MY_FETCH = Prefix._EVENT + "X-MENU/MY/FETCH";

        String MY_SAVE = Prefix._EVENT + "X-MENU/MY/SAVE";
    }

    interface Notify {
        String MY_FETCH = Prefix._EVENT + "I-MESSAGE/MY/FETCH";

        String MY_SAVE = Prefix._EVENT + "I-MESSAGE/MY/SAVE";
    }

    interface App {
        String MY_FETCH = Prefix._EVENT + "X-APP/MY/FETCH";

        String MY_SAVE = Prefix._EVENT + "X-APP/MY/SAVE";
    }
}

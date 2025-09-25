package io.zerows.extension.mbse.basement.uca.sql;

public final class SqlDDLConstant {
    private static final String DELETE_FIELD_FLAG = "deleteBak";

    public static String getDeleteFieldFlag() {
        return DELETE_FIELD_FLAG;
    }

    public static String combineNewName(final String old) {
        return old + "_" + System.currentTimeMillis() + "_" + DELETE_FIELD_FLAG;
    }
}

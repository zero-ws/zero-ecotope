package io.zerows.extension.module.erp.api;

interface Addr {

    interface Company {
        String INFORMATION = AddrPrefix._EVENT + "X-COMPANY";
    }

    interface Employee {
        String ADD = AddrPrefix._EVENT + "E-EMPLOYEE/ADD";

        String BY_ID = AddrPrefix._EVENT + "E-EMPLOYEE/BY-ID";

        String EDIT = AddrPrefix._EVENT + "E-EMPLOYEE/EDIT";

        String DELETE = AddrPrefix._EVENT + "E-EMPLOYEE/DELETE";
    }
}

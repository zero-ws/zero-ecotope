package io.zerows.extension.module.rbac.api;

interface Addr {
    String EVENT = "Ἀτλαντὶς νῆσος://Ασφάλεια/";

    interface Rule {

        /*
         * 「New Version」
         * 1. Fetch for KPermit of new structure
         * 2. Save View based join Configuration
         */
        String FETCH_REGION = EVENT + "X-RULE/FETCH/REGION";
        String FETCH_REGION_VALUES = EVENT + "X-RULE/FETCH/REGION-VALUES";

        String FETCH_REGION_DEFINE = EVENT + "X-RULE/FETCH/REGION-DEFINE";

        String SAVE_REGION = EVENT + "X-RULE/SAVING/SINGLE";
    }

    interface Auth {
        String LOGOUT = EVENT + "O-LOGOUT";
    }

    interface User {
        String INFORMATION = EVENT + "X-INFORMATION";

        String PASSWORD = EVENT + "X-PASSWORD";

        String PROFILE = EVENT + "X-PROFILE";

        String SEARCH = EVENT + "X-SEARCH";

        String GET = EVENT + "X-USER/GET/ID";

        String ADD = EVENT + "X-USER/ADD";

        String DELETE = EVENT + "X-DELETE/USER/ID";

        String UPDATE = EVENT + "X-PUT/USER/ID";

        String IMPORT = EVENT + "X-IMPORT/USER";

        /*
         * New for user processing
         */
        String QR_USER_SEARCH = EVENT + "X-USER/QR/SEARCH";
    }

    interface Perm {
        /* Search all permissions that are not related */
        String PERMISSION_UN_READY = EVENT + "X-PERMISSION/UN-READY";

        /*
         * CRUD replaced
         */
        String BY_ID = EVENT + "X-PERMISSION/CRUD/READ";
        String ADD = EVENT + "X-PERMISSION/CRUD/CREATE";
        String EDIT = EVENT + "X-PERMISSION/CRUD/UPDATE";
        String DELETE = EVENT + "X-PERMISSION/CRUD/DELETE";
    }

    interface Authority {
        /* Api Seeking（Action Only） */
        String ACTION_SEEK = EVENT + "X-ACTION/SEEK";

        /* Api Pre-Ready */
        String ACTION_READY = EVENT + "X-ACTION/READY";

        /* Resource Search */
        String RESOURCE_SEARCH = EVENT + "X-RESOURCE/SEARCH";

        /* Perm Information */
        String PERMISSION_GROUP = EVENT + "X-PERMISSION/GROUP";
        /* Perm Saving, Save permission definition */
        String PERMISSION_DEFINITION_SAVE = EVENT + "X-PERMISSION/DEFINITION/SAVING";

        /* Get all relation between role & permission */
        String PERMISSION_BY_ROLE = EVENT + "X-PERMISSION/BY/ROLE";
        /* Save all relation between role & permission */
        String PERMISSION_SAVE = EVENT + "X-PERMISSION/SAVING";

        /* Resource findRunning with action */
        String RESOURCE_GET_CASCADE = EVENT + "X-RESOURCE/GET-CASCADE";
        /* Resource add with action */
        String RESOURCE_ADD_CASCADE = EVENT + "X-RESOURCE/ADD-CASCADE";
        /* Resource update with action */
        String RESOURCE_UPDATE_CASCADE = EVENT + "X-RESOURCE/UPDATE-CASCADE";
        /* Resource delete with action */
        String RESOURCE_DELETE_CASCADE = EVENT + "X-RESOURCE/DELETE-CASCADE";
    }

    interface View {
        /*
         * View interface publish for `my view` instead of old `my`
         */
        String VIEW_P_BY_USER = EVENT + "X-VIEW-P/GET/BY-USER";
        String VIEW_P_ADD = EVENT + "X-VIEW-P/ADD";
        String VIEW_P_DELETE = EVENT + "X-VIEW-P/DELETE";
        String VIEW_P_UPDATE = EVENT + "X-VIEW-P/UPDATE";
        String VIEW_P_BY_ID = EVENT + "X-VIEW-P/GET/BY-ID";
        String VIEW_P_BATCH_DELETE = EVENT + "X-VIEW-P/BATCH/DELETE";
        String VIEW_P_EXISTING = EVENT + "X-VIEW-P/EXISTING";
    }

    interface Group {
        String GROUP_SIGMA = EVENT + "S-GROUP/SIGMA";
    }

    interface Role {
        String ROLE_SIGMA = EVENT + "S-ROLE/SIGMA";

        String ROLE_PERM_UPDATE = EVENT + "S-ROLE-PERM/PUT";

        String ROLE_SAVE = EVENT + "S-ROLE/SAVE";
    }
}

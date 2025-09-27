package io.zerows.core.constant.configure;

import io.zerows.ams.constant.VOption;

/**
 * @author lang : 2023-05-29
 */
interface YmlJooq {
    String __KEY = "jooq";
    String ORBIT = "orbit";         // 历史库
    String PROVIDER = "provider";   // 正常库
    String WORKFLOW = "workflow";   // 工作流库

    interface orbit extends VOption.database {

    }

    interface provider extends VOption.database {

    }

    interface workflow extends VOption.database {

    }
}

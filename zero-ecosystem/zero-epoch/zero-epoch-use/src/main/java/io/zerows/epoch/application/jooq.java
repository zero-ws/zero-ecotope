package io.zerows.epoch.application;

/**
 * @author lang : 2023-05-29
 */
interface YmlJooq {
    String __KEY = "jooq";
    String ORBIT = "orbit";         // 历史库
    String PROVIDER = "provider";   // 正常库
    String WORKFLOW = "workflow";   // 工作流库

    interface orbit extends YmlOption.database {

    }

    interface provider extends YmlOption.database {

    }

    interface workflow extends YmlOption.database {

    }
}

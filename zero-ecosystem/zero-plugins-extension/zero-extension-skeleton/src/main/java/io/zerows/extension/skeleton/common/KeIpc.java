package io.zerows.extension.skeleton.common;

import io.vertx.core.json.JsonArray;

/**
 * Standard Ipc for Zero extension module
 * It's for communication
 */
public interface KeIpc {
    interface Workflow {
        /*
         * Event Addr Prefix for workflow
         * This get will be shared between zero-ambient / zero-wf
         * */
        String EVENT = "Ἀτλαντὶς νῆσος://Ροή εργασίας/";
    }

    /*
     * Rbac Ipc
     */
    interface Sc {
        /* Ipc for verify token */
        String IPC_TOKEN_VERIFY = "IPC://TOKEN/VERIFY";
        /* Ipc for access token */
        String IPC_TOKEN_ACCESS = "IPC://TOKEN/ACCESS";
    }

    interface Audit {

        JsonArray INCLUDE = new JsonArray()
            .add("/api/user")                           // zero-rbac     用户创建
            .add("/api/permission")                     // zero-rbac     权限创建
            .add("/api/authority/region/:path")         // zero-rbac     权限管理专用
            .add("/api/employee")                       // zero-erp      员工创建
            .add("/api/wh")                             // zero-psi      仓库创建
            .add("/api/i-directory")                    // zero-is       目录创建
            .add("/api/file/upload")                    // zero-ambient  文件上传
            .add("/api/my/menu/save")                   // zero-ambient  个人菜单保存
            .add("/api/up/flow")                        // zero-wf       流程处理专用
            .add("/api/linkage/sync")                   // zero-wf, zero-ambient 关联创建
            .add("/api/bill/")                          // zero-fm       账单通用接口
            .add("/api/bill-item/")                     // zero-fm       账单项通用接口
            .add("/api/trans/")                         // zero-fm       交易创建通用接口
            .add("/api/trans-proc/")                    // zero-fm       交易处理通用接口
            .add("/api/settle/")                        // zero-fm       结算通用接口
            ;

        JsonArray EXCLUDE = new JsonArray()
            .add("/api/:actor/search")                  // zero-crud    默认统一搜索接口
            .add("/api/:actor/missing")                 // zero-crud    默认统一缺失检查接口
            .add("/api/:actor/existing")                // zero-crud    默认统一存在检查接口
            .add("/api/:actor/export")                  // zero-crud    默认统一导出接口
            .add("/api/:actor/import")                  // zero-crud    默认统一导入接口
            .add("/api/up/flow-queue")                  // zero-wf      流程处理队列
            .add("/api/up/flow-history")                // zero-wf      流程历史队列
            .add("/api/user/search/:identifier")        // zero-rbac    按统一标识符查询用户信息（某一类用户查询）
            .add("/api/report/single-generate")         // zero-report  生成报表专用
            ;
    }
}

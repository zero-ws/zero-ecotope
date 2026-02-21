package io.zerows.platform.enums.modeling;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;

/**
 * @author lang : 2023-05-31
 */
public class EmModel {
    public enum Type {
        DIRECT,       // 直接模型（和数据表1对1处理，默认为DIRECT）
        VIEW,         // 视图模型（后期可以和数据库中的视图绑定）
        JOINED,       // 连接模型，和视图模型类似，但不绑定数据库中视图，直接做连接（自然连接）
        READONLY,     // 只读模型
    }

    /**
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    public enum Join {
        /**
         * CRUD, `crud` configured, the system will look up ofMain configuration by `CRUD` standard way.
         */
        CRUD,
        /**
         * DAO, `classDao` configured, directly to seek ofMain configuration
         */
        DAO,
        /**
         * DEFINE, `classDefine` configured, Reserved in future.
         */
        DEFINE,
    }

    /*
     * New Structure for different interface ( Default findRunning )
     * BY_ID / BY_KEY / BY_TENANT / BY_SIGMA
     */
    public enum By {
        BY_ID,              // APP_ID
        BY_KEY,             // APP_KEY
        BY_TENANT,          // TENANT_ID
        BY_SIGMA;           // SIGMA

        /**
         * 直接根据自身值提取查询条件
         * <pre>
         *     - APP_KEY        = ?
         *     - SIGMA          = ?
         *     - TENANT_ID      = ?
         *     - APP_ID         = ?
         * </pre>
         *
         * @param value 查询值
         * @return 查询条件 JsonObject
         */
        public JsonObject whereBy(final String value) {
            final JsonObject conditionJ = new JsonObject()
                .put("", Boolean.TRUE);

            // 直接使用 this 引用当前枚举值
            switch (this) {
                case BY_KEY -> conditionJ.put(VName.APP_KEY, value);
                case BY_SIGMA -> conditionJ.put(VName.SIGMA, value);
                case BY_TENANT -> conditionJ.put(VName.TENANT_ID, value);
                case BY_ID -> conditionJ.put(VName.APP_ID, value);
                default -> conditionJ.put(VName.APP_ID, value); // 保底逻辑
            }
            return conditionJ;
        }
    }
}

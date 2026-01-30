package io.zerows.extension.module.mbsecore.metadata;

import io.zerows.extension.module.mbsecore.domain.tables.pojos.MAttribute;
import io.zerows.extension.module.mbsecore.domain.tables.pojos.MJoin;
import io.zerows.extension.module.mbsecore.domain.tables.pojos.MModel;
import io.zerows.extension.module.mbsecore.metadata.element.DataKey;
import io.zerows.specification.modeling.HLife;
import io.zerows.specification.modeling.HModel;
import io.zerows.specification.modeling.operation.HLinkage;

import java.util.Set;

public interface Model extends HLife, HLinkage, HModel {

    /*
     * The API for database pojo directly such as
     *
     * 1. MModel
     * 2. MJoin
     * 3. MAttribute
     */
    MModel dbModel();

    Set<MJoin> dbJoins();

    Set<MAttribute> dbAttributes();

    MAttribute dbAttribute(String attributeName);

    /*
     * The Api for defined modeling interface
     * Such as
     *
     * 1. Schema
     * 2. HAttribute
     * 3. RuleUnique
     */
    Set<Schema> schema();

    Schema schema(String identifier);

    // ================== 单名空间 ====================
    /* 从Json中连接Schema：会针对joins做过滤 **/
    Model bind(Set<Schema> schemas);

    /* 从数据库中连接Schema：不考虑joins，直接连接 **/
    void bindDirect(Set<Schema> schemas);

    DataKey key();

    void key(DataKey dataKey);
}


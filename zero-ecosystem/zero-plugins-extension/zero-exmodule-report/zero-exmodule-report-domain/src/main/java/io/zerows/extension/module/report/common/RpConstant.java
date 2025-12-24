package io.zerows.extension.module.report.common;

/**
 * @author lang : 2024-07-25
 */
public interface RpConstant {

    interface SourceTypeField {
        String TABLE = "ds.table";
        String VIEW = "ds.view";
        String EXTENSION = "ds.extension";
        String SOURCE = "ds.source";
    }

    interface ConfigField {
        String COMBINE = "combine";
        String TOTAL = "total";
        String TOTAL_COUNT = "totalCount";
    }

    interface DimField {
        String KEY = "dimKey";
        String DISPLAY = "dimDisplay";
        String CHILDREN = "dimChildren";
    }

    interface DimValue {
        String FIELD_GROUP = "field.group";
    }

    interface ValuePath {
        String PREFIX_PARAM = "P:";
        String PREFIX_REFER = "R:";
        String PREFIX_CLASS = "C:";
    }
}

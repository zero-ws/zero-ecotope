package io.zerows.core.constant.em;

/**
 * @author lang : 2023-10-22
 */
public class EmPRI {
    private EmPRI() {
    }

    public enum Connect {
        PARENT_ACTIVE,      // 父表是主表
        PARENT_STANDBY,     // 附表是从表
    }
}

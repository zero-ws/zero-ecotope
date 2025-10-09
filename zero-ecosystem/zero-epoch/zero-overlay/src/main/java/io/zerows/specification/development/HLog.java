package io.zerows.specification.development;

/**
 * @author lang : 2025-10-09
 */
public interface HLog {

    @SuppressWarnings("unchecked")
    default <T> T vLog() {
        return (T) this;
    }

    default String vLog(final int indent) {
        return "";
    }
}

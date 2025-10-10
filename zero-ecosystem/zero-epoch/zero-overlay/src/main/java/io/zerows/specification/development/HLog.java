package io.zerows.specification.development;

/**
 * @author lang : 2025-10-09
 */
public interface HLog {

    @SuppressWarnings("all")
    default <T> T vLog() {
        return (T) this;
    }
}

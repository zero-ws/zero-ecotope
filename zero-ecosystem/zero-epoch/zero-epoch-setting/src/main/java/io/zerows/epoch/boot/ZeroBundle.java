package io.zerows.epoch.boot;

import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2025-10-04
 */
public class ZeroBundle implements HBundle {
    private String name;

    @Override
    public String id(final Class<?> clazz) {
        return Objects.requireNonNull(clazz).getName();
    }

    @Override
    public String name() {
        if (Objects.isNull(this.name)) {
            this.name = "Rachel Momo";
        }
        return this.name;
    }

    @Override
    public HBundle name(final String name) {
        this.name = name;
        return this;
    }
}

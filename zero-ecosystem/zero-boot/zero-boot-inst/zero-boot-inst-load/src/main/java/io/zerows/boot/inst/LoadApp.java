package io.zerows.boot.inst;

import io.zerows.epoch.annotations.Up;

/**
 * @author lang : 2025-12-18
 */
@Up
public class LoadApp {

    public static void main(final String[] args) {
        LoadInst.run(LoadApp.class, args);
    }
}

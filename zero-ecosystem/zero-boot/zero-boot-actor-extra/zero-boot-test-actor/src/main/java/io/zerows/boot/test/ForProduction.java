package io.zerows.boot.test;

import io.zerows.platform.enums.Environment;

/**
 * @author lang : 2023-06-13
 */
public class ForProduction extends PartyABase {
    public ForProduction() {
        super(Environment.Production);
    }
}

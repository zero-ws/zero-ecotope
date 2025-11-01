package io.zerows.boot.test;

import io.zerows.platform.enums.Environment;

/**
 * @author lang : 2023-06-13
 */
public class ForDevelopment extends PartyABase {
    public ForDevelopment() {
        super(Environment.Development);
    }
}

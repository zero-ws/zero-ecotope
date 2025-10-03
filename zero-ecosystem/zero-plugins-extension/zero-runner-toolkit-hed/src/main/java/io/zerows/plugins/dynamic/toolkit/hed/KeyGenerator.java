package io.zerows.plugins.dynamic.toolkit.hed;

import io.zerows.platform.metadata.KPair;
import io.zerows.epoch.program.Ut;

public class KeyGenerator {
    public static void main(final String[] args) {
        final KPair kv = Ut.randomRsa(2048);
        System.out.println("------------------------ Private Key ------------------------");
        System.out.println(kv.getPrivateKey());

        System.out.println("------------------------ Public Key ------------------------");
        System.out.println(kv.getPublicKey());
    }
}

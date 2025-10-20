package io.zerows.plugins.store.liquibase;

import io.r2mo.base.secure.EDPair;
import io.r2mo.jce.component.secure.AlgNorm;
import io.r2mo.jce.component.secure.CryptoByPrivate;
import io.r2mo.jce.component.secure.ED;

/**
 * @author lang : 2025-10-20
 */
public class LiquibaseEDCrypto extends CryptoByPrivate {

    @Override
    protected EDPair data() {
        return LiquibaseEDDefault.loadRSA();
    }

    @Override
    protected ED executor() {
        return ED.encryptOfPrivate(AlgNorm.RSA);
    }
}

package io.vertx.up.backbone.origin;

import io.vertx.quiz.example.WallKeeper2;
import io.zerows.epoch.corpus.web.security.store.WallInquirer;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.support.Ut;

import java.util.Set;

public class WallInquirerTc {

    private final Inquirer<Set<Aegis>> walls =
        Ut.singleton(WallInquirer.class);

    // @Test(expected = BootWallDuplicatedException.class)
    public void testScan() {
        final Set<Class<?>> classes = OCacheClass.entireValue();
        this.walls.scan(classes);
    }

    public void testScanCorrect() {
        final Set<Class<?>> classes = OCacheClass.entireValue();
        classes.remove(WallKeeper2.class);
        final Set<Aegis> treeResult = this.walls.scan(classes);
        for (final Aegis instance : treeResult) {
            System.out.println(instance);
        }
    }
}

package io.vertx.up.backbone.origin;

import io.vertx.quiz.example.WallKeeper2;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.web.security.store.WallInquirer;
import io.zerows.epoch.corpus.metadata.store.OCacheClass;
import io.zerows.epoch.corpus.metadata.zdk.uca.Inquirer;
import io.zerows.epoch.corpus.security.atom.Aegis;

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

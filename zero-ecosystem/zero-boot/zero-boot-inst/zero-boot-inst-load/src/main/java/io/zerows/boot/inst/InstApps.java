package io.zerows.boot.inst;

import io.r2mo.typed.cc.Cc;

import java.net.URI;
import java.util.List;

public interface InstApps {

    Cc<String, InstApps> CC_SKELETON = Cc.openThread();

    static InstApps of() {
        return CC_SKELETON.pick(InstAppsLoad::new);
    }

    List<URI> ioApp();

    List<URI> ioRunning();
}

package io.zerows.specification.development.ncloud;

import io.zerows.component.shared.boot.KPlot;
import io.zerows.component.shared.boot.KRepo;
import io.zerows.enums.EmCloud;
import io.zerows.exception.web._80413Exception501NotImplement;
import io.zerows.specification.configuration.HConfig;

import java.util.concurrent.ConcurrentMap;

/**
 * 云端启动接口，提取配置专用接口，面向底层接口规范处理，可针对配置
 * 执行核心数据提取
 * <pre><code>
 *     1. boot -> KBoot
 *     2. plot -> KPlot
 *     3. mode -> Mode
 *     4. workspace -> String
 *     5. name -> String
 *     6. repo -> ( KRepo )
 * </code></pre>
 *
 * @author lang : 2023-05-20
 */
public interface HAeon extends HConfig {

    @Override
    default <T> T get(final String field) {
        throw new _80413Exception501NotImplement();
    }

    @Override
    default HConfig put(final String field, final Object value) {
        throw new _80413Exception501NotImplement();
    }

    void boot(HStarter bootComponent);

    HStarter boot();

    KPlot plot();

    EmCloud.Mode mode();

    String workspace();

    String name();

    KRepo repo(EmCloud.Runtime runtime);

    ConcurrentMap<EmCloud.Runtime, KRepo> repo();
}

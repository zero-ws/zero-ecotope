package io.zerows.epoch.boot;

import io.r2mo.io.common.HFS;
import io.zerows.epoch.basicore.YmConfiguration;

import java.net.URL;
import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
public class ZeroSource implements ZeroPower.Source {
    private static final String FILE_BOOT = "vertx-boot.yml";
    private static final String FILE_APP = "vertx.yml";
    private final transient HFS fs = HFS.of();

    @Override
    public YmConfiguration load() {
        if (this.fs.isExist(FILE_BOOT)) {
            // 存储路径上 vertx-boot.yml 存在，则直接走 Remote 模式读取数据信息构造 YmConfiguration

        }

        // 如果路径不存在，则考虑类路径
        final URL urlPath = this.getClass().getResource("/" + FILE_BOOT);
        final String content = this.fs.inString(urlPath);
        if (!Objects.nonNull(content)) {
            /*
             * 类路径上存在 vertx-boot.yml，这种情况通常是编译过程中就已经限定了当前 App 是一个这种类型的 App，不
             * 可以直接针对此处组件进行设置
             */
        }
        return null;
    }
}

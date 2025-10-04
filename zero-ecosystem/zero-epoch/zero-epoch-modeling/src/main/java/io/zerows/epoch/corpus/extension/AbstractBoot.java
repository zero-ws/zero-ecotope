package io.zerows.epoch.corpus.extension;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.jigsaw.MDConfiguration;
import io.zerows.epoch.jigsaw.MDConnect;
import io.zerows.epoch.jigsaw.MDEntity;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractBoot implements HExtension {
    static final ConcurrentMap<String, MDConfiguration> CONFIGURATION_MAP = new ConcurrentHashMap<>();
    // 直接从 MSConfiguration 中提取
    private transient final ConcurrentMap<String, MDConnect> bridgeConnect = new ConcurrentHashMap<>();
    // 直接从 MSConfiguration 中提取
    private transient final List<String> bridgeFiles = new ArrayList<>();
    // 直接从 MSConfiguration 中提取
    private transient final ConcurrentMap<String, JsonObject> bridgeModule = new ConcurrentHashMap<>();
    // 直接从 MSConfiguration 中提取
    private transient final ConcurrentMap<String, JsonArray> bridgeColumns = new ConcurrentHashMap<>();
    private transient final MDConfiguration configuration;

    public AbstractBoot(final String module) {
        Objects.requireNonNull(module);
        // 新版直接使用配置模块来加载扩展相关操作，执行数据导入
        this.configuration = HExtension.getOrCreate(module);

        this.logger().info("Bundle: id = {} loaded files {}", module, this.configuration.inFiles().size());
    }

    protected OLog logger() {
        return Ut.Log.boot(this.getClass());
    }

    @Override
    public ConcurrentMap<String, MDConnect> connect() {
        Objects.requireNonNull(this.configuration);
        if (!this.bridgeConnect.isEmpty()) {
            return this.bridgeConnect;
        }

        // 懒加载
        final Set<MDConnect> connectList = this.configuration.inConnect();
        connectList.stream().filter(Objects::nonNull)
            .filter(connect -> Objects.nonNull(connect.getTable()))
            .forEach(connect -> this.bridgeConnect.put(connect.getTable(), connect));
        /* BPT: 断点在此处可以知道每个配置中的 MDConnect 的情况：Table = MDConnect 的监控 */
        return this.bridgeConnect;
    }

    protected Set<String> configureBuiltIn() {
        return new HashSet<>();
    }

    @Override
    public List<String> oob() {
        Objects.requireNonNull(this.configuration);
        if (!this.bridgeFiles.isEmpty()) {
            return this.bridgeFiles;
        }


        // 懒加载：OOB Files
        this.bridgeFiles.addAll(this.configuration.inFiles());
        // 懒加载：BuiltIn Files
        this.bridgeFiles.addAll(this.configureBuiltIn());

        return this.bridgeFiles;
    }

    @Override
    public List<String> oob(final String prefix) {
        Objects.requireNonNull(this.configuration);
        // Fix Issue of Null Pointer
        final String prefixFile = Objects.isNull(prefix) ? VString.EMPTY : prefix;
        return this.oob().stream()
            .filter(item -> item.contains(prefixFile))
            .collect(Collectors.toList());
    }

    @Override
    public ConcurrentMap<String, JsonObject> module() {
        Objects.requireNonNull(this.configuration);
        if (!this.bridgeModule.isEmpty()) {
            return this.bridgeModule;
        }

        // 懒加载
        final Set<MDEntity> entities = this.configuration.inEntity();
        entities.stream().filter(Objects::nonNull).filter(entity -> Objects.nonNull(entity.identifier())).forEach(entity -> {
            final String identifier = entity.identifier();
            final JsonObject module = entity.inModule();
            if (Ut.isNotNil(module)) {
                this.bridgeModule.put(identifier, module);
            }
        });
        return this.bridgeModule;
    }

    @Override
    public ConcurrentMap<String, JsonArray> column() {
        if (!this.bridgeColumns.isEmpty()) {
            return this.bridgeColumns;
        }

        // 懒加载
        final Set<MDEntity> entities = this.configuration.inEntity();
        entities.stream().filter(Objects::nonNull).filter(entity -> Objects.nonNull(entity.identifier())).forEach(entity -> {
            final String identifier = entity.identifier();
            final JsonArray columns = entity.inColumns();
            if (Ut.isNotNil(columns)) {
                this.bridgeColumns.put(identifier, columns);
            }
        });
        return this.bridgeColumns;
    }
}

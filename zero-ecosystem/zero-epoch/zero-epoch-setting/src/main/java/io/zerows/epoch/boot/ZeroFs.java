package io.zerows.epoch.boot;

import io.r2mo.function.Fn;
import io.r2mo.io.common.HFS;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.InPre;
import io.zerows.epoch.basicore.MDId;
import io.zerows.epoch.basicore.MDMod;
import io.zerows.epoch.basicore.exception._41002Exception500ConfigConflict;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * 配置分流器（底层核心组件）
 * <pre>
 *     1. 配置分流读取 {@link InPre} 前置配置
 *        {@link ZeroSource} -> {@link ZeroFs} -> 加载前置配置
 *     2. 配置分流加载模块配置
 * </pre>
 *
 * @author lang : 2025-12-15
 */
@Slf4j
public class ZeroFs {
    private static final String FILE_BOOT = "vertx-boot.yml";
    private static final String MOD_CONTAINER = "zero-exmodule::CORE";
    private static final Cc<String, ZeroFs> CC_SKELETON = Cc.open();
    // ------ 成员变量
    private final MDId id;
    private final String mid;

    private MDMod mod;
    private final transient HFS fs = HFS.of();

    private ZeroFs(final MDId id) {
        this.id = id;
        this.mid = Objects.isNull(id) ? MOD_CONTAINER : id.value();
    }

    public static ZeroFs of(final String mid) {
        return CC_SKELETON.pick(() -> new ZeroFs(MDId.of(mid)), mid);
    }

    public static ZeroFs of(final MDId mdId) {
        return of(mdId.value());
    }

    public static ZeroFs of() {
        return of(MOD_CONTAINER);
    }

    public String name() {
        final MDMod mod = this.getOrCreate();
        return Objects.isNull(mod) ? null : mod.name();
    }

    /**
     * Container 容器层的配置加载流程，这种模式下 mid 必须是 {@link #MOD_CONTAINER}，否则直接抛出配置冲突异常。
     *
     * @return 前置配置对象
     */
    public InPre inPre() {
        Fn.jvmKo(!MOD_CONTAINER.equals(this.mid), _41002Exception500ConfigConflict.class, MOD_CONTAINER, this.mid);
        final ConfigFs<InPre> fs = this.getOrCreate(FILE_BOOT, InPre.class);
        return Objects.isNull(fs) ? null : fs.refT();
    }

    public <T> ConfigFs<T> inFs(final String filename, final Class<T> clazz) {
        return this.getOrCreate(filename, clazz);
    }

    /**
     * Module 模块层配置加载流程，这种模式下直接根据 mid 进行配置计算，并且 mid 不可以是 {@link #MOD_CONTAINER}。
     *
     * @return 模块配置对象
     */
    private MDMod getOrCreate() {
        // 非模块处理
        if (MOD_CONTAINER.equals(this.mid)) {
            return null;
        }


        // 模块处理流程
        if (Objects.nonNull(this.mod)) {
            return this.mod;
        }
        // Fn.jvmKo(MOD_CONTAINER.equals(this.mid), _41002Exception500ConfigConflict.class, "NONE", MOD_CONTAINER);
        final ConfigFs<MDMod> fs = this.getOrCreate("plugins/" + this.mid + ".yml", MDMod.class);
        this.mod = Objects.isNull(fs) ? null : fs.refT();
        return this.mod;
    }

    private <T> ConfigFs<T> getOrCreate(final String filename, final Class<T> clazz) {
        final String content = this.fs.inContent(filename);
        if (Ut.isNil(content)) {
            return null;
        }

        // 根据环境变量和当前内容进行解析
        final String parsedString = Ut.compileYml(content);
        // 有内容，则直接解析之后处理
        final JObject parsed = this.fs.ymlForJ(parsedString);
        return new ConfigFs<>(parsed, clazz);
    }

    // ------------------- 模块加载文件配置 -------------------

    /**
     * 模块配置中必须使用延迟加载，而不考虑构造的时候来处理 this.mod 变量，当前类的配置分流包含两部分
     * <pre>
     *     1. 应用本身的配置分流：{@link #inPre()} 提取
     *        - 本地环境加载
     *        - 远程环境Nacos加载
     *        这个方法对模块配置没有要求，并且不会调用当前类的核心方法，属于桥接
     *     2. 模块的配置分流：{@link #mod} 提取
     *        - 模块配置必须存在
     *        - 模块配置必须正确
     *        这种场景下才对模块引用有需求，所以为了兼容二者，此处必须使用延迟加载！
     * </pre>
     *
     * @return 配置处理器
     */
    private ConfigMod loader() {
        // 非模块处理
        if (MOD_CONTAINER.equals(this.mid)) {
            return ConfigMod.of();
        }


        // 模块处理流程
        if (Objects.isNull(this.mod)) {
            final MDMod mod = this.getOrCreate();
            Objects.requireNonNull(mod, "[ ZERO ] 模块配置初始化失败，无法继续进行后续操作！");
            this.mod = mod;
        }
        return this.mod.configurer();
    }

    private String inPath(final String filename) {
        if (Objects.isNull(this.id) || MOD_CONTAINER.equals(this.id.value())) {
            // 排除容器模式下的默认加载
            return filename;
        }
        final String baseDir = this.id.path();
        return Ut.ioPath(baseDir, filename);
    }

    public JsonObject inJObject(final String filename) {
        final String filepath = this.inPath(filename);
        return this.loader().inJObject(filepath);
    }

    public JsonObject inYamlJ(final String filename) {
        final String filepath = this.inPath(filename);
        return this.loader().inYamlJ(filepath);
    }

    public JsonArray inYamlA(final String filename) {
        final String filepath = this.inPath(filename);
        return this.loader().inYamlA(filepath);
    }

    public InputStream inStream(final String filename) {
        final String filepath = this.inPath(filename);
        return this.loader().inStream(filepath);
    }

    public boolean isExist(final String filename) {
        final String filepath = this.inPath(filename);
        return this.loader().ioExist(filepath);
    }

    public List<String> inFiles(final String directory, final String suffix) {
        final String dirPath = this.inPath(directory);
        return this.loader().ioFiles(dirPath, suffix);
    }

    public List<String> inFiles(final String directory) {
        final String dirPath = this.inPath(directory);
        return this.loader().ioFiles(dirPath);
    }

    public List<String> inDirectories(final String directory) {
        final String dirPath = this.inPath(directory);
        return this.loader().ioDirectories(dirPath);
    }
}

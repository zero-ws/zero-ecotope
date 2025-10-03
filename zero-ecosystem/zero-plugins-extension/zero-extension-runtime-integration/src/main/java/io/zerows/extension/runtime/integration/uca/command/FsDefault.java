package io.zerows.extension.runtime.integration.uca.command;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.corpus.Ux;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.integration.atom.IsConfig;
import io.zerows.extension.runtime.integration.bootstrap.IsPin;
import io.zerows.extension.runtime.integration.domain.tables.pojos.IDirectory;
import io.zerows.extension.runtime.integration.eon.IsConstant;
import io.zerows.extension.runtime.integration.eon.em.EmDirectory;
import io.zerows.specification.vital.HFS;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static io.zerows.extension.runtime.integration.util.Is.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class FsDefault extends AbstractFs {

    @Override
    public IDirectory initTree(final JsonObject directoryJ) {
        /*
         * Store
         */
        final IDirectory directory = Ux.fromJson(directoryJ, IDirectory.class);
        directory.setCode(Ut.encryptMD5(directory.getStorePath()));
        return directory.setType(EmDirectory.Type.STORE.name());
    }

    @Override
    public void initTrash() {
        final String root = this.configRoot();
        final String rootTrash = Ut.ioPath(root, IsConstant.TRASH_FOLDER);
        HFS.common().mkdir(rootTrash);
    }

    @Override
    public Future<JsonArray> mkdir(final JsonArray data) {
        this.runRoot(data, HFS.common()::mkdir);
        return Ux.future(data);
    }

    @Override
    public Future<JsonObject> mkdir(final JsonObject data) {
        final String root = this.configRoot();
        final String path = data.getString(KName.STORE_PATH);
        HFS.common().mkdir(Ut.ioPath(root, path));
        return Ux.future(data);
    }


    @Override
    public Future<JsonArray> synchronize(final JsonArray data, final JsonObject config) {
        return this.initialize(data, config)
            /*
             * 新版此处需注意
             * - initialize 方法的返回值为插入数据，即新增的数据，若是因为做迁移会导致迁移的
             *   目录数据并未在 inserted 数据中，所以此处的 inserted 不可以用来执行 mkdir
             *   的指令
             * - mkdir 应该具有一定幂等性，可反复执行，而真正需要同步的目录为 data 传入数据
             */
            .compose(inserted -> this.mkdir(data));
    }

    @Override
    public Future<JsonArray> rm(final JsonArray data) {
        this.runRoot(data, HFS.common()::rm);
        return Ux.future(data);
    }

    @Override
    public Future<Boolean> rm(final Collection<String> storeSet) {
        final String root = this.configRoot();
        storeSet.forEach(path -> HFS.common().rm(Ut.ioPath(root, path)));
        return Ux.futureT();
    }

    @Override
    public Future<JsonObject> rm(final JsonObject data) {
        final String root = this.configRoot();
        final String path = data.getString(KName.STORE_PATH);
        HFS.common().rm(Ut.ioPath(root, path));
        return Ux.future(data);
    }

    @Override
    public Future<Boolean> rename(final ConcurrentMap<String, String> transfer) {
        if (!transfer.isEmpty()) {
            transfer.forEach(this::renameWith);
        }
        return Ux.futureT();
    }

    @Override
    public Future<Boolean> rename(final String from, final String to) {
        this.renameWith(from, to);
        return Ux.futureT();
    }

    @Override
    public Future<Boolean> rename(final Kv<String, String> kv) {
        return this.rename(kv.key(), kv.value());
    }

    @Override
    public Future<Boolean> upload(final ConcurrentMap<String, String> transfer) {
        if (!transfer.isEmpty()) {
            final String root = this.configRoot();
            transfer.forEach((from, to) -> {
                final String toPath = Ut.ioPath(root, to);
                HFS.common().rename(from, toPath);
            });
        }
        return Ux.futureT();
    }

    @Override
    public Future<Buffer> download(final String storePath) {
        final String root = this.configRoot();
        final String toPath = Ut.ioPath(root, storePath);
        Buffer buffer = Buffer.buffer();
        if (Ut.ioExist(toPath)) {
            buffer = Ut.ioBuffer(toPath);
        }
        return Ux.future(buffer);
    }

    @Override
    public Future<Buffer> download(final Set<String> storeSet) {
        final String root = this.configRoot();
        final Set<String> files = new HashSet<>();
        storeSet.forEach(file -> files.add(Ut.ioPath(root, file)));
        return Ux.future(Ut.toZip(files));
    }

    // ----------------- Run Private Method for Folder Calculation -----------------

    private void renameWith(final String from, final String to) {
        final String root = this.configRoot();
        final String fromPath = Ut.ioPath(root, from);
        final String toPath = Ut.ioPath(root, to);
        HFS.common().rename(fromPath, toPath);
    }

    private String configRoot() {
        final IsConfig config = IsPin.getConfig();
        final String rootPath = config.getStoreRoot();
        if (Ut.isNil(rootPath)) {
            LOG.Path.warn(this.getClass(), "The `storeRoot` of integration service is null");
        }
        return rootPath;
    }

    private void runRoot(final JsonArray data, final Consumer<Set<String>> fsRoot) {
        final String root = this.configRoot();
        final Set<String> dirSet = new HashSet<>();
        Ut.itJArray(data).forEach(json -> {
            final String path = json.getString(KName.STORE_PATH);
            dirSet.add(Ut.ioPath(root, path));
        });
        fsRoot.accept(dirSet);
    }
}

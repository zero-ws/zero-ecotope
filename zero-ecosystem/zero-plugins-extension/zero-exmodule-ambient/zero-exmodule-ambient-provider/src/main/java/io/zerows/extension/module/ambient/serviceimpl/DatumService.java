package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.component.*;
import io.zerows.extension.module.ambient.servicespec.DatumStub;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatumService implements DatumStub {

    private static final Cc<String, Aide> CC_AIDE = Cc.open();
    private static final Cc<String, Tree> CC_TREE = Cc.open();
    private static final Cc<String, SerialGen> CC_SERIAL = Cc.open();

    // ------------------------ Dict Operation Api
    @Override
    public Future<JsonArray> dictApp(final String appId, final String type) {
        final Aide aide = CC_AIDE.pick(AideApp::new, appId); // FnZero.po?l(POOL_AIDE, id, AideApp::new);
        return aide.fetch(appId, new JsonArray().add(type));
    }

    @Override
    public Future<JsonArray> dictApp(final String appId, final JsonArray types) {
        final Aide aide = CC_AIDE.pick(AideApp::new, appId); // FnZero.po?l(POOL_AIDE, id, AideApp::new);
        return aide.fetch(appId, types);
    }

    @Override
    public Future<JsonObject> dictApp(final String appId, final String type, final String code) {
        final Aide aide = CC_AIDE.pick(AideApp::new, appId); // FnZero.po?l(POOL_AIDE, id, AideApp::new);
        return aide.fetch(appId, type, code);
    }

    @Override
    public Future<JsonArray> dictSigma(final String sigma, final JsonArray types) {
        final Aide aide = CC_AIDE.pick(AideSigma::new, sigma); // FnZero.po?l(POOL_AIDE, sigma, AideSigma::new);
        return aide.fetch(sigma, types);
    }

    @Override
    public Future<JsonArray> dictSigma(final String sigma, final String type) {
        final Aide aide = CC_AIDE.pick(AideSigma::new, sigma); // FnZero.po?l(POOL_AIDE, sigma, AideSigma::new);
        return aide.fetch(sigma, new JsonArray().add(type));
    }

    @Override
    public Future<JsonObject> dictSigma(final String sigma, final String type, final String code) {
        final Aide aide = CC_AIDE.pick(AideSigma::new, sigma); // FnZero.po?l(POOL_AIDE, sigma, AideSigma::new);
        return aide.fetch(sigma, type, code);
    }

    // ------------------------ Tree Operation Api
    @Override
    public Future<JsonArray> treeApp(final String appId, final String type, final Boolean leaf) {
        final Tree tree = CC_TREE.pick(TreeApp::new, appId); // FnZero.po?l(POOL_TREE, id, TreeApp::new);
        return tree.fetch(appId, type, leaf);
    }

    @Override
    public Future<JsonArray> treeApp(final String appId, final JsonArray types) {
        final Tree tree = CC_TREE.pick(TreeApp::new, appId); // FnZero.po?l(POOL_TREE, id, TreeApp::new);
        return tree.fetch(appId, types);
    }


    @Override
    public Future<JsonObject> treeApp(final String appId, final String type, final String code) {
        final Tree tree = CC_TREE.pick(TreeApp::new, appId); // FnZero.po?l(POOL_TREE, id, TreeApp::new);
        return tree.fetch(appId, type, code);
    }

    @Override
    public Future<JsonArray> treeSigma(final String sigma, final String type, final Boolean leaf) {
        final Tree tree = CC_TREE.pick(TreeSigma::new, sigma); // FnZero.po?l(POOL_TREE, sigma, TreeSigma::new);
        return tree.fetch(sigma, type, leaf);
    }

    @Override
    public Future<JsonArray> treeSigma(final String sigma, final JsonArray types) {
        final Tree tree = CC_TREE.pick(TreeSigma::new, sigma); // FnZero.po?l(POOL_TREE, sigma, TreeSigma::new);
        return tree.fetch(sigma, types);
    }

    @Override
    public Future<JsonObject> treeSigma(final String sigma, final String type, final String code) {
        final Tree tree = CC_TREE.pick(TreeSigma::new, sigma); // FnZero.po?l(POOL_TREE, sigma, TreeSigma::new);
        return tree.fetch(sigma, type, code);
    }

    // ------------------------ Number Generation
    @Override
    public Future<JsonArray> numberApp(final String appId, final String code, final Integer count) {
        log.info("{} 序号生成: id = {}, code = {}, count = {}", AtConstant.K_PREFIX, appId, code, count);
        // APP_ID = ? AND CODE = ?
        final JsonObject condition = new JsonObject();
        condition.put(KName.APP_ID, appId).put(KName.CODE, code);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, appId); // FnZero.po?l(POOL_SERIAL, id, SerialGen::new);
        return serial.generate(condition, count);
    }


    @Override
    public Future<JsonArray> numberAppI(final String appId, final String identifier, final Integer count) {
        log.info("{} 序号生成: id = {}, identifier = {}, count = {}", AtConstant.K_PREFIX, appId, identifier, count);
        // APP_ID = ? AND IDENTIFIER = ?
        final JsonObject condition = new JsonObject();
        condition.put(KName.APP_ID, appId).put(KName.IDENTIFIER, identifier);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, appId); // FnZero.po?l(POOL_SERIAL, id, SerialGen::new);
        return serial.generate(condition, count);
    }

    @Override
    public Future<JsonArray> numberSigma(final String sigma, final String code, final Integer count) {
        log.info("{} 序号生成: sigma = {}, code = {}, count = {}", AtConstant.K_PREFIX, sigma, code, count);
        // SIGMA = ? AND CODE = ?
        final JsonObject condition = new JsonObject();
        condition.put(KName.SIGMA, sigma).put(KName.CODE, code);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, sigma); // FnZero.po?l(POOL_SERIAL, sigma, SerialGen::new);
        return serial.generate(condition, count);
    }

    @Override
    public Future<JsonArray> numberSigmaI(final String sigma, final String identifier, final Integer count) {
        log.info("{} 序号生成: sigma = {}, identifier = {}, count = {}", AtConstant.K_PREFIX, sigma, identifier, count);
        // SIGMA = ? AND IDENTIFIER = ?
        final JsonObject condition = new JsonObject();
        condition.put(KName.SIGMA, sigma).put(KName.IDENTIFIER, identifier);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, sigma); // FnZero.po?l(POOL_SERIAL, sigma, SerialGen::new);
        return serial.generate(condition, count);
    }

    @Override
    public Future<Boolean> numberAppR(final String appId, final String code, final Long defaultValue) {
        log.info("{} 序号重置: id = {}, code = {}, default = {}", AtConstant.K_PREFIX, appId, code, defaultValue);

        final JsonObject condition = new JsonObject();
        condition.put(KName.APP_ID, appId).put(KName.CODE, code);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, appId); // FnZero.po?l(POOL_SERIAL, id, SerialGen::new);
        return serial.reset(condition, defaultValue);
    }

    @Override
    public Future<Boolean> numberSigmaR(final String sigma, final String code, final Long defaultValue) {
        log.info("{} 序号重置: sigma = {}, code = {}, default = {}", AtConstant.K_PREFIX, sigma, code, defaultValue);

        final JsonObject condition = new JsonObject();
        condition.put(KName.SIGMA, sigma).put(KName.CODE, code);

        final Serial serial = CC_SERIAL.pick(SerialGen::new, sigma); // FnZero.po?l(POOL_SERIAL, sigma, SerialGen::new);
        return serial.reset(condition, defaultValue);
    }
}

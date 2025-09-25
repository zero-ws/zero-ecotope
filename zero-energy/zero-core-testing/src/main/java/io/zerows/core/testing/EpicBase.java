package io.zerows.core.testing;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.common.app.KIntegration;
import io.zerows.core.database.atom.Database;
import io.zerows.core.uca.qr.Criteria;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.uca.logging.OLog;

/**
 * # 「Co」 Testing Framework
 *
 * This class is testing base class to read file resources, here are specification in zero framework for testing
 *
 * 1. The resource path name should be `src/mock/resources/mock/[package]/[filename]`.
 * 2. The `package` name is the same as current class, the root folder is `mock` in testing resources.
 * 3. You can provide `filename` that will be used in testing classes.
 *
 * Here support three file format
 *
 * 1. String
 * 2. JsonObject
 * 3. JsonArray
 *
 * And so on, it provide testing logger for developers to record all the testing logs.
 *
 * This class is for JUnit purely
 *
 * The API is as following（All Api parameters are `filename`）:
 *
 * 1. ioString(filename) - String Content
 * 2. ioBuffer(filename) - Buffer Content
 * 3. ioJObject(filename) - InJson Object Content
 * 4. ioJArray(filename) - InJson Array Content
 * 5. ioDatabase(filename) - Database from file ( InJson Format )
 * 6. ioIntegration(filename) - KIntegration from file ( InJson Format )
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class EpicBase {

    protected String ioString(final String filename) {
        final Class<?> clazz = this.getClass();
        final String file = "test/" + clazz.getPackage().getName() + "/" + filename;
        this.logger().info("[ Tc ] Test input file reading from: {0}", file);
        return file;
    }

    protected Buffer ioBuffer(final String filename) {
        return Ut.ioBuffer(this.ioString(filename));
    }

    protected JsonObject ioJObject(final String filename) {
        return Ut.ioJObject(this.ioString(filename));
    }

    protected Database ioDatabase(final String filename) {
        final JsonObject fileJson = this.ioJObject(filename);
        final Database database = new Database();
        database.fromJson(fileJson);
        return database;
    }

    protected KIntegration ioIntegration(final String filename) {
        final JsonObject fileJson = this.ioJObject(filename);
        final KIntegration integration = new KIntegration();
        integration.fromJson(fileJson);
        return integration;
    }

    protected Criteria ioCriteria(final String filename) {
        return Criteria.create(this.ioJObject(filename));
    }

    protected JsonArray ioJArray(final String filename) {
        return Ut.ioJArray(this.ioString(filename));
    }

    protected OLog logger() {
        return Ut.Log.test(this.getClass());
    }

}

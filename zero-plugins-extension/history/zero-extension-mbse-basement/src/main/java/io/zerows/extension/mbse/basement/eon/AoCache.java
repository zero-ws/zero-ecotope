package io.zerows.extension.mbse.basement.eon;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.mbse.basement.atom.Model;
import io.zerows.extension.mbse.basement.uca.apply.AoDefault;
import io.zerows.extension.mbse.basement.uca.jdbc.AoConnection;
import io.zerows.extension.mbse.basement.uca.metadata.AoBuilder;
import io.zerows.extension.mbse.basement.uca.phantom.AoModeler;

/*
 * 池化处理
 */
public interface AoCache {
    // （设计图上存在）
    /* Model 池化 **/
    Cc<String, Model> CC_MODEL = Cc.open();
    /* AoConnection 池化管理 */
    Cc<String, AoConnection> CC_CONNECTION = Cc.open();

    /* AoBuilder 池化管理 */
    Cc<String, AoBuilder> CC_BUILDER = Cc.openThread();

    // （内部）
    /* OxModeler资源池 */
    Cc<String, AoModeler> CC_MODELER = Cc.openThread();

    Cc<String, AoDefault> CC_DEFAULT = Cc.open();
}

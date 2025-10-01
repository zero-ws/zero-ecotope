package io.zerows.extension.mbse.basement.uca.jdbc;

import io.r2mo.function.Fn;
import io.zerows.epoch.common.shared.app.KDatabase;
import io.zerows.core.util.Ut;
import io.zerows.extension.mbse.basement.exception._80508Exception404PinNotFound;
import io.zerows.extension.mbse.basement.uca.metadata.AoBuilder;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.specification.modeling.operation.HDao;

import java.util.Objects;

/**
 * 统一读取组件的接口，新版的数据访问层在 yml 文件中仅定义 Pin 插件就可以了，
 * 其他所有插件通过 Pin 来实现门面转化的动作，包括读取其他组件的应用都透过 Pin来完成，
 * Pin 中还可以检查数据库连接。
 */
public interface Pin {

    /**
     * 根据配置文件读取连接器
     */
    static Pin getInstance() {
        final Class<?> clazz = Ao.pluginPin();
        Fn.jvmKo(Objects.isNull(clazz), _80508Exception404PinNotFound.class, "implPin");
        return Ut.singleton(clazz);
    }

    /**
     * 读取发布器专用接口
     * 发布器执行时，必须知道是针对哪个Database进行发布
     */
    AoBuilder getBuilder(KDatabase database);

    /**
     * 读取数据处理访问器专用接口
     * 用于Crud等各种复杂操作
     */
    HDao getDao(KDatabase database);
}

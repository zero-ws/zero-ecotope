package io.zerows.extension.module.mbsecore.component.jdbc;

import io.r2mo.base.dbe.Database;
import io.r2mo.function.Fn;
import io.zerows.extension.module.mbsecore.boot.Ao;
import io.zerows.extension.module.mbsecore.component.metadata.AoBuilder;
import io.zerows.extension.module.exception._80508Exception404PinNotFound;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.support.Ut;

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
    AoBuilder getBuilder(Database database);

    /**
     * 读取数据处理访问器专用接口
     * 用于Crud等各种复杂操作
     */
    HDao getDao(Database database);
}

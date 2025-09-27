package io.zerows.core.exception.boot;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.r2mo.vertx.common.exception.VertxE;

/**
 * @author lang : 2025-09-26
 */
public interface ERR {

    VertxE _11000 = VertxE
        .of(-11000, "[ ZERO ] （通用）无法在 META-INF/services/{} 中找到任何实现类，请检查配置.")
        .state(HttpResponseStatus.NOT_FOUND);

    VertxE _11002 = VertxE
        .of(-11002, "[ ZERO ] 参数 `filename` = {}, 对应的 InputStream 为空 = null.")
        .state(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    VertxE _11003 = VertxE
        .of(-11003, "[ ZERO ] 异常 {} 没有定义，无法在异常表中找到！")
        .state(HttpResponseStatus.NOT_FOUND);

    VertxE _11004 = VertxE
        .of(-11004, "[ ZERO ] 系统遇到了 Json 格式问题：decoding / encoding. 文件：{}")
        .state(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);

    VertxE _11005 = VertxE
        .of(-11005, "[ ZERO ] 不支持的操作，当前操作不被允许，请检查方法：{}")
        .state(HttpResponseStatus.NOT_IMPLEMENTED);

    VertxE _11007 = VertxE
        .of(-11007, "[ ZERO ] 输入的池化参数是 null，会引起 NullPointerException.")
        .state(HttpResponseStatus.NOT_FOUND);

    VertxE _11010 = VertxE
        .of(-11010, "[ ZERO ] 路径中 META-INF/services/{} 组件为空 = null, 请检查配置，调用者：{1}")
        .state(HttpResponseStatus.INTERNAL_SERVER_ERROR);

    VertxE _11011 = VertxE
        .of(-11011, "[ ZERO ] 反射调用的前置条件 pre-condition 不满足，方法：{}")
        .state(HttpResponseStatus.INTERNAL_SERVER_ERROR);


    VertxE _40101 = VertxE.of(-40101).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40102 = VertxE.of(-40102).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40103 = VertxE.of(-40103).state(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    VertxE _40104 = VertxE.of(-40104).state(HttpResponseStatus.CONFLICT);
}

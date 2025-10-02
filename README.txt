代码阅读的标记作用
// #BOOT-NNN: 和容器启动的完整生命周期，编号表示顺序
// #SPI: 和 SPI 直接相关的代码
// #PIN: 和其他插件直接相关的核心代码
// #REQ-NNN：和请求处理直接相关的代码，编号表示顺序
新版的核心对象说明
HED: HighOrder Encrypt Decrypt 对象 / 加密解密专用工具类
HPI: HighOrder Service Provider Interface / 高阶服务接口 -> 底层直接对接 SPI
HOI: HighOrder Owner ID / 租户相关标识处理逻辑
DBE: Database Engine / 数据库引擎
HFS/RFS: HighOrder File System / Remote File System -> 抽象之后的完整存储结构
Fx:  Function Extension / 函数扩展处理
Ux:  Utility Extension / 工具扩展处理
Ut:  Utility / 工具类
通常在编程模式之下不会手工去创建三字母对象，只会直接调用 Fx/Ux，Ut 都是少量场景才会调用，而三字母对象是留给研发人员使用做框架内部研发的
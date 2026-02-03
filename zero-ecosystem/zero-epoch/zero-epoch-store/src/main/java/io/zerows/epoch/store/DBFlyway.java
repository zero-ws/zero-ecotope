package io.zerows.epoch.store;

import java.util.List;

/**
 * 构造默认的 locations 专用接口，用于 Flyway 的数据库迁移操作，防止用户在 vertx.yml 中忘记配置 locations 参数
 */
public interface DBFlyway {

    List<String> waitFlyway(String dbType);
}

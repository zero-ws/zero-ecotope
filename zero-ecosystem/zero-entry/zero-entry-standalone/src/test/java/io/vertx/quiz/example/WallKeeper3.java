package io.vertx.quiz.example;

import io.zerows.epoch.annotations.security.Wall;

@Wall(value = "key2", path = "/api/*")
public class WallKeeper3 {
}

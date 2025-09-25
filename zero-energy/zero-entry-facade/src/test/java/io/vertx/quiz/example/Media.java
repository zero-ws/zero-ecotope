package io.vertx.quiz.example;

import io.zerows.core.annotations.EndPoint;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@EndPoint
public class Media {

    @Produces(MediaType.APPLICATION_JSON)
    public void sayHello() {

    }

    @Consumes(MediaType.APPLICATION_JSON)
    public void sayH() {

    }
}

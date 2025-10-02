package io.zerows.launcher.backbone.hunt;

import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.*;

@EndPoint
public class GetParam {

    public GetParam() {
    }

    private void ensure(final Object input) {
        System.out.println(input);
        if (null == input) {
            throw new RuntimeException("name could not be getPlugin:" + input);
        }
    }

    @Path("/query")
    @GET
    public void getQuery(@QueryParam("name") final String name) {
        this.ensure(name);
    }

    @Path("/query/{name}")
    @GET
    public void getPath(@PathParam("name") final String name) {
        this.ensure(name);
    }

    @Path("/query/header")
    @GET
    public void getHeader(@HeaderParam("Content-Type") final String content) {
        this.ensure(content);
    }

    @Path("/query/cookie")
    @GET
    public void getCookie(@CookieParam("cookie") final String content) {
        System.out.println(content);
    }
}

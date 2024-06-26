# Micro, Simple Rpc

From this tutorials we started to move to Rpc tutorials in zero system, at first let's move to the first example.

## 1. Services

The whole example web request flow should be as following picture described:

![](/doc/image/d10085-1.png)

Here are three projects in current demo:

| Http Port | Ipc Port | Ipc Service Name | Project   | Role           |
|:----------|:---------|:-----------------|:----------|:---------------|
| 6100      | --       | --               | up-athena | Api Gateway    |
| 6201      | --       | --               | up-atlas  | Common Service |
| 6401      | 6411     | ipc-coeus        | up-coeus  | Coordinator A  |

## 2. Source Code

Although above picture described complex micro environment web request flow, but in zero system, developers do not care
about service details such as Discovery, Registry, Communication

### 2.1. SingleApi \( service: up-atlas \)

```java
package up.god.micro.rpc;

import io.vertx.up.annotations.Address;
import io.vertx.up.annotations.EndPoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@EndPoint
@Path("/api")
public interface SingleApi {

    @Path("rpc/{name}")
    @GET
    @Address("ZERO://RPC/FIRST")
    String sayHello(@PathParam("name") String name);
}
```

### 2.2. SingleWorker \( service: up-atlas \)

```java
package up.god.micro.rpc;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.up.aiki.Ux;
import io.vertx.up.annotations.Address;
import io.vertx.up.annotations.Queue;
import io.vertx.up.atom.Envelop;
import io.vertx.up.plugin.rpc.RpcClient;

import io.zerows.annotations.infix.Rpc;

@Queue
public class SingleWorker {

    @Rpc
    private transient RpcClient client;

    @Address("ZERO://RPC/FIRST")
    public void sayHello(final Message<Envelop> message) {
        final String name = Ux.getString(message);
        final JsonObject params = new JsonObject().put("name", name);
        this.client.connect("ipc-coeus", "RPC://SAY/HELLO", params, res -> {
            if (res.succeeded()) {
                message.reply(Envelop.success(res.result()));
            } else {
                res.cause().printStackTrace();
                message.reply(Envelop.failure(res.cause().getMessage()));
            }
        });
    }
}
```

### 2.3. HelloInsider \( service: up-coeus \)

```java
package up.god.ipc;

import io.vertx.core.json.JsonObject;
import io.vertx.up.annotations.Ipc;
import io.vertx.up.atom.Envelop;

public class HelloInsider {

    @Ipc("RPC://SAY/HELLO")
    public Envelop sayHello(final Envelop envelop) {
        final JsonObject data = envelop.data();
        System.out.println(data);
        return Envelop.success(data);
    }
}
```

## 3. Testing

Then start above three services and testing with Postman \( The 6100 is api gateway port \)

**URL** : [http://localhost:6100/api/rpc/lang.yu](http://localhost:6100/api/rpc/lang.yu)

**Method** : GET

**Response** :

```json
{
    "data": {
        "name": "lang.yu"
    }
}
```

## 4. Console

When you send the request you should see some message output:

**service: up-atlas**

```shell
[ ZERO ] ( Rpc Client ) Build channel ( host = 10.0.0.7, port = 6411, hashCode = 58401608 )
[ ZERO ] ( Rpc Client ) Final Traffic Data will be IpcData......
[ ZERO ] ( Rpc Client ) Response Json data is {"name":"lang.yu"}
```

**service: up-coeus**

```shell
[ ZERO ] --> ( Terminator ) found, will provide response. method ......
{"name":"lang.yu"}
[ ZERO ] Current flow is Envelop, return type = class io.vertx.up.argument.Envelop
```

## 5. Summary

From this demo, you could see the whole web request workflow has been finished in zero system. Here we used another new
annotation `io.zerows.annotations.infix.Rpc`, this annotation is defined by zero system. You can inject `RpcClient` directly
here, the reference of `RpcClient` is the type of `io.vertx.up.plugin.rpc.RpcClient`.

Another annotation to describe Rpc address is `io.vertx.up.annotations.Ipc` , this annotation could tell zero system
which method will be executed in Rpc Server, in further tutorials we'll introduce more usage of these annotations here. 

 

 \`


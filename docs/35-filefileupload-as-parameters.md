# File/FileUpload as parameters

## 1. Source Code

EndPoint Code

```java
package up.god.apollo.exp6;

import io.vertx.core.json.JsonObject;
import io.vertx.up.annotations.Codex;
import io.vertx.up.annotations.EndPoint;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import io.zerows.core.web.io.annotations.StreamParam;
import java.io.File;

@EndPoint
@Path("/exp6/")
public class FileUpload {

    @POST
    @Path("/upload")
    public JsonObject upload(@Codex @StreamParam final File file) {
        System.out.println(file.getAbsoluteFile());
        return null;
    }
}
```

## 2. Points

Be careful about the parameter annotation, the file uploading parameter must be annotated with `@StreamParam`，It's for
binary mode to get all bytes data from client. Now the parameter type support `java.io.File`
and `io.vertx.ext.web.FileUpload`，but we recommend use `FileUpload` type instead of `java.io.File` because this object
contains many metadata information such as filename, filesize etc.

 \`


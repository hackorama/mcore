package m.core.server.micronaut;

import static io.micronaut.http.HttpResponse.*;

import io.micronaut.http.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.validation.Validated;

@io.micronaut.http.annotation.Controller("/")
@Validated
public class Controller {
    @Get(uri = "/echo", produces = MediaType.TEXT_PLAIN)
    public HttpResponse<String> echo(HttpRequest<?> request) {
        return ok(request.getPath());
    }

    public String test() {
        return "test";
    }

}

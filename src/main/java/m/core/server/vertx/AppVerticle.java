package m.core.server.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class AppVerticle extends AbstractVerticle {

    private Vertx vertx;
    private Router router;
    private int port;

    public AppVerticle(Vertx vertx, Router router, int port) {
        this.vertx = vertx;
        this.router = router;
        this.port = port;
    }

    @Override
    public void start(Future<Void> future) {
        vertx.createHttpServer().requestHandler(router).listen(port, result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }

}

package m.core.server.micronaut;

import javax.inject.Inject;

import io.micronaut.context.ExecutionHandleLocator;
import io.micronaut.web.router.DefaultRouteBuilder;

public class Router extends DefaultRouteBuilder {

    public Router(ExecutionHandleLocator executionHandleLocator) {
        super(executionHandleLocator);
    }

    @Inject
    void routes(Controller controller) {
        GET("/test", controller, "test");
    }
}

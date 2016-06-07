package net.bunselmeyer.middleware.pipes;


import net.bunselmeyer.middleware.core.ConfigurableApp;
import net.bunselmeyer.middleware.core.MiddlewareApp;
import net.bunselmeyer.middleware.core.RoutableApp;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

public interface Controller<A extends ConfigurableApp & MiddlewareApp> extends RunnableApp<HttpRequest, HttpResponse> {

    void configure(ConfigurableApp<A> app);

    void middleware(MiddlewareApp<HttpRequest, HttpResponse, A> app);

    void route(RoutableApp<HttpRequest, HttpResponse> app);

    void onError(A app);

}

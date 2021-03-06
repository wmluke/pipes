package net.bunselmeyer.middleware.pipes.http;

import io.netty.handler.codec.http.Cookie;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Consumer;

public interface HttpResponse {

    boolean isCommitted();

    HttpResponse status(int status);

    Integer status();

    HttpResponse header(String name, String value);

    String header(String name);

    Map<String, String> headers();

    HttpResponse cookie(String name, Cookie value);

    HttpResponse cookie(String name, String value, Consumer<Cookie> cookieOptions);

    HttpResponse clearCookie(String name);

    HttpResponse redirect(String url);

    HttpResponse charset(String charset);

    HttpResponse charset(Charset charset);

    Charset charset();

    HttpResponse type(String type);

    String type();

    HttpResponse send(int status);

    HttpResponse send(int status, String body);

    HttpResponse send(String body);

    HttpResponse sendWriter(ThrowingConsumer<PrintWriter, IOException> consumer);

    HttpResponse sendOutput(ThrowingConsumer<OutputStream, IOException> consumer);

    HttpResponse json(int status);

    HttpResponse toJson(int status, Object body);

    HttpResponse toJson(Object body);

    HttpResponse json(String body);

    PrintWriter writer() throws IOException;

    OutputStream outputStream() throws IOException;

    HttpServletResponse delegate();

    interface ThrowingConsumer<T, E extends Throwable> {
        void accept(T t) throws E;
    }

    static javax.servlet.http.Cookie servletCookie(Cookie nettyCookie) {
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(nettyCookie.name(), nettyCookie.value());
        servletCookie.setHttpOnly(nettyCookie.isHttpOnly());
        servletCookie.setComment(nettyCookie.comment());
        if (nettyCookie.domain() != null) {
            servletCookie.setDomain(nettyCookie.domain());
        }
        servletCookie.setPath(nettyCookie.path());
        servletCookie.setMaxAge((int) nettyCookie.maxAge());
        if (nettyCookie.isDiscard()) {
            servletCookie.setMaxAge(0);
        }
        servletCookie.setSecure(nettyCookie.isSecure());
        servletCookie.setVersion(nettyCookie.version());
        return servletCookie;
    }
}

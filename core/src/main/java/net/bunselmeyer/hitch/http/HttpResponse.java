package net.bunselmeyer.hitch.http;

import io.netty.handler.codec.http.Cookie;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public interface HttpResponse {

    boolean isCommitted();

    HttpResponse status(int status);

    Integer status();

    HttpResponse header(String name, String value);

    String header(String name);

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

    HttpResponse json(int status);

    HttpResponse json(int status, Object body);

    HttpResponse json(String body);

    HttpServletResponse delegate();


    public static javax.servlet.http.Cookie servletCookie(Cookie nettyCookie) {
        javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(nettyCookie.getName(), nettyCookie.getValue());
        servletCookie.setHttpOnly(nettyCookie.isHttpOnly());
        servletCookie.setComment(nettyCookie.getComment());
        if (nettyCookie.getDomain() != null) {
            servletCookie.setDomain(nettyCookie.getDomain());
        }
        servletCookie.setPath(nettyCookie.getPath());
        servletCookie.setMaxAge((int) nettyCookie.getMaxAge());
        if (nettyCookie.isDiscard()) {
            servletCookie.setMaxAge(0);
        }
        servletCookie.setSecure(nettyCookie.isSecure());
        servletCookie.setVersion(nettyCookie.getVersion());
        return servletCookie;
    }
}

package net.bunselmeyer.hitch.server;

import io.netty.handler.codec.http.Cookie;
import net.bunselmeyer.hitch.app.Options;

import java.nio.charset.Charset;
import java.util.Map;

public interface Response {

    Response status(int status);

    Integer status();

    Response header(String name, String value);

    String header(String name);

    Response cookie(String name, Cookie value);

    Response cookie(String name, String value, Options<Cookie> cookieOptions);

    Cookie cookie(String name);

    Response clearCookie(String name);

    Response redirect(int status, String url);

    Response redirect(String url);

    Response charset(String charset);

    Response charset(Charset charset);

    Charset charset();

    Response type(String type);

    String type();

    Response send(int status);

    Response send(int status, String body);

    Response send(String body);

    Response json(int status);

    Response json(int status, String body);

    Response json(String body);

    String body();

    Map<String, String> headers();

    Map<String, Cookie> cookies();
}

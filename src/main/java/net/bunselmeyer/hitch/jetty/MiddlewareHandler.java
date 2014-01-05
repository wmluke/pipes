package net.bunselmeyer.hitch.jetty;

import com.google.common.base.Joiner;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.ServerCookieEncoder;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.DefaultResponse;
import net.bunselmeyer.hitch.app.Middleware;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

public class MiddlewareHandler extends AbstractHandler {

    private final App app;

    public MiddlewareHandler(App app) {
        this.app = app;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


        HttpServletWrapperRequest req = new HttpServletWrapperRequest(request);

        DefaultResponse res = new DefaultResponse();

        for (Middleware middleware : app.middleware()) {
            middleware.run(req, res);
        }

        baseRequest.setHandled(true);

        response.setStatus(res.status());

        for (Map.Entry<String, String> entry : res.headers().entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Cookie> entry : res.cookies().entrySet()) {
            response.addHeader(SET_COOKIE.toString(), ServerCookieEncoder.encode(entry.getValue()));

        }

        List<String> contentType = new ArrayList<>();
        contentType.add(StringUtils.trimToNull(res.type()));
        if (res.charset() != null) {
            contentType.add("charset=" + res.charset().name());
        }
        response.addHeader(CONTENT_TYPE.toString(), Joiner.on("; ").skipNulls().join(contentType));
        response.getWriter().append(res.body());
    }
}

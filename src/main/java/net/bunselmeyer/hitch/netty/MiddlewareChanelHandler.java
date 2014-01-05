package net.bunselmeyer.hitch.netty;

import com.google.common.base.Joiner;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.app.DefaultResponse;
import net.bunselmeyer.hitch.app.Middleware;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class MiddlewareChanelHandler extends SimpleChannelInboundHandler<HttpMessage> {

    private final App app;

    public MiddlewareChanelHandler(App app) {
        this.app = app;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
        if (msg instanceof DefaultFullHttpRequest) {
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;

            if (is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            boolean keepAlive = isKeepAlive(request);

            NettyWrapperRequest defaultRequest = new NettyWrapperRequest(request);

            DefaultResponse response = new DefaultResponse();
            response.charset("UTF-8");


            for (Middleware middleware : app.middleware()) {
                middleware.run(defaultRequest, response);
            }

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(response.status()),
                    Unpooled.copiedBuffer(response.body(), response.charset())
            );

            for (Map.Entry<String, String> entry : response.headers().entrySet()) {
                httpResponse.headers().set(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, Cookie> entry : response.cookies().entrySet()) {
                httpResponse.headers().set(SET_COOKIE, ServerCookieEncoder.encode(entry.getValue()));
            }

            List<String> contentType = new ArrayList<>();
            contentType.add(StringUtils.trimToNull(response.type()));
            if (response.charset() != null) {
                contentType.add("charset=" + response.charset().name());
            }
            httpResponse.headers().set(CONTENT_TYPE, Joiner.on("; ").skipNulls().join(contentType));
            httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());


            if (!keepAlive) {
                ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
            } else {
                httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                ctx.write(httpResponse);
            }
        }
    }
}

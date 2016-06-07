package net.bunselmeyer.middleware.server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.netty.HttpRequestNettyAdapter;
import net.bunselmeyer.middleware.pipes.http.netty.HttpResponseNettyAdapter;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

class MiddlewareChanelHandler extends SimpleChannelInboundHandler<HttpMessage> {

    private final RunnableApp<HttpRequest, HttpResponse> app;

    MiddlewareChanelHandler(RunnableApp<HttpRequest, HttpResponse> app) {
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

            ObjectMapper jsonMapper = app.configuration(ObjectMapper.class);
            ObjectMapper xmlMapper = app.configuration(ObjectMapper.class, App.XML_MAPPER_NAME);

            AbstractHttpRequest req = new HttpRequestNettyAdapter(request, jsonMapper, xmlMapper);
            HttpResponseNettyAdapter res = new HttpResponseNettyAdapter(ctx, isKeepAlive(request), jsonMapper);

            app.run(req, res, null);

        }
    }
}

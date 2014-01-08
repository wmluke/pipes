package net.bunselmeyer.hitch.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import net.bunselmeyer.hitch.app.App;
import net.bunselmeyer.hitch.http.AbstractHttpRequest;

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

            AbstractHttpRequest req = new HttpRequestNettyAdapter(request);
            HttpResponseNettyAdapter res = new HttpResponseNettyAdapter(ctx, isKeepAlive(request));

            app.dispatch(req, res);

        }
    }
}

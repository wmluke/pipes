package net.bunselmeyer.middleware.server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import net.bunselmeyer.middleware.core.App;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.AbstractHttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;
import net.bunselmeyer.middleware.pipes.http.netty.HttpRequestNettyAdapter;
import net.bunselmeyer.middleware.pipes.http.netty.HttpResponseNettyAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

class MiddlewareChanelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(MiddlewareChanelHandler.class);

    private static final HttpDataFactory factory =
        new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

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
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        ObjectMapper jsonMapper = app.configuration(ObjectMapper.class);
        ObjectMapper xmlMapper = app.configuration(ObjectMapper.class, App.XML_MAPPER_NAME);

        AbstractHttpRequest req = new HttpRequestNettyAdapter(request, jsonMapper, xmlMapper);
        HttpResponseNettyAdapter res = new HttpResponseNettyAdapter(ctx, isKeepAlive(request), jsonMapper);

        app.run(req, res, null);

    }
}

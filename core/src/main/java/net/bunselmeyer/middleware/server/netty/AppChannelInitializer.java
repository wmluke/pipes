package net.bunselmeyer.middleware.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.bunselmeyer.middleware.core.RunnableApp;
import net.bunselmeyer.middleware.pipes.http.HttpRequest;
import net.bunselmeyer.middleware.pipes.http.HttpResponse;

class AppChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final RunnableApp<HttpRequest, HttpResponse> app;

    AppChannelInitializer(RunnableApp<HttpRequest, HttpResponse> app) {
        this.app = app;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline p = ch.pipeline();

        //p.addLast(new HttpResponseEncoder());
        //p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new ChunkedWriteHandler());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new MiddlewareChanelHandler(app));


    }
}

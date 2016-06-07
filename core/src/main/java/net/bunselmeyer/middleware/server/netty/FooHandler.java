package net.bunselmeyer.middleware.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;

public class FooHandler extends IdleStateHandler {
    public FooHandler() {
        super(20000, 20000, 20000);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}

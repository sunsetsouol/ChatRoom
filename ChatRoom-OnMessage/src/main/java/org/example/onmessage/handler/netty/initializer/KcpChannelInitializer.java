package org.example.onmessage.handler.netty.initializer;

import io.jpower.kcp.netty.UkcpChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import org.example.onmessage.handler.netty.server.KcpServerHandler;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/7
 */
@RequiredArgsConstructor
@Component
public class KcpChannelInitializer extends ChannelInitializer<UkcpChannel> {
   private final KcpServerHandler kcpServerHandler;
    @Override
    protected void initChannel(UkcpChannel ukcpChannel) throws Exception {
        ChannelPipeline pipeline = ukcpChannel.pipeline();
        pipeline.addLast(kcpServerHandler);
    }
}

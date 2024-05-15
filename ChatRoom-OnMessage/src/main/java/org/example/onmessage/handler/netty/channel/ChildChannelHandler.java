package org.example.onmessage.handler.netty.channel;

import io.jpower.kcp.netty.UkcpChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChildChannelHandler extends ChannelInitializer<UkcpChannel> {
    private final ServerHandler serverHandler;

    static final int CONV = Integer.parseInt(System.getProperty("conv", "10"));
    static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));
    @Override
    protected void initChannel(UkcpChannel ukcpChannel) throws Exception {
        log.info("initChannel");
        ChannelPipeline pipeline = ukcpChannel.pipeline();
        pipeline.addLast(serverHandler);
    }
}

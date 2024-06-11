package org.example.onmessage.listener;

import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpServerChannel;
import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.onmessage.constants.NettyConstant;
import org.example.onmessage.handler.netty.initializer.KcpChannelInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/7
 */
@Component
public class SpringInitListener implements ApplicationListener<ContextRefreshedEvent> {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final UkcpServerBootstrap ukcpServerBootstrap;
    private final KcpChannelInitializer kcpChannelInitializer;
    private Integer PORT;

    public SpringInitListener(@Qualifier(NettyConstant.BOSS_EVENT_LOOP_GROUP)EventLoopGroup bossGroup, @Qualifier(NettyConstant.WORKER_EVENT_LOOP_GROUP)EventLoopGroup workerGroup,
                              UkcpServerBootstrap ukcpServerBootstrap, KcpChannelInitializer kcpChannelInitializer,
                              @Value("#{${server.port}-74}") Integer PORT) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.ukcpServerBootstrap = ukcpServerBootstrap;
        this.kcpChannelInitializer = kcpChannelInitializer;
        this.PORT = PORT;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ukcpServerBootstrap.group(bossGroup)
                .channel(UkcpServerChannel.class)
                .childHandler(kcpChannelInitializer);
        ChannelOptionHelper.nodelay(ukcpServerBootstrap, true, 20, 2, true)
                .childOption(UkcpChannelOption.UKCP_MTU, 512);

        // Start the server.
        ChannelFuture f = ukcpServerBootstrap.bind(PORT).sync();

        f.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });
        // Wait until the server socket is closed.
//        f.channel().closeFuture().sync();
    }
}

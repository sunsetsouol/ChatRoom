package org.example.onmessage.config.netty;

import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.example.onmessage.constants.NettyConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/7
 */
@Configuration
public class NettyConfig {

    @Bean(NettyConstant.BOSS_EVENT_LOOP_GROUP)
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(NettyConstant.WORKER_EVENT_LOOP_GROUP)
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public UkcpServerBootstrap ukcpServerBootstrap() {
        return new UkcpServerBootstrap();
    }
}

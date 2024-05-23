package org.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.example.gateway.filter.CustomReactiveLoadBalanceFilter;
import org.example.gateway.hashring.HashRingUtil;
import org.example.gateway.service.common.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/19
 */
@Configuration
@Slf4j
public class GatewayConfig {

    @Autowired
    private  LoadBalancerClientFactory clientFactory;
    @Autowired
    private RedisCacheService redisCacheService;
//    @Bean
//    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client,
//                                                             LoadBalancerProperties properties,
//                                                             DiscoveryClient discoveryClient) {
//        return new CustomLoadBalancerClientFilter(client, properties,discoveryClient);
//    }
    /**
     * @param client                 负载均衡客户端
     * @param loadBalancerProperties 负载均衡配置
     * @param hashRingUtil
     * @param discoveryClient        服务发现客户端
     * @return 注入自定义的 Reactive 过滤器 Bean 对象
     */
    @Bean
    public CustomReactiveLoadBalanceFilter customReactiveLoadBalanceFilter(LoadBalancerClient client,
                                                                           LoadBalancerProperties loadBalancerProperties,
                                                                           HashRingUtil hashRingUtil,
                                                                           DiscoveryClient discoveryClient) {
        log.debug("初始化 自定义响应式负载均衡器: {}, {}", client, loadBalancerProperties);
        return new CustomReactiveLoadBalanceFilter(clientFactory, loadBalancerProperties,
                hashRingUtil, discoveryClient, redisCacheService);
    }



    @Bean
    public GlobalFilter customGlobalPostFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.just(exchange))
                .map(serverWebExchange -> {
                    //adds header to response
                    serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
                            HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
                    return serverWebExchange;
                })
                .then();
    }
}

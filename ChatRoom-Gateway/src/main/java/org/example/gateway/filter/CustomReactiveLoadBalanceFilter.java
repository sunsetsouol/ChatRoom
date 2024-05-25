package org.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gateway.hashring.HashRingUtil;
import org.example.gateway.service.common.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * 响应式网关的负载均衡过滤器
 *
 * @author lawrence
 * @since 2021/3/29
 */
@Slf4j
public class CustomReactiveLoadBalanceFilter extends ReactiveLoadBalancerClientFilter implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomReactiveLoadBalanceFilter.class);

    final HashRingUtil hashRingUtil;
    final DiscoveryClient discoveryClient;
    final LoadBalancerClientFactory clientFactory;
    final LoadBalancerProperties properties;
    final RedisCacheService redisCacheService;

    public CustomReactiveLoadBalanceFilter(LoadBalancerClientFactory clientFactory,
                                           LoadBalancerProperties properties,
                                           HashRingUtil hashRingUtil,
                                           DiscoveryClient discoveryClient, RedisCacheService redisCacheService) {
        super(clientFactory, properties);
        this.clientFactory = clientFactory;
        this.properties = properties;
        this.hashRingUtil = hashRingUtil;
        this.discoveryClient = discoveryClient;
        this.redisCacheService = redisCacheService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("CustomReactiveLoadBalanceFilter.filter");
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null
                || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        // preserve the original url
        addOriginalRequestUrl(exchange, url);

        if (logger.isTraceEnabled()) {
            logger.trace("{} url before: {}", ReactiveLoadBalancerClientFilter.class.getSimpleName(), url);
        }

        return chooseInstance(exchange).doOnNext(response -> {
            if (!response.hasServer()) {
                throw NotFoundException.create(properties.isUse404(),
                        "Unable to find instance for " + url.getHost());
            }

            ServiceInstance retrievedInstance = response.getServer();

            URI uri = exchange.getRequest().getURI();

            // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
            // if the loadbalancer doesn't provide one.
            String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }

            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(
                    retrievedInstance, overrideScheme);

            URI requestUrl = reconstructURI(serviceInstance, uri);

            if (logger.isTraceEnabled()) {
                logger.trace("LoadBalancerClientFilter url chosen: {}", requestUrl);
            }
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }).then(chain.filter(exchange));
    }

    @SuppressWarnings("deprecation")
    private Mono<Response<ServiceInstance>> chooseInstance(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        assert uri != null;
        WebSocketSessionLoadBalancer loadBalancer = new WebSocketSessionLoadBalancer(
                clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class),
                hashRingUtil,
                discoveryClient, redisCacheService);
        return loadBalancer.choose(this.createRequest(exchange));
    }

    @SuppressWarnings("deprecation")
    private Request<ServerWebExchange> createRequest(ServerWebExchange exchange) {
        // 其实返回的 Request 对象里至少需要包含的就是负载均衡时 choose 的依据
        return new DefaultRequest<>(exchange);
    }

}

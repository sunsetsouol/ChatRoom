//package org.example.gateway.filter;
//
//import org.example.gateway.rule.EngineeringChooseRule;
//import org.example.gateway.rule.IChooseRule;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
//import org.springframework.cloud.gateway.config.LoadBalancerProperties;
//import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
//import org.springframework.web.server.ServerWebExchange;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/5/19
// */
//public class CustomLoadBalancerClientFilter extends LoadBalancerClientFilter {
//    private final DiscoveryClient discoveryClient;
//    private final List<IChooseRule> chooseRules;
//    public CustomLoadBalancerClientFilter(LoadBalancerClient loadBalancer, LoadBalancerProperties properties, DiscoveryClient discoveryClient) {
//        super(loadBalancer, properties);
//        this.discoveryClient = discoveryClient;
//        this.chooseRules = new ArrayList<>();
//        chooseRules.add(new EngineeringChooseRule());
//    }
//
//    @Override
//    protected ServiceInstance choose(ServerWebExchange exchange) {
//        System.out.println("CustomLoadBalancerClientFilter.choose");
//        System.out.println("+================================================q");
//        if (!chooseRules.isEmpty()) {
//            for (IChooseRule chooseRule : chooseRules) {
//                ServiceInstance choose = chooseRule.choose(exchange, discoveryClient);
//                if (choose != null) {
//                    return choose;
//                }
//            }
//        }
//        return loadBalancer.choose(((URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR)).getHost());
//    }
//}

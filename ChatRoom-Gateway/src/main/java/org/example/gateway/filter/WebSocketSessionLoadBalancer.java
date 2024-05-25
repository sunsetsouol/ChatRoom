package org.example.gateway.filter;


import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.GlobalConstants;
import org.example.constant.RedisCacheConstants;
import org.example.gateway.hashring.HashRingUtil;
import org.example.gateway.hashring.Node;
import org.example.gateway.service.common.RedisCacheService;
import org.example.pojo.bo.UserBO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * WebSocket 负载均衡器
 *
 * @author lawrence
 * @since 2021/3/29
 */
@Component
@Slf4j
@Data
public class WebSocketSessionLoadBalancer implements ReactorServiceInstanceLoadBalancer {


    private final HashRingUtil hashRingUtil;
    private final DiscoveryClient discoveryClient;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final RedisCacheService redisCacheService;

    public WebSocketSessionLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                        HashRingUtil hashRingUtil,
                                        DiscoveryClient discoveryClient, RedisCacheService redisCacheService) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.hashRingUtil = hashRingUtil;
        this.discoveryClient = discoveryClient;
        this.redisCacheService = redisCacheService;
    }


    @Override
    @SuppressWarnings("deprecation")
    public Mono<Response<ServiceInstance>> choose(Request request) {
        log.info("choose");
        ServerWebExchange exchange = (ServerWebExchange) request.getContext();
        URI originalUrl = (URI) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String instancesId = originalUrl.getHost();
        if (GlobalConstants.LISTEN_WS_SERVICE_NAME.equals(instancesId)) {
            // 获取需要参与哈希的字段，此项目为 userId
            final String userIdFromRequest = getUserIdFromRequest(exchange);
            if (null != userIdFromRequest && null != this.serviceInstanceListSupplierProvider) {
                // 请求参数中有 userId，需要经过哈希环的路由
                ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
                return (supplier.get()).next().map(list -> getServiceInstanceByUserId(userIdFromRequest, instancesId));
            }
        }
        return choose();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Mono<Response<ServiceInstance>> choose() {
        log.debug("【RoundRobin】无 userId 的请求，对于 WebSocket 服务也没有意义，轮询转发...");
        // Round-Robin: 该术语来源于含义为「带子」的法语词 ruban，久而被讹用并成为惯用语。在 17、18 世纪时法国农民希望以请愿的方式抗议国王时，
        // 通常君主的反应是将请愿书中最前面的两至三人逮捕并处决，所以很自然地没有人希望自己的名字被列在前面。为了对付这种专制的报复，
        // 人们在请愿书底部把名字签成一个圈（如同一条环状的带子），这样就找不出打头的人，于是只能对所有参与者进行同样的惩罚。
        RoundRobinLoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer(serviceInstanceListSupplierProvider, GlobalConstants.LISTEN_WS_SERVICE_NAME);
        return roundRobinLoadBalancer.choose();
    }

    /**
     * 哈希环的使用，根据 userId 来查询对应的节点
     *
     * @param userId      用户ID，关联了 WebSocket Session
     * @param instancesId 服务名
     * @return 服务实例的 Response
     */
    @SuppressWarnings("deprecation")
    private Response<ServiceInstance> getServiceInstanceByUserId(final String userId, String instancesId) {
        Node node = hashRingUtil.getConsistentHashRouter().routeNode(userId);
        if (null != node) {
            // 获取当前注册中心的实例
            List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
            for (ServiceInstance instance : instances) {
                log.info("当前实例: {}", instance);
                // 如果 userId 映射后的真实节点的 IP 与某个实例 IP 一致，就转发
                String key = instance.getHost() + ":" + instance.getPort();
                if (key.equals(node.getKey())) {
                    log.debug("当前客户端[{}]匹配到真实节点 {}", userId, node.getKey());
                    redisCacheService.setCacheObject(RedisCacheConstants.ONLINE + userId, key);
                    return new DefaultResponse(instance);
                }
            }
        }
        log.warn("网关监测到当前无哈希环, 即无 WebSocket 服务实例，尝试取第一个实例，可能为 null");
        ServiceInstance serviceInstance = discoveryClient.getInstances(instancesId).get(0);
        if (Objects.nonNull(serviceInstance)){
            redisCacheService.setCacheObject(RedisCacheConstants.ONLINE + userId, serviceInstance.getHost() + ":" + serviceInstance.getPort());
        }
        return new DefaultResponse(serviceInstance);
    }

    /**
     * 从 WS/HTTP 请求 中获取待哈希字段 userId
     *
     * @param exchange 请求上下文
     * @return userId，可能为空
     */
    protected static String getUserIdFromRequest(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(GlobalConstants.JSONTOKEN);

        //2.1 解析token
        String json = Base64.decodeStr(token);
        JSONObject userJson = JSON.parseObject(json);

        //2.2 获取jsonToken中的用户角色
        String user = (String) userJson.get("principal");
        UserBO userBO = JSON.parseObject(user, UserBO.class);
        return userBO.getId().toString();

//        URI originalUrl = (URI) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
//        String userId = "1";
//        if (originalUrl.getPath().startsWith(GlobalConstants.WEBSOCKET_ENDPOINT_PATH)) {
//            // ws: "lb://websocket-server/connect/1" 获取这里面的最后一个路径参数 userId: 1
//            List<PathContainer.Element> elements = exchange.getRequest().getPath().elements();
//            PathContainer.Element lastElement = elements.get(elements.size() - 1);
//            userId = lastElement.value();
//            log.debug("【网关负载均衡】WebSocket 获取到 userId: {}", userId);
//        } else {
//            // 前提：websocket http 服务 userId 放在 query 中
//            // rest: "lb://websocket-server/send?userId=1&message=text"
//            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
////            List<String> userIds = queryParams.get(GlobalConstant.KEY_TO_BE_HASHED);
////            if (null != userIds && !userIds.isEmpty()) {
////                userId = userIds.get(0);
////                logger.debug("【网关负载均衡】HTTP 获取到 userId: {}", userId);
////            }
//        }
//        return userId;
    }

}

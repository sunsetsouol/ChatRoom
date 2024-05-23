//package org.example.gateway.rule;
//
//import cn.hutool.core.codec.Base64;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.nacos.naming.utils.nacoshashring.entity.Address;
//import lombok.extern.slf4j.Slf4j;
//import org.example.gateway.util.hashring_util.HashRing;
//import org.example.gateway.util.hashring_util.Server;
//import org.example.gateway.util.hashring_util.support.HashRingRedis;
//import org.example.pojo.bo.UserBO;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.http.server.RequestPath;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//
//import javax.annotation.Resource;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/5/19
// */
//@Slf4j
//public class EngineeringChooseRule implements IChooseRule {
//
//    @Resource
//    @Qualifier("websocketServer")
//    private Server server;
//    @Override
//    public ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) {
//        URI originalUrl = (URI) exchange.getAttributes().get(GATEWAY_REQUEST_URL_ATTR);
//        String instancesId = originalUrl.getHost();
//
//        if ("onmessage".equals(instancesId) && exchange.getRequest().getHeaders().containsKey("Sec-WebSocket-Key")) {
//
//            List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
//
//            //1. 获取以base64加密的jsontoken
//            String token = exchange.getRequest().getHeaders().getFirst("jsonToken");
//
//
//            //2.1 解析token
//            String json = Base64.decodeStr(token);
//            JSONObject userJson = JSON.parseObject(json);
//
//            //2.2 获取jsonToken中的用户角色
//            String user = (String) userJson.get("principal");
//
//            UserBO userObj = null;
//
//            //2.5 解析为用户对象
//            if (StringUtils.hasText(user)) {
//                userObj = JSONObject.parseObject(user, UserBO.class);
//                HashRing hashRingRedis = HashRingRedis.newInstance(server);
//                String userId = String.valueOf(userObj.getId());
//                Address address = hashRingRedis.getAddress(userId);
//
//                log.info("ws服务站点数量为 为: {}", instances.size());
//                for (ServiceInstance instance : instances) {
//                    //if (address != null && address.equals(instance.getHost() + ":" + instance.getPort())) {
//                    if (address != null && address.getIp().equals(instance.getHost()) && address.getPort().equals(instance.getPort())) {
//                        log.info("id为 {} 的用户请求的ws连接到的机器 ip:端口为 {}:{}", userId, instance.getHost(), instance.getPort());
//                        return instance;
//                    }
//                }
//            }
//
//
//
////            RequestPath path = exchange.getRequest().getPath();
////            String pathStr = path.toString();
////
////            if (pathStr.contains("/ws")) {
////                String[] split = pathStr.split("/");
////                if (split.length == 4) {
////
////                    String userId = split[2];
////
////                    //String server = GateWayHashUtils.getServer(chatRoomId);
////
////                    HashRing hashRingRedis = HashRingRedis.newInstance(server);
////                    Address address = hashRingRedis.getAddress(userId);
////
////                    log.info("ws服务站点数量为 为: {}", instances.size());
////                    for (ServiceInstance instance : instances) {
////                        //if (address != null && address.equals(instance.getHost() + ":" + instance.getPort())) {
////                        if (address != null && address.getIp().equals(instance.getHost()) && address.getPort().equals(instance.getPort())) {
////                            log.info("id为 {} 的用户请求的ws连接到的机器 ip:端口为 {}:{}", userId, instance.getHost(), instance.getPort());
////                            return instance;
////                        }
////                    }
////                }
////            }
//        }
//        return null;
//    }
//}

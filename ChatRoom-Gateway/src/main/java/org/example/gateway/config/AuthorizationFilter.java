package org.example.gateway.config;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.constant.GlobalConstants;
import org.example.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/4
 */
@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request对象
        ServerHttpRequest request = exchange.getRequest();

        // 获取请求头中的token
        String token = request.getHeaders().getFirst(GlobalConstants.TOKEN_HEAD);

        // 如果token为空，直接放行，后面还有资源拦截器
        if (!StringUtils.hasText(token)){
            chain.filter(exchange);
        }

        // 如果token不为空，解析token，并放入请求头中
        String originToken = token.replace(GlobalConstants.TOKEN_PREFIX, "");

        String parseJwt = JwtUtil.parseJwt(originToken);
        JSONObject jsonObject = JSON.parseObject(parseJwt);

        // 原始参数无损传递
        Map<String ,Object > jsonToken = new HashMap<>(jsonObject);

        jsonToken.put("authorities", jsonObject.get("authorities"));
        jsonToken.put("principal", jsonObject.get("user_name"));
//        把token解析后的信息,放入jsonToken中,在微服务中传递
        request = request.mutate().header("jsonToken", Base64.encode(JSONObject.toJSONString(jsonToken))).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

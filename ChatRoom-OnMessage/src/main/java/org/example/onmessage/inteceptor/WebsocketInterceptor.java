package org.example.onmessage.inteceptor;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.GlobalConstants;
import org.example.constant.RedisCacheConstants;
import org.example.exception.BusinessException;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.vo.ResultStatusEnum;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/4
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        //1. 获取以base64加密的jsontoken
        String token = serverHttpRequest.getHeaders().getFirst("jsonToken");


        //2.1 解析token
        String json = Base64.decodeStr(token);
        JSONObject userJson = JSON.parseObject(json);

        //2.2 获取jsonToken中的用户角色
        String user = (String) userJson.get("principal");

        //2.3 权限信息
        JSONArray authoritiesArray = userJson.getJSONArray("authorities");

        //2.4 转为数组
        String[] authorities = null;

        try {
            authorities = authoritiesArray.toArray(new String[0]);
        } catch (Exception e) {
            log.warn("权限信息解析失败", e);
        }

        UserBO userObj = null;

        //2.5 解析为用户对象
        if (StringUtils.hasText(user)) {
            userObj = JSONObject.parseObject(user, UserBO.class);
            map.put("user", userObj);
        }

        List<String> strings = null;

        try {
            strings = new ArrayList<>(Arrays.asList(authorities));
        } catch (Exception e) {
            log.warn("权限信息转换失败", e);
        }
        map.put("authorities", strings);
        URI uri = serverHttpRequest.getURI();
        String path = uri.getPath();
        Long lastId = Long.valueOf(path.substring(path.lastIndexOf("/") + 1));
        map.put(GlobalConstants.LAST_GLOBAL_MESSAGE_ID, lastId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
    }
}

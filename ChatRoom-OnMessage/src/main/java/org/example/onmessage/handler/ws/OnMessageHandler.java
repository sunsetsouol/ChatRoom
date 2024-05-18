package org.example.onmessage.handler.ws;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.onmessage.constants.ThreadPoolConstant;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.dto.WsMessageDTO;
import org.example.onmessage.route.MessageBuffer;
import org.example.onmessage.service.MessageService;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.bo.UserBO;
import org.example.pojo.vo.ResultStatusEnum;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/4
 */
@Component
@RequiredArgsConstructor
public class OnMessageHandler implements WebSocketHandler {
    private final MessageService messageService;
    private final MessageBuffer messageBuffer;
    @Resource(name = ThreadPoolConstant.COMMON_THREAD_POOL_NAME)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Long userId = getUserId(webSocketSession);
        // 初始化发送最大clientId
        GlobalWsMap.PC_SESSION.put(userId, webSocketSession);
        Map<Integer, Long> collect = AbstractMessage.DeviceType.getTypeMap().keySet().stream()
                .collect(Collectors.toMap(Function.identity(), device -> messageBuffer.getMaxClientId(userId, device)));
        WsMessageDTO wsMessageDTO = new WsMessageDTO();
        wsMessageDTO.setMessageType(AbstractMessage.MessageType.INIT.getCode());
        wsMessageDTO.setMessage(JSON.toJSONString(collect));
        // TODO：没有ack就一直传
        webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(wsMessageDTO)));
//        redisCache.setCacheObject(user.getId().toString(), "", RedisCacheConstants.HEARTBEAT_TIMEOUT, TimeUnit.MINUTES);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        String message = webSocketMessage.getPayload().toString();

        WsMessageDTO wsMessageDTO = getWsMessage(webSocketSession, message);

        // 心跳处理
        if (wsMessageDTO.getMessageType().equals(AbstractMessage.MessageType.PING.getCode())){
            return;
        }
        CompletableFuture.runAsync(() -> messageService.accept(wsMessageDTO), threadPoolTaskExecutor);
    }

    private WsMessageDTO getWsMessage(WebSocketSession webSocketSession, String message) {
        WsMessageDTO wsMessageDTO = JSON.parseObject(message, WsMessageDTO.class);
        wsMessageDTO.setFromUserId(getUserId(webSocketSession));
        return wsMessageDTO;
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        GlobalWsMap.PC_SESSION.remove(getUserId(webSocketSession));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    Long getUserId(WebSocketSession webSocketSession){
        UserBO user = (UserBO) webSocketSession.getAttributes().get("user");
        if (Objects.isNull(user)){
            throw new BusinessException(ResultStatusEnum.UNAUTHORIZED);
        }
        return user.getId();
    }

}

package org.example.onmessage.publish.listener;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.onmessage.dao.MsgReader;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.onmessage.publish.event.UserOnlineEvent;
import org.example.onmessage.route.MessageBuffer;
import org.example.onmessage.service.common.RedisCacheService;
import org.example.pojo.AbstractMessage;
import org.example.pojo.dto.WsMessageDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserOnlineListener {
    private final MsgReader msgReader;
    private final MessageBuffer messageBuffer;
    @EventListener(UserOnlineEvent.class)
    public void userOnline(UserOnlineEvent event) throws IOException {
        Long userId = event.getUserId();
        WebSocketSession webSocketSession = event.getWebSocketSession();
        sendClientId(userId, webSocketSession);
        sendUnReadMessage(userId, webSocketSession);
    }

    private void sendUnReadMessage(Long userId, WebSocketSession webSocketSession) {

    }

    private void sendClientId(Long userId, WebSocketSession webSocketSession) throws IOException {
        // 初始化发送最大clientId
        GlobalWsMap.PC_SESSION.put(userId, webSocketSession);
        Map<Integer, Long> collect = AbstractMessage.DeviceType.getTypeMap().keySet().stream()
                .collect(Collectors.toMap(Function.identity(), device -> messageBuffer.getMaxClientId(userId, device), (v1, v2) -> v1));
        WsMessageDTO wsMessageDTO = new WsMessageDTO();
        wsMessageDTO.setMessageType(AbstractMessage.MessageType.INIT.getCode());
        wsMessageDTO.setMessage(JSON.toJSONString(collect));
        // TODO：没有ack就一直传
        webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(wsMessageDTO)));
    }
}

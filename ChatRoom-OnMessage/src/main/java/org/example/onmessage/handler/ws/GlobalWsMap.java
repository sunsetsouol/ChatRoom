package org.example.onmessage.handler.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@Slf4j
public class GlobalWsMap {

    public final static ConcurrentHashMap<Long, WebSocketSession> PC_SESSION = new ConcurrentHashMap<>();
    public final static ConcurrentHashMap<Long, WebSocketSession> MOBILE_SESSION = new ConcurrentHashMap<>();

    private final static ArrayList<ConcurrentHashMap<Long, WebSocketSession>> SESSION_MAP = new ArrayList<>();
    static {
        SESSION_MAP.add(PC_SESSION);
        SESSION_MAP.add(MOBILE_SESSION);
    }


    public static Boolean sendText(Long userId, String message) {
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        SESSION_MAP.forEach(sessionMap -> {
            WebSocketSession webSocketSession = sessionMap.get(userId);
            if (Objects.nonNull(webSocketSession) && webSocketSession.isOpen()) {
                try {
                    webSocketSession.sendMessage(new TextMessage(message));
                    flag.set(true);
                } catch (IOException e) {
                    log.error("发送消息失败", e);
                }
            }
        });
        return flag.get();
//        WebSocketSession webSocketSession = PC_SESSION.get(userId);
//        if (Objects.isNull(webSocketSession) || !webSocketSession.isOpen()) {
//            log.error("用户{}未连接", userId);
//            return false;
//        }
//        try {
//            webSocketSession.sendMessage(new TextMessage(message));
//            return true;
//        } catch (IOException e) {
//            log.error("发送消息失败", e);
//            return false;
//        }

    }
}

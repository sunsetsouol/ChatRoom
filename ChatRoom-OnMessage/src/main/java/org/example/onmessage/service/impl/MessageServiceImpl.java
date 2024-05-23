package org.example.onmessage.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.pojo.dto.WsMessageDTO;
import org.example.onmessage.route.DownLinkMessageRoute;
import org.example.onmessage.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final DownLinkMessageRoute downLinkMessageRoute;
    @Override
    public void accept(WsMessageDTO wsMessageDTO) {
        // 参数校验
        wsMessageDTO.validate();

        // 消息下行推送
        downLinkMessageRoute.downLinkMessagePush(wsMessageDTO);

    }
}

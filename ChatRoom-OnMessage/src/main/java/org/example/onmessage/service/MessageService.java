package org.example.onmessage.service;

import org.example.onmessage.entity.dto.WsMessageDTO;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
public interface MessageService {
    void accept(WsMessageDTO wsMessageDTO);
}

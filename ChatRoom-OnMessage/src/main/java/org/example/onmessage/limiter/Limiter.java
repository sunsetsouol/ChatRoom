package org.example.onmessage.limiter;

import org.example.pojo.dto.WsMessageDTO;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/11
 */
public interface Limiter {
    boolean limiter(WsMessageDTO wsMessageDTO);
}

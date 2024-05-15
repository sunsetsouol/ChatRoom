package org.example.onmessage.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.dto.WsMessageDTO;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class MessageBO extends WsMessageDTO {

    private Long id;


    public static MessageBO getAckMessage(MessageBO messageBO) {
        MessageBO copy = new MessageBO();
        copy.setClientMessageId(messageBO.getClientMessageId());
        copy.setDevice(messageBO.getDevice());
        copy.setMessageType(MessageType.BUSINESS_ACK.getCode());
        return copy;
    }
}
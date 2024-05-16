package org.example.onmessage.adapter;

import org.example.onmessage.entity.AbstractMessage;
import org.example.onmessage.entity.dto.WsMessageDTO;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
public class MessageAdapter {

    public static WsMessageDTO getAck1Message(WsMessageDTO wsMessageDTO) {
        return getAckWsMessageDTO(wsMessageDTO, AbstractMessage.MessageType.ACK);
    }

    public static WsMessageDTO getBusinessAckMessage(WsMessageDTO wsMessageDTO){
        return getAckWsMessageDTO(wsMessageDTO, AbstractMessage.MessageType.BUSINESS_ACK);
    }

    private static WsMessageDTO getAckWsMessageDTO(WsMessageDTO wsMessageDTO, AbstractMessage.MessageType ack) {
        WsMessageDTO copy = new WsMessageDTO();
        copy.setDevice(wsMessageDTO.getDevice());
        copy.setClientMessageId(wsMessageDTO.getClientMessageId());
        copy.setMessageType(ack.getCode());
        return copy;
    }

    public static WsMessageDTO getUnreadAckMessage(WsMessageDTO wsMessageDTO) {
        return getAckWsMessageDTO(wsMessageDTO, AbstractMessage.MessageType.GET_MESSAGE_ACK);
    }
}

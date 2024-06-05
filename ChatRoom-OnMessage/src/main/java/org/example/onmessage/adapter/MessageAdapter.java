package org.example.onmessage.adapter;

import org.example.pojo.AbstractMessage;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.WsMessageDTO;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/13
 */
public class MessageAdapter {

    public static WsMessageDTO getAck1Message(WsMessageDTO wsMessageDTO) {
        return getAckWsMessageDTO(wsMessageDTO, AbstractMessage.MessageType.ACK);
    }

    public static MessageBO getBusinessAckMessage(MessageBO messageBO){
        MessageBO copy = new MessageBO();
        copy.setClientMessageId(messageBO.getClientMessageId());
        copy.setMessageType(AbstractMessage.MessageType.SERVER_ACK.getCode());
        copy.setDevice(messageBO.getDevice());
        copy.setId(messageBO.getId());
        return copy;
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

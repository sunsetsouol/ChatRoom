package org.example.onmessage.entity.dto;

import lombok.*;
import org.example.exception.BusinessException;
import org.example.onmessage.entity.AbstractMessage;
import org.example.pojo.vo.ResultStatusEnum;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessageDTO extends AbstractMessage {

    /**
     * 客户端消息id
     */
    private Long clientMessageId;

    /**
     * 发送者id
     */
    private Long fromUserId;

    /**
     * 发送目标的id，userId或groupId
     */
    private Long targetId;

    /**
     * 客户端时间
     */
    private Long clientTime;

    /**
     * 消息类型
     * {@link MetaDataType#code}
     */
    private Integer metaDataType;

    /**
     * 文本
     */
    private String message;

    /**
     * 二进制数据
     */
    private byte[] byteArray;




    @AllArgsConstructor
    @Getter
    public enum MetaDataType {
        TEXT(1, "文本"),
        IMAGE(2, "图片"),
        FILE(3, "文件"),
        VOICE(4, "语音"),
        VIDEO(5, "视频")
        ;
        private final Integer code;
        private final String type;
        private static Map<Integer, String> typeMap = new HashMap<>();

        static {
            for (MetaDataType type : MetaDataType.values()) {
                typeMap.put(type.code, type.type);
            }
        }

        public static Map<Integer, String> getTypeMap() {
            return typeMap;
        }

        public static String getType(Integer code) {
            return typeMap.get(code);
        }

    }
    public void validate() {
        if (metaDataType.equals(MetaDataType.TEXT.getCode())) {
            if (!StringUtils.hasText(message)) {
                throw new BusinessException(ResultStatusEnum.EMPTY_MESSAGE_TEXT);
            }
        } else {
            if (Objects.isNull(byteArray)) {
                throw new BusinessException(ResultStatusEnum.EMPTY_MESSAGE_FILE);
            }
        }
        if (Objects.isNull(fromUserId)) {
            throw new BusinessException(ResultStatusEnum.FROM_USER_ID_EMPTY);
        }
        if (this.getMessageType().equals(AbstractMessage.MessageType.SINGLE.getCode())) {
            if (Objects.isNull(targetId)) {
                throw new BusinessException(ResultStatusEnum.RECEIVER_USER_ID_EMPTY);
            }
        }else if (this.getMessageType().equals(AbstractMessage.MessageType.GROUP.getCode())) {
            if (Objects.isNull(targetId)) {
                throw new BusinessException(ResultStatusEnum.GROUP_ID_EMPTY);
            }
        }
        if (Objects.isNull(getDevice())) {
            throw new BusinessException(ResultStatusEnum.DEVICE_EMPTY);
        }
//        if (getMessageType().equals(MessageType.SINGLE.getCode())){
//            if (fromUserId == targetId){
//                throw new BusinessException(ResultStatusEnum.SEND_TO_SELF);
//            }
//        }

    }
}

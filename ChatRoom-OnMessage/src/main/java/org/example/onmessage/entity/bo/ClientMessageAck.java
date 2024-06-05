//package org.example.onmessage.entity.bo;
//
//import lombok.*;
//import org.example.pojo.AbstractMessage;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/5/12
// */
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//@Builder
//public class ClientMessageAck {
//    private Long clientMessageId;
//    /**
//     * 设备
//     * {@link AbstractMessage.DeviceType#getCode()}
//     */
//    private Integer device;
//
//    /**
//     * ack类型
//     * {@link AckType#code}
//     */
//    private Integer ackType;
//
//    private Boolean isAck;
//
//    @AllArgsConstructor
//    @Getter
//    public enum AckType {
//        FIRST_ACK(1),
//        SERVER_ACK(2)
//        ;
//        private final Integer code;
//
//    }
//
//}

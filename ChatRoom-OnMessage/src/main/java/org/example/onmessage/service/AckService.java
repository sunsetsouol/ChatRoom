package org.example.onmessage.service;

import org.example.pojo.bo.MessageBO;

import java.util.Set;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/5
 */
public interface AckService {
    Set<String> getUnAcked(MessageBO messageBO);


    void deleteAck(MessageBO messageBO);

    long setBusinessAck(MessageBO messageBO, Set<String> userIds);


    boolean ack(MessageBO messageId, Set<String> userIds);
}

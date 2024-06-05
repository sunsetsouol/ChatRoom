package org.example.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/6/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BusinessAckMessage {
    private MessageBO message;
    private Set<String> userIds;
}

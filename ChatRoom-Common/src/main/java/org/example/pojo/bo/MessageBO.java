package org.example.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pojo.dto.WsMessageDTO;

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


}

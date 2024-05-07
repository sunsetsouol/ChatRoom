package org.example.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class UserBO {

    private Long userId;

    private String username;

    private String phone;

    private String avatar;


}

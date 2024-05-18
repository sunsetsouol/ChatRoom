package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomCreateDTO {

    private String name;

    private String description;

    private String avatar;
}

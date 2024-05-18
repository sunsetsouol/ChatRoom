package org.example.user.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.RoomCreateDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2024-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_room")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 聊天室名字
     */
    private String roomName;

    /**
     * 描述
     */
    private String description;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 成员人数上限
     */
    private Integer memberLimit;

    /**
     * 群主id
     */
    private Long ownerId;

    /**
     * 群聊人数
     */
    private Integer memberCount;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic(delval = "NULL")
    private Integer deleted;


    public Room(RoomCreateDTO roomCreateDTO, UserBO userBO) {
        this.roomName = roomCreateDTO.getName();
        this.description = roomCreateDTO.getDescription();
        this.avatar = userBO.getAvatar();
        this.ownerId = userBO.getId();
    }
}

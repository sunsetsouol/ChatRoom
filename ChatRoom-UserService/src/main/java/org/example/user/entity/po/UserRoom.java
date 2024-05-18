package org.example.user.entity.po;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@TableName("t_user_room")
public class UserRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 身份（0普通成员，1管理员，2群主，-1退出群聊）
     * {@link IdentityEnum}
     */
    private Integer identity;

    /**
     * 加入时间
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


    public UserRoom(Long userId, Long roomId, Integer identity) {
        this.userId = userId;
        this.roomId = roomId;
        this.identity = identity;
    }

    @AllArgsConstructor
    @Getter
    public enum IdentityEnum {
        MEMBER(0),
        ADMIN(1),
        OWNER(2),
        QUIT(-1);

        private final Integer identity;


    }
}

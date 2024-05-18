package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.user.entity.po.UserRoom;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
public interface UserRoomService extends IService<UserRoom> {

    Boolean addUserRoom(Long userId, Long roomId, UserRoom.IdentityEnum identityEnum);
}

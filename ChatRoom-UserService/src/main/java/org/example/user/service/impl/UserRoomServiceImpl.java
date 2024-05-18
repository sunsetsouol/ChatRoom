package org.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.constant.RedisCacheConstants;
import org.example.user.entity.po.UserRoom;
import org.example.user.mapper.RoomMapper;
import org.example.user.mapper.UserRoomMapper;
import org.example.user.service.UserRoomService;
import org.example.user.service.common.RedisCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
@Service
@RequiredArgsConstructor
public class UserRoomServiceImpl extends ServiceImpl<UserRoomMapper, UserRoom> implements UserRoomService {
    private final UserRoomMapper userRoomMapper;
    private final RoomMapper roomMapper;
    private final RedisCacheService redisCacheService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addUserRoom(Long userId, Long roomId, UserRoom.IdentityEnum identityEnum) {
        UserRoom userRoom = new UserRoom(userId, roomId, identityEnum.getIdentity());
        userRoomMapper.insert(userRoom);
        if (roomMapper.incrementMember(roomId) == 1) {
            redisCacheService.setCacheSet(RedisCacheConstants.ROOM_MEMBER + roomId, userId);
            return true;
        }
        return false;
    }
}

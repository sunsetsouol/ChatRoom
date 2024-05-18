package org.example.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.RoomCreateDTO;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.dto.UserDTO;
import org.example.user.entity.po.Room;
import org.example.user.entity.po.UserRoom;
import org.example.user.mapper.RoomMapper;
import org.example.user.service.RoomService;
import org.example.user.service.UserRoomService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
    private final RoomMapper roomMapper;
    private final UserRoomService userRoomService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createRoom(RoomCreateDTO roomCreateDTO) {
        UserBO userBO = getUserBO();
        Room room = new Room(roomCreateDTO, userBO);
        roomMapper.insert(room);

        return userRoomService.addUserRoom(userBO.getId(), room.getId(), UserRoom.IdentityEnum.OWNER);
    }

    private static UserBO getUserBO() {
        UserAuthority principal = (UserAuthority) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO user = principal.getUser();
        UserBO userBO = JSON.parseObject(user.getUsername(), UserBO.class);
        return userBO;
    }

    @Override
    public Boolean joinRoom(Long roomId) {
        UserBO userBO = getUserBO();
        return userRoomService.addUserRoom(userBO.getId(), roomId, UserRoom.IdentityEnum.MEMBER);
    }
}

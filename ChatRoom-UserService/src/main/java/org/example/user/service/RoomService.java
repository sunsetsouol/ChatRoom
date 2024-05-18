package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.dto.RoomCreateDTO;
import org.example.user.entity.po.Room;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
public interface RoomService extends IService<Room> {
    Boolean createRoom(RoomCreateDTO roomCreateDTO);

    Boolean joinRoom(Long roomId);
}

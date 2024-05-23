package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pojo.dto.RoomCreateDTO;
import org.example.pojo.vo.Result;
import org.example.user.service.RoomService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天室
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/17
 */
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    /**
     * 公共资源
     * @return r1
     */
    @GetMapping("/r1")
    public String r1(){
        return "r1";
    }

    /**
     * 创建聊天室
     * @param roomCreateDTO 创建聊天室参数
     * @return 是否创建成功
     */
    @PostMapping("/create")
    public Result<Boolean> createRoom(@RequestBody @Validated RoomCreateDTO roomCreateDTO) {
        return Result.success(roomService.createRoom(roomCreateDTO));
    }

    /**
     * 加入聊天室
     * @param roomId 房间id
     * @return 是否加入成功
     */
    @PostMapping("/join")
    public Result<Boolean> joinRoom(@RequestParam @Validated Long roomId) {
        return Result.success(roomService.joinRoom(roomId));
    }
}

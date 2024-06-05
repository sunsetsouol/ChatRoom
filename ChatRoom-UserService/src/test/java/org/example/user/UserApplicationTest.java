package org.example.user;

import cn.hutool.crypto.digest.BCrypt;
import com.alibaba.fastjson.JSON;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.vo.Result;
import org.example.user.entity.po.Room;
import org.example.user.mapper.RoomMapper;
import org.example.user.service.RoomService;
import org.example.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/5
 */
@SpringBootTest
public class UserApplicationTest {
    @Autowired
    private RoomService roomService;
    @Test
    public void contextLoads() {
        Room room = new Room();
        room.setOwnerId(1L);
        room.setRoomName("a");
        Room room1 = new Room();
        room1.setOwnerId(1L);
        room1.setRoomName("a");
        List<Room> list = new ArrayList<>();
        list.add(room);
        list.add(room1);
        roomService.saveBatch(list);
        for (Room room2 : list) {
            System.out.println(room2);
        }
//        System.out.println(BCrypt.hashpw("123456"));
//        String s = JwtUtil.parseJwt("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTUwMjA0NjQsInVzZXJfbmFtZSI6IntcImF2YXRhclwiOlwiaHR0cHM6Ly9zdW5zZXRzb3VvbC5vc3MtY24tZ3Vhbmd6aG91LmFsaXl1bmNzLmNvbS9waWMvMjAyNDA1MDMwOTA1MjcyLmpwZ1wiLFwicGhvbmVcIjpcIm5hbWVcIixcInVzZXJuYW1lXCI6XCJuYW1lXCJ9IiwiYXV0aG9yaXRpZXMiOlsidXNlciJdLCJqdGkiOiIwNmIwZjgwNS1kZDliLTRlZDQtOGY1YS1jYzZlZWE0NzAzNWEiLCJjbGllbnRfaWQiOiJjbGllbnQiLCJzY29wZSI6WyJyZWFkOnVzZSJdfQ.cgqjG9cz-vh7qsZANqI3EAscDWYMZ6nKdPjvBPH5NQA");
//        System.out.println(s);


    }
}

package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.vo.Result;
import org.example.user.entity.dto.UserRegisterDTO;
import org.example.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据手机号获取用户权限（内部接口）
     * @param phone 手机号
     * @return 用户权限
     */
    @GetMapping("/inner/getAuthority")
    public Result<UserAuthority> getAuthorityByPhone(@RequestParam("phone") String phone) {
        return Result.success(userService.getUserAuthorityByPhone(phone));
    }

    /**
     * 注册
     * @param userRegisterDTO 用户注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody @Validated UserRegisterDTO userRegisterDTO) {
        return Result.success(userService.register(userRegisterDTO));
    }

    @GetMapping("test")
    public String test() {
        return "test";
    }
}

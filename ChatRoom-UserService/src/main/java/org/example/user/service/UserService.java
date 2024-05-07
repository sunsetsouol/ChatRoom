package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.dto.UserAuthority;
import org.example.user.entity.dto.UserRegisterDTO;
import org.example.user.entity.po.User;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
public interface UserService extends IService<User> {

    UserAuthority getUserAuthorityByPhone(String phone);

    Boolean register(UserRegisterDTO userRegisterDTO);
}

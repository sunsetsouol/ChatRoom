package org.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.vo.ResultStatusEnum;
import org.example.user.entity.dto.UserRegisterDTO;
import org.example.user.entity.po.User;
import org.example.user.mapper.UserMapper;
import org.example.user.service.RoleService;
import org.example.user.service.UserRoleService;
import org.example.user.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final RoleService roleService;

    @Override
    public UserAuthority getUserAuthorityByPhone(String phone) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (Objects.isNull(user)){
            throw new BusinessException(ResultStatusEnum.USER_NOT_EXIST);
        }
        // 用户信息
        UserBO userBO = BeanUtil.copyProperties(user, UserBO.class);

        UserDTO userDTO = UserDTO
                .builder()
                .username(JSON.toJSONString(userBO))
                .password(user.getPassword())
                .build();

        // 权限信息
        List<Long> roleIdsByUserId = userRoleService.getRoleIdsByUserId(user.getId());
        List<String> permissionByRoleIds = roleService.getPermissionByRoleIds(roleIdsByUserId);


        return UserAuthority
                .builder()
                .user(userDTO)
                .permissions(permissionByRoleIds)
                .build();
    }

    @Override
    public Boolean register(UserRegisterDTO userRegisterDTO) {
        User user = BeanUtil.copyProperties(userRegisterDTO, User.class);
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ResultStatusEnum.PHONE_ALREADY_REGISTERED);
        }
        return true;
    }
}

package org.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.user.entity.po.UserRole;
import org.example.user.mapper.UserRoleMapper;
import org.example.user.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return lambdaQuery().eq(UserRole::getUserId, userId).list().stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }
}

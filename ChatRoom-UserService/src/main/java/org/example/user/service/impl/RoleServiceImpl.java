package org.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.user.entity.po.Role;
import org.example.user.mapper.RoleMapper;
import org.example.user.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService{
    @Override
    public List<String> getPermissionByRoleIds(List<Long> roleIds) {
        return roleIds.isEmpty() ? new ArrayList<>() : lambdaQuery().in(Role::getId, roleIds).select(Role::getPermissions).list().stream().map(Role::getPermissions).collect(Collectors.toList());
    }
}

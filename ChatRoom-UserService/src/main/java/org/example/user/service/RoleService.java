package org.example.user.service;

import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
public interface RoleService {

    List<String> getPermissionByRoleIds(List<Long> roleIds);
}

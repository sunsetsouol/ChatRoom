package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.user.entity.po.UserRole;

import java.util.List;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
public interface UserRoleService extends IService<UserRole> {
    List<Long> getRoleIdsByUserId(Long userId);
}

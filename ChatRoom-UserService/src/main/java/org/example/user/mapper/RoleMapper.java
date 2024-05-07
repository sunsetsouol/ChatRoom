package org.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.user.entity.po.Role;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}

package org.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.user.entity.po.User;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

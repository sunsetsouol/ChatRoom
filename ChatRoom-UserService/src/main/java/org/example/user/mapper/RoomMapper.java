package org.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.user.entity.po.Room;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2024-05-17
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    @Update("update t_room set member_count = member_count + 1 where id = #{roomId} and member_count < member_limit and deleted is not null")
    int incrementMember(Long roomId);
}

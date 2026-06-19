package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    void update(User user);

    Integer countByMap(Map map);

    @Update("update user set rider_status=#{status} where id=#{id}")
    void updateRiderStatus(@Param("id") Integer id, @Param("status") Integer status);
}

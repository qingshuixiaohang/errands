package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    @Select("select * from user where rider_status > 0")
    List<fun.cyhgraph.entity.User> getRiders();

    @Select("<script>select * from user <where> <if test=\'name != null and name != \"\"\'>and name like concat(\"%\",#{name},\"%\")</if> <if test=\'phone != null and phone != \"\"\'>and phone like concat(\"%\",#{phone},\"%\")</if> </where> order by create_time desc</script>")
    List<User> getAllUsers(@Param("name") String name, @Param("phone") String phone);
}


package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.RiderSchedule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RiderScheduleMapper {

    @Insert("insert into rider_schedule(rider_id, weekday, start_time, end_time, location, is_active) " +
            "values(#{riderId}, #{weekday}, #{startTime}, #{endTime}, #{location}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RiderSchedule riderSchedule);

    @Update("update rider_schedule set weekday=#{weekday}, start_time=#{startTime}, end_time=#{endTime}, " +
            "location=#{location}, is_active=#{isActive} where id=#{id}")
    void update(RiderSchedule riderSchedule);

    @Delete("delete from rider_schedule where id=#{id}")
    void delete(Integer id);

    @Select("select * from rider_schedule where rider_id=#{riderId} and is_active=1 order by weekday, start_time")
    List<RiderSchedule> findByRiderId(Integer riderId);

    @Select("select * from rider_schedule where weekday=#{weekday} and start_time<=#{timeStr} " +
            "and end_time>=#{timeStr} and is_active=1")
    List<RiderSchedule> findActiveByTime(@Param("weekday") Integer weekday, @Param("timeStr") String timeStr);
}

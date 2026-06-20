package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.AiAssignmentLog;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AiAssignmentLogMapper {

    @Insert("insert into ai_assignment_log(order_id, candidates, selected_rider_id, selection_method, reason, " +
            "rider_response, response_time) " +
            "values(#{orderId}, #{candidates}, #{selectedRiderId}, #{selectionMethod}, #{reason}, " +
            "#{riderResponse}, #{responseTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AiAssignmentLog log);

    @Select("select * from ai_assignment_log where order_id = #{orderId}")
    AiAssignmentLog getByOrderId(Integer orderId);

    @Update("update ai_assignment_log set rider_response=#{riderResponse}, response_time=#{responseTime} " +
            "where order_id=#{orderId}")
    void updateResponse(@Param("orderId") Integer orderId,
                        @Param("riderResponse") String riderResponse,
                        @Param("responseTime") LocalDateTime responseTime);

    @Select("select * from ai_assignment_log where rider_response='pending' " +
            "and assignment_time < #{deadline}")
    List<AiAssignmentLog> findPendingBefore(LocalDateTime deadline);

    @Select("select al.*, o.number as order_number, o.amount as order_amount, o.address, u.name as rider_name " +
            "from ai_assignment_log al " +
            "left join orders o on al.order_id = o.id " +
            "left join user u on al.selected_rider_id = u.id " +
            "order by al.assignment_time desc")
    List<java.util.Map<String, Object>> getLogsWithDetails();
}

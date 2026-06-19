package fun.cyhgraph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderSchedule implements Serializable {
    private Integer id;
    private Integer riderId;
    private Integer weekday;       // 1-7 周一到周日
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;       // 常驻区域
    private Integer isActive;
    private LocalDateTime createTime;
}

package fun.cyhgraph.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalTime;

@Data
public class RiderScheduleDTO implements Serializable {
    private Integer riderId;
    private Integer weekday;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
}

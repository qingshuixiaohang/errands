package fun.cyhgraph.service;

import fun.cyhgraph.entity.RiderSchedule;
import java.util.List;

public interface RiderScheduleService {
    void save(Integer riderId, Integer weekday, String startTime, String endTime, String location);
    void update(Integer id, Integer weekday, String startTime, String endTime, String location);
    void delete(Integer id);
    List<RiderSchedule> list(Integer riderId);
}

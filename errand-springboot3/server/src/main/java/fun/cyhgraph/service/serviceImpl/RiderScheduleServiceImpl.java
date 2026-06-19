package fun.cyhgraph.service.serviceImpl;

import fun.cyhgraph.entity.RiderSchedule;
import fun.cyhgraph.mapper.RiderScheduleMapper;
import fun.cyhgraph.service.RiderScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class RiderScheduleServiceImpl implements RiderScheduleService {

    @Autowired
    private RiderScheduleMapper riderScheduleMapper;

    @Transactional
    public void save(Integer riderId, Integer weekday, String startTime, String endTime, String location) {
        RiderSchedule schedule = RiderSchedule.builder()
                .riderId(riderId)
                .weekday(weekday)
                .startTime(LocalTime.parse(startTime))
                .endTime(LocalTime.parse(endTime))
                .location(location)
                .isActive(1)
                .build();
        riderScheduleMapper.insert(schedule);
        log.info("骑手排班已添加: riderId={}, weekday={}", riderId, weekday);
    }

    @Transactional
    public void update(Integer id, Integer weekday, String startTime, String endTime, String location) {
        RiderSchedule schedule = riderScheduleMapper.findByRiderId(id).stream()
                .filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        if (schedule != null) {
            schedule.setWeekday(weekday);
            schedule.setStartTime(LocalTime.parse(startTime));
            schedule.setEndTime(LocalTime.parse(endTime));
            schedule.setLocation(location);
            riderScheduleMapper.update(schedule);
        }
    }

    @Transactional
    public void delete(Integer id) {
        riderScheduleMapper.delete(id);
    }

    public List<RiderSchedule> list(Integer riderId) {
        return riderScheduleMapper.findByRiderId(riderId);
    }
}

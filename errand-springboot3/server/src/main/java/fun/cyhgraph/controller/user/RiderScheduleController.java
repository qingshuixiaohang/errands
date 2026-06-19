package fun.cyhgraph.controller.user;

import fun.cyhgraph.dto.RiderScheduleDTO;
import fun.cyhgraph.entity.RiderSchedule;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.RiderScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("userRiderScheduleController")
@RequestMapping("/user/rider/schedule")
@Slf4j
public class RiderScheduleController {

    @Autowired
    private RiderScheduleService riderScheduleService;

    @PostMapping
    public Result<String> save(@RequestBody RiderScheduleDTO dto) {
        log.info("添加空闲时间: {}", dto);
        riderScheduleService.save(dto.getRiderId(), dto.getWeekday(),
                dto.getStartTime().toString(), dto.getEndTime().toString(), dto.getLocation());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        log.info("删除空闲时间: id={}", id);
        riderScheduleService.delete(id);
        return Result.success();
    }

    @GetMapping("/list/{riderId}")
    public Result<List<RiderSchedule>> list(@PathVariable Integer riderId) {
        List<RiderSchedule> list = riderScheduleService.list(riderId);
        return Result.success(list);
    }
}

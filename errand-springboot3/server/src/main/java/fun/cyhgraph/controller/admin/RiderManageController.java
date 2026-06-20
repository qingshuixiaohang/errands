package fun.cyhgraph.controller.admin;

import fun.cyhgraph.entity.Order;
import fun.cyhgraph.entity.User;
import fun.cyhgraph.mapper.AiAssignmentLogMapper;
import fun.cyhgraph.mapper.OrderMapper;
import fun.cyhgraph.mapper.UserMapper;
import fun.cyhgraph.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/rider")
@Slf4j
public class RiderManageController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AiAssignmentLogMapper aiAssignmentLogMapper;

    /** 骑手列表 */
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> riders = userMapper.getRiders();
        return Result.success(riders);
    }

    /** 某个骑手的订单 */
    @GetMapping("/{riderId}/orders")
    public Result<List<Order>> riderOrders(@PathVariable Integer riderId,
                                           @RequestParam(required = false) Integer status) {
        List<Order> orders = orderMapper.getByRiderId(riderId);
        if (status != null) {
            orders = orders.stream().filter(o -> o.getStatus().equals(status)).toList();
        }
        return Result.success(orders);
    }

    /** 骑手统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        List<User> riders = userMapper.getRiders();
        int total = riders.size();
        int idle = (int) riders.stream().filter(r -> r.getRiderStatus() != null && r.getRiderStatus() == 1).count();
        int delivering = total - idle;
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("idle", idle);
        stats.put("delivering", delivering);
        return Result.success(stats);
    }

    /** 派单记录 */
    @GetMapping("/logs")
    public Result<List<Map<String, Object>>> logs() {
        List<Map<String, Object>> logs = aiAssignmentLogMapper.getLogsWithDetails();
        return Result.success(logs);
    }

    /** 取消骑手 */
    @PutMapping("/{riderId}/removeRider")
    public Result removeRider(@PathVariable Integer riderId) {
        log.info("后台取消骑手: riderId={}", riderId);
        userMapper.updateRiderStatus(riderId, 0);
        return Result.success();
    }
}

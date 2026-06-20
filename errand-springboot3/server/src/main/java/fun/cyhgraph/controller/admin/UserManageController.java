package fun.cyhgraph.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.entity.Order;
import fun.cyhgraph.entity.User;
import fun.cyhgraph.mapper.OrderMapper;
import fun.cyhgraph.mapper.UserMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserManageController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    /** 用户列表（分页） */
    @GetMapping("/list")
    public Result<PageResult> list(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int pageSize,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String phone) {
        try {
            PageHelper.startPage(page, pageSize);
            List<User> users = userMapper.getAllUsers(name, phone);
            Page<User> p = (Page<User>) users;
            return Result.success(new PageResult(p.getTotal(), p.getResult()));
        } finally {
            PageHelper.clearPage();
        }
    }

    /** 某个用户的订单 */
    @GetMapping("/{userId}/orders")
    public Result<List<Order>> userOrders(@PathVariable Integer userId) {
        List<Order> orders = orderMapper.getByUserId(userId);
        return Result.success(orders);
    }

    /** 设为骑手 */
    @PutMapping("/{userId}/setRider")
    public Result setRider(@PathVariable Integer userId) {
        log.info("后台授权骑手: userId={}", userId);
        userMapper.updateRiderStatus(userId, 1);
        return Result.success();
    }

    /** 取消骑手 */
    @PutMapping("/{userId}/removeRider")
    public Result removeRider(@PathVariable Integer userId) {
        log.info("后台取消骑手: userId={}", userId);
        userMapper.updateRiderStatus(userId, 0);
        return Result.success();
    }
}

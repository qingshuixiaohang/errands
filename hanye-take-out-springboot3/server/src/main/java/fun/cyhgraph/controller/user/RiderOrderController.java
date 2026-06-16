package fun.cyhgraph.controller.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.dto.OrderConfirmDTO;
import fun.cyhgraph.dto.OrderPageDTO;
import fun.cyhgraph.entity.Order;
import fun.cyhgraph.mapper.OrderMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.OrderService;
import fun.cyhgraph.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("userRiderOrderController")
@RequestMapping("/user/rider")
@Slf4j
public class RiderOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 骑手接单
     * @param id 订单id
     * @return
     */
    @PutMapping("/accept/{id}")
    public Result accept(@PathVariable Integer id){
        log.info("骑手接单，订单id：{}", id);
        OrderConfirmDTO orderConfirmDTO = new OrderConfirmDTO();
        orderConfirmDTO.setId(id);
        orderService.confirm(orderConfirmDTO);
        return Result.success();
    }

    /**
     * 骑手开始配送
     * @param id 订单id
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Integer id){
        log.info("骑手开始配送，订单id：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 骑手确认送达
     * @param id 订单id
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Integer id){
        log.info("骑手送达完成，订单id：{}", id);
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 骑手查看所有订单（不按用户过滤，骑手需要看到所有待接单/配送中的订单）
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/orders")
    public Result<PageResult> getAllOrders(int page, int pageSize, Integer status) {
        log.info("骑手查看订单列表，page:{}, pageSize:{}, status:{}", page, pageSize, status);
        PageHelper.startPage(page, pageSize);
        OrderPageDTO orderPageDTO = new OrderPageDTO();
        // 不设置userId，查询所有订单
        orderPageDTO.setStatus(status);
        Page<Order> orders = orderMapper.page(orderPageDTO);
        // 转换为VO
        List<OrderVO> orderVOList = new ArrayList<>();
        if (orders != null && orders.getTotal() > 0) {
            for (Order order : orders) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVOList.add(orderVO);
            }
        }
        return Result.success(new PageResult(orders.getTotal(), orderVOList));
    }
}

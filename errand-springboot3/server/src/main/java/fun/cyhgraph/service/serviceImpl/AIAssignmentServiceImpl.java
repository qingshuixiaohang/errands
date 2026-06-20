package fun.cyhgraph.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fun.cyhgraph.entity.*;
import fun.cyhgraph.exception.OrderBusinessException;
import fun.cyhgraph.mapper.*;
import fun.cyhgraph.service.AIAssignmentService;
import fun.cyhgraph.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIAssignmentServiceImpl implements AIAssignmentService {

    @Autowired private OrderMapper orderMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private RiderScheduleMapper riderScheduleMapper;
    @Autowired private AiAssignmentLogMapper aiAssignmentLogMapper;
    @Autowired private WebSocketServer webSocketServer;
    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public void dispatch(Integer orderId) {
        log.info("AI派单开始: orderId={}", orderId);
        Order order = orderMapper.getById(orderId);
        if (order == null) {
            log.warn("订单不存在: orderId={}", orderId);
            return;
        }
        if (order.getStatus() != Order.TO_BE_CONFIRMED) {
            log.warn("订单状态不允许AI派单: status={}", order.getStatus());
            return;
        }

        // 1. 查当前空闲骑手
        List<Map<String, Object>> candidates = findAvailableRiders();
        if (candidates.isEmpty()) {
            log.info("无空闲骑手，保持待接单状态");
            return;
        }

        // 2. 评分排序
        for (Map<String, Object> r : candidates) {
            double rating = ((Number) r.getOrDefault("rating", 5.0)).doubleValue();
            int totalOrders = ((Number) r.getOrDefault("total_orders", 0)).intValue();
            r.put("score", rating * 40.0 + totalOrders * 0.1);
        }
        candidates.sort((a, b) -> Double.compare(
                ((Number) b.get("score")).doubleValue(),
                ((Number) a.get("score")).doubleValue()));

        // 3. 选择骑手
        Integer selectedId; String reason; String method;
        if (candidates.size() == 1) {
            selectedId = (Integer) candidates.get(0).get("id");
            reason = "唯一空闲骑手";
            method = "direct";
        } else {
            double top = ((Number) candidates.get(0).get("score")).doubleValue();
            double second = ((Number) candidates.get(1).get("score")).doubleValue();
            if (second == 0 || (top - second) / second > 0.1) {
                selectedId = (Integer) candidates.get(0).get("id");
                reason = "评分最优，得分" + String.format("%.1f", top);
                method = "direct";
            } else {
                // 评分接近时调AI模块
                try {
                    JSONObject aiResult = callAiRecommendation(order, candidates);
                    selectedId = aiResult.getInteger("riderId");
                    reason = aiResult.getString("reason");
                    method = "ai_assist";
                } catch (Exception e) {
                    log.warn("AI推荐不可用，取评分最高: {}", e.getMessage());
                    selectedId = (Integer) candidates.get(0).get("id");
                    reason = "AI不可用，评分最优";
                    method = "direct";
                }
            }
        }

        // 4. 更新订单
        order.setAssignedRiderId(selectedId);
        order.setAssignmentType(1);
        order.setStatus(Order.AI_ASSIGNING);
        orderMapper.update(order);

        // 5. 记录日志
        AiAssignmentLog logEntry = AiAssignmentLog.builder()
                .orderId(orderId)
                .candidates(JSON.toJSONString(candidates))
                .selectedRiderId(selectedId)
                .selectionMethod(method)
                .reason(reason)
                .riderResponse("pending")
                .assignmentTime(LocalDateTime.now())
                .build();
        aiAssignmentLogMapper.insert(logEntry);

        // 6. WebSocket 通知骑手
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "ai_assign");
        msg.put("orderId", orderId);
        msg.put("riderId", selectedId);
        msg.put("content", "您有新的AI派单");
        webSocketServer.sendToAllClient(JSON.toJSONString(msg));

        log.info("AI派单完成: orderId={}, riderId={}, method={}, reason={}",
                orderId, selectedId, method, reason);
    }

    @Transactional
    public void acceptAssignment(Integer orderId, Integer riderId) {
        log.info("骑手接受派单: orderId={}, riderId={}", orderId, riderId);
        Order order = orderMapper.getById(orderId);
        if (order == null) {
            log.warn("订单不存在: orderId={}", orderId);
            return;
        }
        if (order.getStatus() != Order.AI_ASSIGNING) {
            log.warn("订单状态异常, 当前status={}, 需要status=7", order.getStatus());
            return;
        }
        order.setStatus(Order.CONFIRMED);
        orderMapper.update(order);
        userMapper.updateRiderStatus(riderId, 2);
        aiAssignmentLogMapper.updateResponse(orderId, "accepted", LocalDateTime.now());

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "rider_accepted");
        msg.put("orderId", orderId);
        msg.put("riderId", riderId);
        msg.put("content", "骑手已接单");
        webSocketServer.sendToAllClient(JSON.toJSONString(msg));
    }

    @Transactional
    public void rejectAssignment(Integer orderId) {
        log.info("骑手拒绝派单: orderId={}", orderId);
        Order order = orderMapper.getById(orderId);
        if (order == null) {
            log.warn("订单不存在: orderId={}", orderId);
            return;
        }
        if (order.getStatus() != Order.AI_ASSIGNING) {
            log.warn("订单状态异常, 当前status={}, 需要status=7", order.getStatus());
            return;
        }
        order.setStatus(Order.TO_BE_CONFIRMED);
        order.setAssignedRiderId(null);
        order.setAssignmentType(0);
        orderMapper.update(order);
        aiAssignmentLogMapper.updateResponse(orderId, "rejected", LocalDateTime.now());
    }

    @Transactional
    public void checkTimeout() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(1);
        List<AiAssignmentLog> pending = aiAssignmentLogMapper.findPendingBefore(deadline);
        for (AiAssignmentLog logEntry : pending) {
            try {
                Order order = orderMapper.getById(logEntry.getOrderId());
                if (order != null && order.getStatus() == Order.AI_ASSIGNING) {
                    order.setAssignedRiderId(null);
                    order.setAssignmentType(0);
                    order.setStatus(Order.TO_BE_CONFIRMED);
                    orderMapper.update(order);
                    aiAssignmentLogMapper.updateResponse(
                            logEntry.getOrderId(), "timeout", LocalDateTime.now());
                    log.info("派单超时: orderId={}", logEntry.getOrderId());
                }
            } catch (Exception e) {
                log.error("超时处理异常", e);
            }
        }
    }

    private List<Map<String, Object>> findAvailableRiders() {
        DayOfWeek dow = LocalDateTime.now().getDayOfWeek();
        LocalTime now = LocalTime.now();
        List<RiderSchedule> schedules = riderScheduleMapper.findActiveByTime(
                dow.getValue(), now.toString());
        Set<Integer> riderIds = schedules.stream()
                .map(RiderSchedule::getRiderId).collect(Collectors.toSet());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Integer rid : riderIds) {
            User rider = userMapper.getById(rid);
            if (rider == null || rider.getRiderStatus() == null || rider.getRiderStatus() != 1) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("id", rider.getId());
            item.put("name", rider.getName());
            item.put("rating", rider.getRating() != null ? rider.getRating() : 5.0);
            item.put("total_orders", rider.getTotalOrders() != null ? rider.getTotalOrders() : 0);
            schedules.stream().filter(s -> s.getRiderId().equals(rid)).findFirst()
                    .ifPresent(s -> item.put("location", s.getLocation()));
            result.add(item);
        }
        return result;
    }

    private JSONObject callAiRecommendation(Order order, List<Map<String, Object>> candidates) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderId", order.getId());
        requestBody.put("orderAmount", order.getAmount());
        requestBody.put("address", order.getAddress());
        requestBody.put("candidates", candidates);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:8082/assignment/recommend", request, String.class);

        JSONObject json = JSON.parseObject(response.getBody());
        if (json.getInteger("code") != null && json.getInteger("code") == 0) {
            return json.getJSONObject("data");
        }
        throw new RuntimeException("AI推荐接口异常: " + response.getBody());
    }
}

package fun.cyhgraph.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fun.cyhgraph.service.AssignmentAssistant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assignment")
@Slf4j
public class AssignmentController {

    @Autowired
    private AssignmentAssistant assignmentAssistant;

    @PostMapping("/recommend")
    public JSONObject recommend(@RequestBody Map<String, Object> request) {
        log.info("AI派单推荐请求: orderId={}", request.get("orderId"));

        StringBuilder prompt = new StringBuilder();
        prompt.append("【订单信息】\n");
        prompt.append("订单ID: ").append(request.get("orderId")).append("\n");
        prompt.append("配送地址: ").append(request.get("address")).append("\n");
        prompt.append("金额: ").append(request.get("orderAmount")).append("\n\n");

        prompt.append("【候选骑手列表】\n");
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) request.get("candidates");
        if (candidates != null) {
            for (int i = 0; i < candidates.size(); i++) {
                Map<String, Object> r = candidates.get(i);
                prompt.append(i + 1).append(". ");
                prompt.append("骑手ID=").append(r.get("id"));
                prompt.append(", 评分=").append(r.get("rating"));
                prompt.append(", 完成单数=").append(r.get("total_orders"));
                prompt.append(", 区域=").append(r.get("location"));
                prompt.append("\n");
            }
        }

        String result = assignmentAssistant.recommend(prompt.toString());
        log.info("AI推荐原始结果: {}", result);

        // AI可能返回分析文字+JSON混合内容，提取最后一个JSON
        int jsonStart = result.lastIndexOf("{");
        int jsonEnd = result.lastIndexOf("}");
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            result = result.substring(jsonStart, jsonEnd + 1);
        }
        log.info("AI推荐提取JSON: {}", result);

        JSONObject jsonResult = JSON.parseObject(result);
        JSONObject response = new JSONObject();
        response.put("code", 0);
        response.put("data", jsonResult);
        return response;
    }
}


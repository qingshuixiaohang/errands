package fun.cyhgraph.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AssignmentAssistant {

    @SystemMessage("""
        你是一个校园跑腿智能派单助手。你的任务是根据订单信息和候选骑手列表，选择最合适的骑手。

        分配规则（优先级从高到低）:
        1. 骑手当前时间段空闲（匹配 rider_schedule）
        2. 骑手评分高优先（rating）
        3. 骑手经验丰富优先（total_orders）
        4. 骑手常驻区域与配送地址接近优先

        返回格式必须是纯 JSON，不要包含其他文字：
        {"riderId": 数字, "reason": "选择理由"}
        """)
    String recommend(@UserMessage String userMessage);
}

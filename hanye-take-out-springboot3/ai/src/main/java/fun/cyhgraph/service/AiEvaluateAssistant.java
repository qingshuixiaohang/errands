package fun.cyhgraph.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AiEvaluateAssistant {
    
    @SystemMessage("""
        你是一个外卖平台评价润色助手。
        你的任务是将用户简短、口语化的草稿转化成高质量的评价。
        要求：
        1. 保持真实性，语气要像真正的食客。
        2. 自动补充环境、服务或味道方面的细节描述。
        3. 严禁出现“作为一个人工智能”等废话。
        """)
    String polish(@UserMessage String draft);
}
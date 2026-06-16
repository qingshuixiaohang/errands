package fun.cyhgraph;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.SystemMessage;
import fun.cyhgraph.config.RedisChatMemoryStory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisMemoryTest {

    @Autowired
    private RedisChatMemoryStory redisChatMemoryStory;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisConnection() {
        // 测试Redis连接
        stringRedisTemplate.opsForValue().set("test:key", "test:value");
        String value = stringRedisTemplate.opsForValue().get("test:key");
        assertEquals("test:value", value);
        System.out.println("Redis连接测试成功: " + value);

        // 清理测试数据
        stringRedisTemplate.delete("test:key");
    }

    @Test
    public void testChatMemoryStore() {
        String userId = "test_user_001";

        // 第一次获取消息，应该是空的
        List<ChatMessage> messages1 = redisChatMemoryStory.getMessages(userId);
        assertTrue(messages1.isEmpty());
        System.out.println("首次获取消息列表为空，正确");

        // 添加一些消息
        UserMessage userMessage = UserMessage.from("你好，我想了解你们的菜品");
        SystemMessage systemMessage = SystemMessage.from("You are a helpful restaurant assistant");

        // 创建消息列表
        java.util.ArrayList<ChatMessage> messageList = new java.util.ArrayList<>();
        messageList.add(userMessage);
        messageList.add(systemMessage);

        // 保存消息
        redisChatMemoryStory.updateMessages(userId, messageList);

        // 再次获取消息，应该有数据
        List<ChatMessage> messages2 = redisChatMemoryStory.getMessages(userId);
        assertFalse(messages2.isEmpty());
        assertEquals(2, messages2.size());
        System.out.println("获取到消息数量: " + messages2.size());

        // 检查Redis中的key
        String redisKey = "chat_memory" + userId;
        String jsonFromRedis = stringRedisTemplate.opsForValue().get(redisKey);
        System.out.println("Redis中的JSON数据: " + jsonFromRedis);
        assertNotNull(jsonFromRedis);

        // 删除消息
        redisChatMemoryStory.deleteMessages(userId);

        // 验证消息已被删除
        List<ChatMessage> messages3 = redisChatMemoryStory.getMessages(userId);
        assertTrue(messages3.isEmpty());
        System.out.println("消息删除成功");
    }
}
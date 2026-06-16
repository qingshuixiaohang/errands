# LangChain4j 使用教程

## 目录
- [项目概述](#项目概述)
- [环境配置](#环境配置)
- [核心组件](#核心组件)
- [详细步骤说明](#详细步骤说明)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

## 项目概述

本项目是一个基于Spring Boot 3.2.5和LangChain4j构建的AI对话系统，实现了以下功能：
- 用户对话记忆存储（Redis）
- 多用户会话隔离
- RESTful API接口
- 简洁的前端交互界面

## 环境配置

### 1. Maven依赖配置 (pom.xml)

```xml
<!-- LangChain4j BOM管理 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-bom</artifactId>
            <version>1.0.0-beta3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 核心依赖 -->
<dependencies>
    <!-- LangChain4j核心 -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-core</artifactId>
    </dependency>
    
    <!-- OpenAI集成 -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Spring Boot集成 -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Redis记忆存储 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
</dependencies>
```

### 2. 应用配置 (application.yml)

```yaml
# OpenAI配置
langchain4j:
  openai:
    apiKey: ${OPENAI_API_KEY:your-api-key}
    baseUrl: ${OPENAI_BASE_URL:https://api.openai.com}
    model: ${OPENAI_MODEL:gpt-3.5-turbo}
    
# Redis配置
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 3. 主类配置

```java
@SpringBootApplication
@EnableAiService  // 启用AI服务功能
@EnableCaching    // 启用缓存
@EnableScheduling // 启用定时任务
@Slf4j
public class AiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
        log.info("server started successfully!");
    }
}
```

## 核心组件

### 1. 核心注解说明

#### @AiService
- **作用**：标记接口为AI服务接口
- **位置**：接口声明上
- **功能**：让LangChain4j为该接口生成实现

```java
@AiService
public interface Assistant {
    // AI服务方法
}
```

#### @SystemMessage
- **作用**：定义AI的系统角色和初始指令
- **位置**：方法参数前
- **功能**：设定AI的行为模式和知识库

```java
@SystemMessage("You are a polite assistant")
String chat(@MemoryId String userId, @UserMessage String userMessage);
```

#### @UserMessage
- **作用**：标记用户输入的消息
- **位置**：方法参数前
- **功能**：识别需要处理的用户输入

#### @MemoryId
- **作用**：标识用户ID，用于会话记忆
- **位置**：方法参数前
- **功能**：实现多用户会话隔离

### 2. 记忆存储实现

#### RedisChatMemoryStory.java
```java
@Component
public class RedisChatMemoryStory implements ChatMemoryStore {

    private static final String KEY_MEMORY_PREFIX = "chat_memory";
    
    private final StringRedisTemplate stringRedisTemplate;
    
    // 构造器注入
    public RedisChatMemoryStory(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryID) {
        String key = KEY_MEMORY_PREFIX + memoryID;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String key = KEY_MEMORY_PREFIX + memoryId;
        // 序列化消息并存储，设置7天过期
        stringRedisTemplate.opsForValue().set(
            key, 
            ChatMessageSerializer.messagesToJson(messages), 
            7, TimeUnit.DAYS
        );
    }

    @Override
    public void deleteMessages(Object memoryId) {
        String key = KEY_MEMORY_PREFIX + memoryId;
        stringRedisTemplate.delete(key);
    }
}
```

### 3. 配置类

#### AiConfig.java
```java
@Configuration
public class AiConfig {

    @Autowired
    private RedisChatMemoryStory redisChatMemoryStory;

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)  // 最多保存20条消息
                        .chatMemoryStore(redisChatMemoryStory)
                        .build();
            }
        };
    }
}
```

## 详细步骤说明

### 步骤1：创建AI服务接口

1. 创建服务接口类
```java
package fun.cyhgraph.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {
    
    @SystemMessage("你是一个专业的外卖餐厅助手，能够帮助用户了解菜单、推荐菜品、处理订单等")
    String chat(
        @MemoryId String userId,      // 用户ID，用于会话隔离
        @UserMessage String userMessage  // 用户输入的消息
    );
}
```

2. **作用说明**：
   - `@AiService`：告诉LangChain4j这是一个AI服务接口
   - `@SystemMessage`：设定AI的角色和行为准则
   - `@MemoryId`：每个用户的独立会话ID
   - `@UserMessage`：用户输入的消息内容

### 步骤2：实现记忆存储

1. 实现ChatMemoryStore接口
```java
@Component
public class RedisChatMemoryStory implements ChatMemoryStore {
    
    // 使用Redis存储聊天记录
    // key格式: chat_memory{userId}
    // value: 序列化的JSON消息列表
}
```

2. **关键点**：
   - 每个用户的对话数据独立存储
   - 使用JSON序列化存储消息
   - 设置合理的过期时间（如7天）

### 步骤3：配置记忆提供者

1. 创建ChatMemoryProvider
```java
@Bean
public ChatMemoryProvider chatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(20)  // 窗口大小
            .chatMemoryStore(redisChatMemoryStory)
            .build();
}
```

2. **参数说明**：
   - `maxMessages`：保留的消息数量
   - `chatMemoryStore`：自定义的存储实现

### 步骤4：创建API控制器

```java
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private Assistant assistant;

    @PostMapping("/ai")
    public ResponseEntity<String> chatAi(
            @RequestParam String userId,
            @RequestParam String message) {
        
        // 参数验证
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(message)) {
            return ResponseEntity.badRequest().body("用户ID和消息不能为空");
        }
        
        try {
            // 调用AI服务
            String response = assistant.chat(userId, message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI服务错误: " + e.getMessage());
        }
    }
}
```

### 步骤5：前端界面集成

```html
<!-- 简单的聊天界面 -->
<div class="chat-container">
    <div id="messages"></div>
    <div class="input-group">
        <input type="text" id="messageInput" placeholder="输入消息...">
        <button onclick="sendMessage()">发送</button>
    </div>
</div>

<script>
async function sendMessage() {
    const userId = "user123";
    const message = document.getElementById('messageInput').value;
    
    try {
        const response = await fetch(`/chat/ai?userId=${userId}&message=${encodeURIComponent(message)}`);
        const data = await response.text();
        
        // 显示消息
        addMessage('user', message);
        addMessage('ai', data);
    } catch (error) {
        console.error('Error:', error);
    }
}
</script>
```

## 最佳实践

### 1. 系统消息设计
```java
@SystemMessage("""
你是一个专业的外卖餐厅助手，具有以下能力：
1. 推荐合适的菜品
2. 回答菜品相关问题
3. 处理用户订单
4. 记住用户的偏好

回答要求：
- 语气友好、专业
- 基于实际情况回答
- 不编造不存在的信息
""")
```

### 2. 错误处理
```java
@PostMapping("/ai")
public ResponseEntity<String> chatAi(@RequestParam String userId, 
                                  @RequestParam String message) {
    
    // 参数校验
    if (userId == null || userId.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("用户ID不能为空");
    }
    
    try {
        String response = assistant.chat(userId.trim(), message);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("AI服务调用失败", e);
        return ResponseEntity.internalServerError().body("服务暂时不可用，请稍后重试");
    }
}
```

### 3. 记忆优化
```java
// 在AiConfig中配置
@Bean
public ChatMemoryProvider chatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .maxMessages(10)  // 根据业务需求调整
            .chatMemoryStore(redisChatMemoryStory)
            .build();
}
```

### 4. 安全配置
```yaml
# 在application.yml中配置
langchain4j:
  openai:
    apiKey: ${OPENAI_API_KEY}  # 从环境变量读取
    model: gpt-3.5-turbo
```

## 常见问题

### 1. Q: 对话记录没有保存到Redis？
**A**: 检查以下配置：
- 主类添加了`@EnableAiService`
- Redis连接正常
- ChatMemoryProvider正确配置

### 2. Q: 多用户会话混乱？
**A**: 确保：
- 每个请求都传入正确的userId
- `@MemoryId`参数不为空
- Redis key包含userId

### 3. Q: AI回复内容不理想？
**A**: 优化：
- 改进`@SystemMessage`的提示词
- 调整窗口大小（maxMessages）
- 使用更强大的模型

### 4. Q: Redis连接失败？
**A**: 检查：
- Redis服务是否启动
- 连接配置是否正确
- 网络是否可达

### 5. Q: 如何增加更多功能？
**A**: 扩展：
1. 使用`@Tool`注解添加工具调用
2. 实现`ChatMemoryStore`的其他存储方式
3. 添加图片、文件等多模态支持
4. 集成RAG功能提升回答质量

## 总结

LangChain4j通过简单的注解和配置，就能快速构建强大的AI应用。本教程涵盖了从基础配置到高级功能的完整实现，希望对你的AI开发有所帮助。
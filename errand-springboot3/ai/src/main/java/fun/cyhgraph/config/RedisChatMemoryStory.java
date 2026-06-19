package fun.cyhgraph.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * makise
 * </p>
 *
 * @author makise
 * @since 2026/4/9
 */
@Component
public class RedisChatMemoryStory implements ChatMemoryStore {

    final StringRedisTemplate stringRedisTemplate;

    public RedisChatMemoryStory(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

   private static final String key_memory="chat_memory";

    @Override
    public List<ChatMessage> getMessages(Object memoryID) {
        String key=key_memory+memoryID;
        String json = stringRedisTemplate.opsForValue().get(key);
        if(json==null || json.isEmpty()){
            return new ArrayList<>();
        }
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        String key=key_memory+memoryId;
    //存进redis
        stringRedisTemplate.opsForValue().set(key, ChatMessageSerializer.messagesToJson(list),7, TimeUnit.DAYS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        //直接删除key
        stringRedisTemplate.delete(key_memory+memoryId);
    }
}

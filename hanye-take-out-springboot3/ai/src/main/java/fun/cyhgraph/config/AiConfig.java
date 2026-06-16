package fun.cyhgraph.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import fun.cyhgraph.service.AiEvaluateAssistant;
import fun.cyhgraph.service.Assistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Configuration
public class AiConfig {

    @Autowired
    private RedisChatMemoryStory redisChatMemoryStory; // 注入您的Redis实现


    @Bean
    public Executor ragExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        //队列容量
        executor.setQueueCapacity(20);
        executor.initialize();
        return executor;
    }


    //显示绑定，虽然只有一个AIService的情况下会自动注入绑定接口
    @Bean
    public Assistant aiAssistant(ChatLanguageModel model,
                                 RetrievalAugmentor augmenter   ,
                                 ChatMemoryProvider memory) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .retrievalAugmentor(augmenter)
                .chatMemoryProvider(memory)
                .build();
    }
    //多个业务接口就配置多个bean助手
    @Bean
    public AiEvaluateAssistant aiEvaluateAssistant(ChatLanguageModel model) {
        return AiServices.builder(AiEvaluateAssistant.class)
                .chatLanguageModel(model)
                // 这里不需要配置 memory 和 augmentor
                .build();
    }

//    //这个在配置文件中可以自动生成
//    @Bean
//    public ChatLanguageModel chatLanguageModel() {
//        return ZhipuAiChatModel.builder()
//                .apiKey("你的API_KEY") // 替换成你真实的 KEY
//                .model("glm-4")       // 或者 glm-4-flash
//                .build();
//    }

    /**
     * 显式声明使用本地的 384 维 EmbeddingModel
     * 确保整个应用只使用这一个 EmbeddingModel，避免维度不匹配
     */
    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        return new dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {

        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStory)
                        .build();
            }
        };
    }


    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return RedisEmbeddingStore.builder()
                .host("127.0.0.1")
                .port(6380)
                .dimension(384)
                .indexName("Embedding")//类似表名
                .build();
    }

    //user提问时的菜单检索器
    //把这个所有retriever都写成内部方法，router暴露成bean就可以
    private ContentRetriever menuRetriever(EmbeddingStore<TextSegment> embeddingStore,EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .filter(metadataKey("document_type").isEqualTo("menu"))
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build();
    }

    @Bean
    public WebSearchEngine tavilyWebSearchEngine(){
        return TavilyWebSearchEngine.builder()
                .apiKey("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")
                .build();
    }

    //同上
    private ContentRetriever webRetriever(WebSearchEngine tavilyWebSearchEngine){
        return WebSearchContentRetriever.builder()
                .webSearchEngine(tavilyWebSearchEngine)
                .maxResults(1)
                .build();
    }
    //查询转换器:将用户的口语化的追问通过结合上文改成完整的
    @Bean
    public QueryTransformer queryTransformer(ChatLanguageModel model) {
        //这个是真正的转换器
        return new CompressingQueryTransformer(model);
    }

    //检索器路由
    @Bean
    public QueryRouter queryRouter(WebSearchEngine tavilyWebSearchEngine,EmbeddingStore<TextSegment> embeddingStore,
                                   EmbeddingModel embeddingModel) {
        //默认实现是每一个检索都会走（当retriever少的时候用）
        ContentRetriever menu=menuRetriever(embeddingStore,embeddingModel);
        ContentRetriever web=webRetriever(tavilyWebSearchEngine);
        return new DefaultQueryRouter(menu);
      //通过LLM来路由
        //  return new LanguageModelQueryRouter();
    }


    //总管家:负责一个完整的"检索-增强"闭环，包括：查询优化、路由分发、内容检索、内容聚合、结果注入
    @Bean
    public RetrievalAugmentor retrievalAugmentor(QueryTransformer queryTransformer, QueryRouter queryRouter,Executor ragExecutor) {
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .queryRouter(queryRouter)
                //传入线程池，这个会自动把一些retriever包装成callable任务
                .executor(ragExecutor)
                .build();
    }

}
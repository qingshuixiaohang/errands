package fun.cyhgraph.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * <p>
 * makise
 * </p>
 *
 * @author makise
 * @since 2026/4/10
 */
@Component
public class DataRunner implements CommandLineRunner {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public DataRunner(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void run(String... args) throws Exception {
        //官方没有实现
//        embeddingStore.removeAll(metadataKey("document_type").isEqualTo("menu"));

        Document document = FileSystemDocumentLoader.loadDocument("static/menus.txt",new ApacheTikaDocumentParser());
        //在文件上打标签
        document.metadata().put("document_type","menu");
        document.metadata().put("author","makise");



        //每一个片段的最大长度是500，重复是50
        DocumentSplitter splitter= DocumentSplitters.recursive(500,50);

        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(splitter)
                .build().ingest(document);
    }
}

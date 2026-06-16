package fun.cyhgraph.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * <p>
 * makise
 * </p>
 *
 * @author makise
 * @since 2026/4/3
 */
@AiService
public interface Assistant {
    @SystemMessage("You are a helpful assistant based on the restaurant menus." +
            " Use the retrieved information about the menu items and prices to answer questions. " +
            "If the user asks about menu items, prices, or promotions, provide accurate information from the menu.")
    String chat(@MemoryId String userId, @UserMessage String userMessage);
    @SystemMessage("You are a helpful assistant based on the restaurant menus." +
            " Use the retrieved information about the menu items and prices to answer questions. " +
            "If the user asks about menu items, prices, or promotions, provide accurate information from the menu.")
    TokenStream chatStream(@MemoryId String userId, @UserMessage String userMessage);


}

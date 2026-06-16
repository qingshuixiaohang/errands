package fun.cyhgraph.controller;
import fun.cyhgraph.service.Assistant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
@CrossOrigin
@RestController
public class ChatController {

    @Autowired
    Assistant assistant;

    @GetMapping("/chatAi/stream")
    public SseEmitter chat(@RequestParam String userId, @RequestParam String message) {
            //创建发射器，设置 5 分钟超时
        SseEmitter emitter = new SseEmitter(300000L);
        assistant.chatStream(userId, message)
                .onRetrieved(source ->{
                    System.out.println("【系统提示：为您检索到 " + source.size() + " 条相关资料】");
                })
                .onNext(token ->{
                    try {
                        emitter.send(token);
                    } catch (IOException e) {
                        // emitter 已关闭，忽略后续 token
                    }
                })
                .onComplete(result -> {
                    try {
                        emitter.complete();
                    } catch (Exception e) {
                        // 忽略 complete 异常
                    }
                    System.out.println("【AI 流式响应完成】");
                })
                .onError(error -> {
                    try {
                        emitter.send("\n\n【系统抱歉：服务开小差了 (" + error.getMessage() + ")】");
                    } catch (IOException e) {
                        // emitter 已关闭，忽略
                    }
                })
                .start();
        return emitter;
    }

    /**
     * 非流式接口（给微信小程序使用，uni.request 不支持 SSE）
     */
    @GetMapping("/chatAi")
    public String chatSync(@RequestParam String userId, @RequestParam String message) {
        return assistant.chat(userId, message);
    }

}

package com.zk.miaosha.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theokanning.openai.service.OpenAiService.*;

@RestController
public class ChatGPTController {


    @RequestMapping("/ask")
    public String ask(String question){
        ObjectMapper mapper = defaultObjectMapper();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
        OkHttpClient client = defaultClient("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", Duration.ofSeconds(30))
                .newBuilder()
                .proxy(proxy)
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api);

        List<ChatMessage> chatList=new ArrayList<>();
        ChatMessage newMessage=new ChatMessage();
        newMessage.setRole("user");
        newMessage.setContent(question);
        chatList.add(newMessage);
        ChatCompletionRequest chatCompletionRequest= ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(chatList)
                .build();
        ChatCompletionResult completionResult=service.createChatCompletion(chatCompletionRequest);

        String text = completionResult.getChoices().get(0).getMessage().getContent();
        Map<String, String> map = new HashMap<>();
        map.put("ans", text);
        ObjectMapper om = new ObjectMapper();
        String s = "请求超时...";
        try {
            s = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }
}

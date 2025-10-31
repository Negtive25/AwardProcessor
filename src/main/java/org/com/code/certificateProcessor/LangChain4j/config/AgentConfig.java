package org.com.code.certificateProcessor.LangChain4j.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import org.com.code.certificateProcessor.LangChain4j.agent.ClassificationAgent;
import org.com.code.certificateProcessor.LangChain4j.agent.OCRAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AgentConfig {
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    public static final int DimensionOfEmbeddingModel = 768;

    public static final String textEmbeddingModel = "text-embedding-v4";
    public static final String qwen3VLFlash = "qwen3-vl-flash";
    public static final String qwenFlash = "qwen-flash";

    public Builder builder(){
        return new Builder(apiKey);
    }


    public static class Builder{
        private final String apiKey;
        private ChatModel chatModel;

        public Builder(String apiKey){
            this.apiKey = apiKey;
        }

        public Builder chatModel(String model){
            ChatRequestParameters defaultRequestParameters = ChatRequestParameters.builder()
                    .responseFormat(ResponseFormat.JSON)
                    .build();

            this.chatModel=QwenChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(model)
                    .defaultRequestParameters(defaultRequestParameters)
                    .maxTokens(4096)
                    .build();

            return this;
        }

        public ClassificationAgent buildClassificationAgent(){
            return AiServices.builder(ClassificationAgent.class)
                    .chatModel(chatModel)
                    .build();
        }

        public OCRAgent buildOCRAgent(){
            return AiServices.builder(OCRAgent.class)
                    .chatModel(chatModel)
                    .build();
        }


        public EmbeddingModel embeddingModel(String embeddingModel){
            return QwenEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName(embeddingModel)
                    .dimension(DimensionOfEmbeddingModel)
                    .build();
        }
    }
}

package org.com.code.certificateProcessor.LangChain4j.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import lombok.Getter;
import org.com.code.certificateProcessor.ElasticSearch.ESConst;
import org.com.code.certificateProcessor.LangChain4j.agent.ClassificationAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AgentConfig {
    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    public static final int DimensionOfEmbeddingModel = ESConst.Vector_DIM;

    public static final String textEmbeddingModel = "text-embedding-v4";
    public static final String qwen3VLFlash = "qwen3-vl-flash";
    public static final String qwenFlash = "qwen-flash";

    public Builder builder(){
        return new Builder(apiKey);
    }


    public static class Builder{
        private final String apiKey;
        private ChatModel model;

        public Builder(String apiKey){
            this.apiKey = apiKey;
        }

        public Builder chatModel(String model){
            this.model =QwenChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(model)
                    .maxTokens(4096)
                    .build();

            return this;
        }

        public ClassificationAgent buildClassificationAgent(){
            return AiServices.builder(ClassificationAgent.class)
                    .chatModel(model)
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
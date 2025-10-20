package org.com.code.certificateProcessor.LangChain4j.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import org.com.code.certificateProcessor.LangChain4j.agent.ClassificationAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AgentConfig {
    @Autowired
    @Qualifier("ChatModel")
    private QwenChatModel qwenChatModel;

    @Bean("ClassificationAgent")
    public ClassificationAgent classificationAgent(){
        return AiServices.builder(ClassificationAgent.class)
                .chatLanguageModel(qwenChatModel)
                .build();
    }
}
